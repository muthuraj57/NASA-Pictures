/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Muthuraj on 17/03/21.
 *
 * To scroll the smoothScrollPosition item in center of the screen.
 * By default, the item will be scrolled such that it will be at bottom of screen
 */
class IndicatorLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        val smoothScroller = CenterSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            //Return view's center position with respect to box (RecyclerView) bounds to center the
            //scrolled item in RecyclerView.
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
        }
    }
}