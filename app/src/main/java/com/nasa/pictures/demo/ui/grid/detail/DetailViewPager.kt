/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.ui.grid.adapter.DetailViewAdapter
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Created by Muthuraj on 17/03/21.
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

    fun setAdapter(adapter: DetailViewAdapter) {
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
                // Next line scales the item's height. You can remove it if you don't want this effect
                page.scaleY = 1 - (0.1f * position.absoluteValue)
                // If you want a fading effect uncomment the next line:
//                page.alpha = 0.25f + (1 - position.absoluteValue)
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

    fun setCurrentItem(position: Int, smoothScroll: Boolean) {
        viewPager.setCurrentItem(position, smoothScroll)
    }

    fun getCurrentItem() = viewPager.currentItem

    fun registerPageChangeCallback(pageChangeCallback: ViewPager2.OnPageChangeCallback) {
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }
}