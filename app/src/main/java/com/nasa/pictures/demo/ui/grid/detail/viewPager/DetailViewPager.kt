/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail.viewPager

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.nasa.pictures.demo.R
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Created by Muthuraj on 17/03/21.
 *
 * Shows detail view with image and meta information in ViewPager style. The left and right pages
 * will be partially visible to indicate that user can scroll to see other item's detail.
 */
class DetailViewPager : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val viewPager = ViewPager2(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        this@DetailViewPager.addView(this)
    }

    //This is internally used by viewPager. We need to access it sometimes, so hold the reference as
    //member variable for ease of access.
    private val recyclerView = viewPager.getChildAt(0) as RecyclerView

    fun setAdapter(adapter: DetailViewPagerAdapter) {
        viewPager.adapter = adapter

        //To show part of previous and next item.
        //Ref: https://stackoverflow.com/a/58088398/3423932
        with(viewPager) {
            offscreenPageLimit = 1

            val nextItemVisiblePx =
                resources.getDimension(R.dimen.viewpager_next_item_visible)
            val currentItemHorizontalMarginPx =
                resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
            val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
            setPageTransformer { page, position ->
                page.translationX = -pageTranslationX * position
                // Next line scales the item's height
                page.scaleY = 1 - (0.1f * position.absoluteValue)
                // For fading effect.
                page.alpha = 0.45f + (1 - position.absoluteValue)
            }

            //Adds margin to the left and right sides of the RecyclerView item.
            addItemDecoration(object : RecyclerView.ItemDecoration() {

                private val horizontalMargin = currentItemHorizontalMarginPx.roundToInt()

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = horizontalMargin
                    outRect.left = horizontalMargin
                }
            })
        }
    }

    /**
     * Calling this with [smoothScroll] as false will invoke animation on item in [position].
     * */
    fun setCurrentItem(position: Int, smoothScroll: Boolean) {
        if (!smoothScroll) {
            //If not smooth scrolling, animate the item on binding.
            (viewPager.adapter as DetailViewPagerAdapter).detailItemAnimatePosition = position

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val currentlyVisibleItemRange =
                layoutManager.findFirstVisibleItemPosition()..layoutManager.findLastVisibleItemPosition()
            if (viewPager.currentItem in currentlyVisibleItemRange) {
                //View is already visible, rebind it to force the animation.
                viewPager.adapter!!.notifyItemChanged(position)
            }
        }
        viewPager.setCurrentItem(position, smoothScroll)
    }

    fun registerPageChangeCallback(pageChangeCallback: ViewPager2.OnPageChangeCallback) {
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    fun getCurrentItemData() =
        (viewPager.adapter as DetailViewPagerAdapter).dataset[viewPager.currentItem]

    fun findDetailImage(transitionName: String): ImageView? {
        return recyclerView
            .children
            .map { it.findViewById<ImageView>(R.id.imageView) }
            .find { it.transitionName == transitionName }
    }
}