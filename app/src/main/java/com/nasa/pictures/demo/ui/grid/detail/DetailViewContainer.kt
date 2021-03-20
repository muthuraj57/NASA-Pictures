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
import androidx.annotation.CheckResult
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
import com.nasa.pictures.demo.ui.grid.detail.viewPager.DetailViewPager
import com.nasa.pictures.demo.ui.grid.detail.viewPager.DetailViewPagerAdapter
import com.nasa.pictures.demo.ui.grid.shared.DataAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Created by Muthuraj on 17/03/21.
 */
@AndroidEntryPoint
class DetailViewContainer : LinearLayout {
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
        layoutManager = IndicatorLayoutManager(context)

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

    @Inject
    lateinit var dataAdapterFactory: DataAdapter.Factory

    fun setData(data: List<Data>, onIndicatorItemSelected: (position: Int) -> Unit) {
        indicatorList.adapter = dataAdapterFactory.create(data, true) { clickedPosition ->
            //Open corresponding detail view for clicked indicator item.
            detailViewPager.setCurrentItem(clickedPosition, true)
        }
        detailViewPager.setAdapter(DetailViewPagerAdapter(data))

        this.onIndicatorItemSelected = onIndicatorItemSelected
        setupListeners()
    }

    private var onIndicatorItemSelected: ((position: Int) -> Unit)? = null


    //Listeners, item decorators should be added one time alone. This check helps with that.
    private var isSetupDone = false

    private fun setupListeners() {
        if (isSetupDone) {
            return
        }
        isSetupDone = true

        //Max width and height the indicator item should achieve when for selected position.
        val targetHeight =
            resources.getDimensionPixelSize(R.dimen.horizontal_current_image_height)

        //Extra space between each indicator items.
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

                //If offset not 0, then we should evaluate with next position (Ref: onPageScrolled
                //method's documentation).
                var position = if (positionOffset == 0f) _position else _position + 1

                val isScrollingFromLeftToRight =
                    when (position) {
                        previousPosition -> positionOffset > previousOffset
                        else -> position > previousPosition
                    }
                if (previousPosition != -1 && positionOffset != 0f && isScrollingFromLeftToRight.not()) {
                    //When scrolling from right to left, the position we get is not the left most one.
                    //So subtract one from position to get actual left most position where we should
                    //scale the indicator view.
                    position--
                }

                val view = topListLayoutManager.findViewByPosition(position)
                if (view != null) {
                    val imageView = view.findViewById<ImageView>(R.id.imageView)

                    if (positionOffset == 0f) {
                        //Scroll is settled and this position is the selected one. Scale and set width
                        //with end value.
                        view.scaleX = targetHeight.toFloat() / imageView.height
                        view.scaleY = targetHeight.toFloat() / imageView.height
                        view.updateLayoutParams<ViewGroup.LayoutParams> {
                            width = targetHeight + itemHorizontalMargin
                        }
                    } else {
                        //Scroll in progress. Update scale factor and width by including position offset.

                        //Offset height difference by current position change for right to left scroll case.
                        //Add this to current width to give space for the scale factor.
                        val offset =
                            when {
                                positionOffset != 0f && previousPosition != -1 && isScrollingFromLeftToRight.not() -> 1f - positionOffset
                                else -> positionOffset
                            }
                        val delta = (targetHeight - imageView.height) * offset
                        view.updateLayoutParams<ViewGroup.LayoutParams> {
                            width = imageView.width + delta.roundToInt() + itemHorizontalMargin
                        }

                        val scaleFactor =
                            if (offset == 0f) targetHeight.toFloat() / imageView.height
                            else (imageView.height + (delta)) / imageView.height
                        if (scaleFactor.isFinite()) {
                            view.scaleX = scaleFactor
                            view.scaleY = scaleFactor
                        }
                    }
                }

                //To update scale and width for adjacent views.
                indicatorList
                    .children
                    .filter { it != view } //Filter out current view since we already worked out for it.
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
                            val offset = when {
                                isForRightSideItem -> positionOffset //Offset already decreasing from 1 to 0.
                                else -> 1f - positionOffset //Invert the offset.
                            }
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
                            }
                        } else {
                            itemView.scaleX = 1f
                            itemView.scaleY = 1f
                            itemView.updateLayoutParams<ViewGroup.LayoutParams> {
                                width = imageView.width + itemHorizontalMargin
                            }
                        }
                    }

                previousPosition =
                    when {
                        previousPosition != -1 && positionOffset != 0f && isScrollingFromLeftToRight.not() -> {
                            /*
                            * When scrolling from right to left, don't update previous position value
                            * with current position since this value will be wrong. The position value
                            * will be for the right most item even when scrolling towards left.
                            * */
                            previousPosition
                        }
                        else -> position
                    }
                previousOffset = positionOffset
            }
        })

        //When user scrolls the indicator list, view's scale and width should be reset.
        //And when user comes back to the current detail item's position, we should update proper
        //scale and width for corresponding view. These are done using ItemDecoration.
        //
        //getItemOffsets is usually called for each layout pass for all recyclerView children,
        //which is a perfect hook to do this job.
        indicatorList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val imageView = view.findViewById<ImageView>(R.id.imageView)
                if (currentVisibleDetailItemPosition != -1 &&
                    currentVisibleDetailItemPosition == parent.getChildAdapterPosition(view)
                ) {
                    //This view is the current detail view. Update full scale and width.
                    val scaleFactor = targetHeight.toFloat() / imageView.height
                    if (scaleFactor.isFinite()) {
                        view.scaleX = targetHeight.toFloat() / imageView.height
                        view.scaleY = targetHeight.toFloat() / imageView.height
                    }
                    view.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = targetHeight + itemHorizontalMargin
                    }
                } else {
                    //Reset values.
                    view.scaleX = 1f
                    view.scaleY = 1f
                    view.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = imageView.width + itemHorizontalMargin
                    }
                }
            }
        })
    }

    /**
     * Call this to open detail view for the particular position. This will return [OnBackPressedCallback]
     * which should be added to the activity's back pressed dispatcher. It is used to intercept first
     * back press action and dismiss the detail view from there.
     * */
    @CheckResult
    fun openDetail(clickedPosition: Int, onBackPressed: () -> Unit): OnBackPressedCallback {
        indicatorList.scrollToPosition(clickedPosition)
        detailViewPager.setCurrentItem(clickedPosition, false)

        visibility = View.VISIBLE

        indicatorList.scaleX = 0f
        indicatorList.alpha = 0f
        indicatorList.animate()
            .scaleX(1f)
            .alpha(1f)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(500)
            .start()

        return object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeDetail()
                onBackPressed()

                //This callbacks purpose is over, remove it.
                remove()
            }
        }
    }

    private fun closeDetail() {
        detailViewPager.animate()
            .setInterpolator(FastOutSlowInInterpolator())
            .alpha(0f)
            .setDuration(400)
            .setUpdateListener {
                background.alpha = ((1f - it.animatedFraction) * 255).roundToInt()
            }
            .withEndAction {
                visibility = View.INVISIBLE
                detailViewPager.alpha = 1f
                background.alpha = 255
            }
            .start()

        indicatorList.scaleX = 1f
        indicatorList.alpha = 1f
        indicatorList.animate()
            .scaleX(0f)
            .alpha(0f)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(500)
            .start()
    }

    fun onGridViewScrolled(position: Int) {
        indicatorList.scrollToPosition(position)
    }

    fun findDetailImage(transitionName: String): ImageView? {
        return detailViewPager.findDetailImage(transitionName)
    }

    fun getCurrentItemData() = detailViewPager.getCurrentItemData()
}