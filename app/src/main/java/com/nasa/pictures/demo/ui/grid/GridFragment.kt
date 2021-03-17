/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.databinding.FragmentGridBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.adapter.DataAdapter
import com.nasa.pictures.demo.ui.grid.adapter.SharedViewModel
import com.nasa.pictures.demo.util.log
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Created by Muthuraj on 17/03/21.
 */
@AndroidEntryPoint
class GridFragment : Fragment(R.layout.fragment_grid) {

    private val binding by viewBinding(FragmentGridBinding::bind)
    private val viewModel by activityViewModels<SharedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.horizontalRecyclerView.visibility = View.INVISIBLE
//        binding.horizontalRecyclerView.layoutManager = HorizontalLayoutManager(requireContext())
        viewModel.getDataFlow()
            .onEach { dataFetchStatus ->
                when (dataFetchStatus) {
                    is DataFetchStatus.Done -> onDataFetchDone(dataFetchStatus.dataset)
                    DataFetchStatus.InProgress -> onDataFetchProgress()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun onDataFetchProgress() {
        binding.loadingText.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun onDataFetchDone(data: List<Data>) {
        binding.loadingText.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.recyclerView.adapter = DataAdapter(data, false, ::openDetailScreen)
        binding.detailView.setData(data){selectedIndicatorItemPosition ->
            binding.recyclerView.scrollToPosition(selectedIndicatorItemPosition)
        }
//        binding.horizontalRecyclerView.adapter = DataAdapter(data, true) { clickedPosition, _ ->
//            binding.viewPager.setCurrentItem(clickedPosition, true)
//        }
//        binding.viewPager.setAdapter(DetailViewAdapter(data))

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val firstVisiblePosition =
                        (recyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
//                    binding.horizontalRecyclerView.scrollToPosition(firstVisiblePosition)
                    binding.detailView.onGridViewScrolled(firstVisiblePosition)
                }
            }
        })

        /*val targetHeight =
            resources.getDimensionPixelSize(R.dimen.horizontal_current_image_height)
        val itemHorizontalMargin =
            resources.getDimensionPixelSize(R.dimen.horizontal_image_list_gap)
        val topListLayoutManager =
            binding.horizontalRecyclerView.layoutManager as LinearLayoutManager

        //Used to scale top list view for currently selected detail view from item decorator.
        var currentVisibleDetailItemPosition: Int = -1

        binding.viewPager.registerPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentVisibleDetailItemPosition = position
                binding.recyclerView.scrollToPosition(position)
                binding.horizontalRecyclerView.smoothScrollToPosition(position)
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
                        this@GridFragment.logE { "onPageScrolled() width reset 1 scaleX: ${view.scaleX}, ${imageView.transitionName}" }
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
                            this@GridFragment.log { "onPageScrolled() called with: position = [$position], positionOffset = [$positionOffset], targetHeight = [$targetHeight], imageView.height = [${imageView.height}], scaleFactor = [$scaleFactor]" }
                            view.scaleX = scaleFactor
                            view.scaleY = scaleFactor
                            this@GridFragment.logE { "onPageScrolled() width reset 2 scaleX: ${scaleFactor}, ${imageView.transitionName}, position: $position, positionOffset: $positionOffset, previousPosition: $previousPosition, isScrollingFromLeftToRight: $isScrollingFromLeftToRight" }
                        }
                    }
                }

                binding.horizontalRecyclerView
                    .children
                    .filter { it != view }
                    .forEach { itemView ->

                        val adapterPosition =
                            binding.horizontalRecyclerView.getChildAdapterPosition(itemView)
                        val imageView = itemView.findViewById<ImageView>(R.id.imageView)

                        val canCheckForSides = positionOffset != 0f && previousPosition != -1
                        val isForLeftSideItem =
                            isScrollingFromLeftToRight && adapterPosition == position - 1
                        val isForRightSideItem =
                            isScrollingFromLeftToRight.not() && (adapterPosition == position + 1 || adapterPosition == position)

                        if (canCheckForSides && (isForLeftSideItem || isForRightSideItem)) {
                            this@GridFragment.logE { "onPageScrolled() width scale isScrollingFromLeftToRight: [$isScrollingFromLeftToRight], adapterPosition: [$adapterPosition], position: [$position]" }
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
                                this@GridFragment.logE { "onPageScrolled() width reset 3 scaleX: ${itemView.scaleX}, ${imageView.transitionName}" }
                            }
                        } else {
                            itemView.scaleX = 1f
                            itemView.scaleY = 1f
                            itemView.updateLayoutParams<ViewGroup.LayoutParams> {
                                width = imageView.width + itemHorizontalMargin
                            }
                            this@GridFragment.logE { "onPageScrolled() width reset 4 scaleX: ${itemView.scaleX}, ${imageView.transitionName}" }
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

        binding.horizontalRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                log {
                    "getItemOffsets() called with: currentVisibleDetailItemPosition: ${currentVisibleDetailItemPosition}, childAdapterPosition: ${
                        parent.getChildAdapterPosition(
                            view
                        )
                    }"
                }
                if (currentVisibleDetailItemPosition != -1 &&
                    currentVisibleDetailItemPosition == parent.getChildAdapterPosition(view)
                ) {
                    val imageView = view.findViewById<ImageView>(R.id.imageView)
                    view.scaleX = targetHeight.toFloat() / imageView.height
                    view.scaleY = targetHeight.toFloat() / imageView.height
                    view.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = targetHeight + itemHorizontalMargin
                    }
                }
            }
        })*/
    }

    private fun openDetailScreen(clickedPosition: Int, transitionData: TransitionData) {
        with(binding) {
//            horizontalRecyclerView.scrollToPosition(clickedPosition)
//            viewPager.setCurrentItem(clickedPosition, false)

            val topImageViews = detailView.getVisibleIndicatorImageViews()
//            val topImageViews = horizontalRecyclerView.children
//                .map { it.findViewById<ImageView>(R.id.imageView) }
//                .toList()
            val transitionSet = TransitionSet().apply {
                ordering = TransitionSet.ORDERING_TOGETHER
            }
//            horizontalRecyclerView.visibility = View.VISIBLE
            recyclerView.children
                .map { it.findViewById<ImageView>(R.id.imageView) }
                .mapNotNull { mainView ->
                    val topImageView =
                        topImageViews.find { it.transitionName == mainView.transitionName }
                            ?: return@mapNotNull null
                    this@GridFragment.log { "MaterialContainerTransform() adding transition for: mainView = [$mainView] and topImageView = [$topImageView]" }
                    MaterialContainerTransform().apply {
                        startView = mainView
                        endView = topImageView
                        addTarget(topImageView)
                        setPathMotion(MaterialArcMotion())
                        duration = 550
                        scrimColor = Color.TRANSPARENT
                    }
                }.onEach { transition ->
                    transitionSet.addTransition(transition)
                }.toList()
            TransitionManager.beginDelayedTransition(binding.constraintLayout, transitionSet)

//            recyclerView.visibility = View.INVISIBLE

           /* viewPager.visibility = View.VISIBLE
            viewPager.translationY = -1000f
            viewPager.alpha = 0f
            viewPager.animate()
                .translationY(0f)
                .alpha(1f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setDuration(500)
                .start()*/

            val backPressedCallback = detailView.openDetail(clickedPosition)
            requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

//            requireActivity().onBackPressedDispatcher.addCallback(
//                viewLifecycleOwner,
//                object : OnBackPressedCallback(true) {
//                    override fun handleOnBackPressed() {
//                        resetToGridView()
//                        remove()
//                    }
//                })
        }

        //To ensure navigation happens only when app is in started state.
        /*viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            requireActivity().supportFragmentManager.commit {
                val transitionName = TransitionNameSetter.getTransitionName(transitionData.data)
                addSharedElement(transitionData.imageView, transitionName)
                replace(
                    R.id.fragmentContainer,
                    DetailFragment.newInstance(clickedPosition),
                    DetailFragment::class.java.simpleName
                )
                addToBackStack(transitionName)
            }
        }*/
    }

    /*private fun resetToGridView() {
        with(binding) {
            viewPager.animate()
                .translationY(-1000f)
                .setInterpolator(FastOutSlowInInterpolator())
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    viewPager.visibility = View.INVISIBLE
                    viewPager.alpha = 1f

                    recyclerView.visibility = View.VISIBLE
                    horizontalRecyclerView.visibility = View.INVISIBLE
                }
                .start()

            val transitionSet = TransitionSet().apply {
                ordering = TransitionSet.ORDERING_TOGETHER
            }
            val topImageViews = horizontalRecyclerView.children
                .map { it.findViewById<ImageView>(R.id.imageView) }
                .toList()
            recyclerView.children
                .map { it.findViewById<ImageView>(R.id.imageView) }
                .mapNotNull { mainView ->
                    val topImageView =
                        topImageViews.find { it.transitionName == mainView.transitionName }
                            ?: return@mapNotNull null
                    this@GridFragment.log { "MaterialContainerTransform() adding transition for: mainView = [$mainView] and topImageView = [$topImageView]" }
                    MaterialContainerTransform().apply {
                        startView = topImageView
                        endView = mainView
                        addTarget(mainView)
                        setPathMotion(MaterialArcMotion())
                        duration = 550
                        scrimColor = Color.TRANSPARENT
                    }
                }.onEach { transition ->
                    transitionSet.addTransition(transition)
                }.toList()
            TransitionManager.beginDelayedTransition(binding.constraintLayout, transitionSet)
        }
    }*/
}