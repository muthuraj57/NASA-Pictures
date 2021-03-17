/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.adapter.DataAdapter
import com.nasa.pictures.demo.ui.grid.adapter.DetailViewAdapter
import com.nasa.pictures.demo.ui.grid.adapter.HorizontalLayoutManager
import com.nasa.pictures.demo.util.log
import com.nasa.pictures.demo.util.logE
import kotlin.math.roundToInt

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val indicatorList = RecyclerView(context).apply {
        val height = resources.getDimensionPixelSize(R.dimen.horizontal_image_height)
        val horizontalMargin = resources.getDimensionPixelSize(R.dimen.grid_item_gap)
        val verticalMargin =
            resources.getDimensionPixelSize(R.dimen.horizontal_image_list_vertical_margin)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height).apply {
            updateMargins(
                left = horizontalMargin,
                right = horizontalMargin,
                top = verticalMargin,
                bottom = verticalMargin
            )
        }
        layoutManager = HorizontalLayoutManager(context)

        //Needed since we scale currently visible detail item from this list and that view's bounds
        //might come outside it's parent's bound.
        clipToPadding = false
        clipChildren = false
    }

    private val detailViewPager = DetailViewPager(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f).apply {
            updateMargins(bottom = resources.getDimensionPixelSize(R.dimen.horizontal_image_list_vertical_margin))
        }
    }

    init {
        //Don't show it initially.
        visibility = View.INVISIBLE

        setBackgroundColor(ContextCompat.getColor(context, R.color.detail_background))

        orientation = VERTICAL

        addView(detailViewPager)
        addView(indicatorList)

        //Needed to scale current visible detail item outside of it's view bounds.
        clipToPadding = false
        clipChildren = false
    }

    fun setData(data: List<Data>, onIndicatorItemSelected: (position: Int) -> Unit) {
        indicatorList.adapter = DataAdapter(data, true) { clickedPosition, _ ->
            detailViewPager.setCurrentItem(clickedPosition, true)
        }
        detailViewPager.setAdapter(DetailViewAdapter(data))

        this.onIndicatorItemSelected = onIndicatorItemSelected
        setupListeners()
    }

    private var onIndicatorItemSelected: ((position: Int) -> Unit)? = null


    private var isSetupDone = false
    private fun setupListeners() {
        if (isSetupDone) {
            return
        }
        isSetupDone = true

        val targetHeight =
            resources.getDimensionPixelSize(R.dimen.horizontal_current_image_height)
        val itemHorizontalMargin =
            resources.getDimensionPixelSize(R.dimen.horizontal_image_list_gap)
        val topListLayoutManager =
            indicatorList.layoutManager as LinearLayoutManager

        //Used to scale top list view for currently selected detail view from item decorator.
        var currentVisibleDetailItemPosition: Int = -1

        detailViewPager.registerPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentVisibleDetailItemPosition = position
                onIndicatorItemSelected?.invoke(position)
                indicatorList.smoothScrollToPosition(position)
            }

            private var previousPosition = -1
            private var previousOffset = 0f

            override fun onPageScrolled(
                _position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                log { "onPageScrolled() called with: _position = [$_position], positionOffset = [$positionOffset], positionOffsetPixels = [$positionOffsetPixels]" }
                var position = if (positionOffset == 0f) _position else _position + 1
                val isScrollingFromLeftToRight =
                    when (position) {
                        previousPosition -> {
                            log { "onPageScrolled() called with isScrollingFromLeftToRight position and previousPosition are equal" }
                            positionOffset > previousOffset
                        }
                        else -> {
                            logE { "onPageScrolled() called with isScrollingFromLeftToRight position not equal position: $position, previousPosition: $previousPosition" }
                            position > previousPosition
                        }
                    }
                log { "onPageScrolled() called with isScrollingFromLeftToRight $isScrollingFromLeftToRight, previousPosition = [$previousPosition]" }
                if (previousPosition != -1 && positionOffset != 0f && isScrollingFromLeftToRight.not()) {
                    position--
                }

                val view = topListLayoutManager.findViewByPosition(position)
                if (view != null) {
                    val imageView = view.findViewById<ImageView>(R.id.imageView)

                    if (positionOffset == 0f) {
                        view.scaleX = targetHeight.toFloat() / imageView.height
                        view.scaleY = targetHeight.toFloat() / imageView.height
                        view.updateLayoutParams<ViewGroup.LayoutParams> {
                            width = targetHeight + itemHorizontalMargin
                        }
                        this@DetailView.logE { "onPageScrolled() width reset 1 scaleX: ${view.scaleX}, ${imageView.transitionName}" }
                    } else {

                        //Height difference offset by current position change. Add this to current width
                        //to give space for the scale factor.
                        val offset =
                            if (positionOffset != 0f && previousPosition != -1 && isScrollingFromLeftToRight.not()) {
                                1f - positionOffset
                            } else {
                                positionOffset
                            }
                        val delta = (targetHeight - imageView.height) * offset
                        view.updateLayoutParams<ViewGroup.LayoutParams> {
                            width = imageView.width + delta.roundToInt() + itemHorizontalMargin
                        }

                        val scaleFactor =
                            if (offset == 0f) targetHeight.toFloat() / imageView.height
                            else (imageView.height + (delta)) / imageView.height
                        if (scaleFactor.isFinite()) {
                            this@DetailView.log { "onPageScrolled() called with: position = [$position], positionOffset = [$positionOffset], targetHeight = [$targetHeight], imageView.height = [${imageView.height}], scaleFactor = [$scaleFactor]" }
                            view.scaleX = scaleFactor
                            view.scaleY = scaleFactor
                            this@DetailView.logE { "onPageScrolled() width reset 2 scaleX: ${scaleFactor}, ${imageView.transitionName}, position: $position, positionOffset: $positionOffset, previousPosition: $previousPosition, isScrollingFromLeftToRight: $isScrollingFromLeftToRight" }
                        }
                    }
                }

                indicatorList
                    .children
                    .filter { it != view }
                    .forEach { itemView ->

                        val adapterPosition =
                            indicatorList.getChildAdapterPosition(itemView)
                        val imageView = itemView.findViewById<ImageView>(R.id.imageView)

                        val canCheckForSides = positionOffset != 0f && previousPosition != -1
                        val isForLeftSideItem =
                            isScrollingFromLeftToRight && adapterPosition == position - 1
                        val isForRightSideItem =
                            isScrollingFromLeftToRight.not() && (adapterPosition == position + 1 || adapterPosition == position)

                        if (canCheckForSides && (isForLeftSideItem || isForRightSideItem)) {
                            this@DetailView.logE { "onPageScrolled() width scale isScrollingFromLeftToRight: [$isScrollingFromLeftToRight], adapterPosition: [$adapterPosition], position: [$position]" }
                            val offset = if (isForRightSideItem) {
                                if (adapterPosition == position + 1) {
                                    positionOffset
                                } else {
                                    positionOffset
                                }
                            } else {
                                1f - positionOffset
                            }
//                            val offset = 1f - positionOffset
                            val delta = (targetHeight - imageView.height) * offset
                            itemView.updateLayoutParams<ViewGroup.LayoutParams> {
                                width = imageView.width + delta.roundToInt() + itemHorizontalMargin
                            }
                            val scaleFactor =
                                if (offset == 0f) targetHeight.toFloat() / imageView.height
                                else (imageView.height + (delta)) / imageView.height
                            if (scaleFactor.isFinite()) {
                                itemView.scaleX = scaleFactor
                                itemView.scaleY = scaleFactor
                                this@DetailView.logE { "onPageScrolled() width reset 3 scaleX: ${itemView.scaleX}, ${imageView.transitionName}" }
                            }
                        } else {
                            itemView.scaleX = 1f
                            itemView.scaleY = 1f
                            itemView.updateLayoutParams<ViewGroup.LayoutParams> {
                                width = imageView.width + itemHorizontalMargin
                            }
                            this@DetailView.logE { "onPageScrolled() width reset 4 scaleX: ${itemView.scaleX}, ${imageView.transitionName}" }
                        }
                    }

                previousPosition =
                    when {
                        previousPosition != -1 && positionOffset != 0f && isScrollingFromLeftToRight.not() -> previousPosition
                        else -> position
                    }
                previousOffset = positionOffset
            }
        })

        indicatorList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                if (currentVisibleDetailItemPosition != -1 &&
                    currentVisibleDetailItemPosition == parent.getChildAdapterPosition(view)
                ) {
                    val imageView = view.findViewById<ImageView>(R.id.imageView)
                    val scaleFactor = targetHeight.toFloat() / imageView.height
                    if (scaleFactor.isFinite()) {
                        view.scaleX = targetHeight.toFloat() / imageView.height
                        view.scaleY = targetHeight.toFloat() / imageView.height
                    }
                    view.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = targetHeight + itemHorizontalMargin
                    }
                }
            }
        })
    }

    fun getVisibleIndicatorImageViews() = indicatorList.children
        .map { it.findViewById<ImageView>(R.id.imageView) }
        .toList()

    fun openDetail(clickedPosition: Int): OnBackPressedCallback {
        indicatorList.scrollToPosition(clickedPosition)
        detailViewPager.setCurrentItem(clickedPosition, false)

        visibility = View.VISIBLE

        detailViewPager.translationY = -1000f
        detailViewPager.alpha = 0f
        detailViewPager.animate()
            .translationY(0f)
            .alpha(1f)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(500)
            .start()

        return object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeDetail()

                //This callbacks task is finished, remove it.
                remove()
            }
        }
    }

    private fun closeDetail() {
        detailViewPager.animate()
            .translationY(-1000f)
            .setInterpolator(FastOutSlowInInterpolator())
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                visibility = View.INVISIBLE
                detailViewPager.alpha = 1f
            }
            .start()
    }

    fun onGridViewScrolled(position: Int) {
        indicatorList.scrollToPosition(position)
    }

}