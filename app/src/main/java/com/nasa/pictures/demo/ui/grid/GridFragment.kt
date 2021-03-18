/* $Id$ */
package com.nasa.pictures.demo.ui.grid

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
import com.google.android.material.transition.MaterialContainerTransform
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.databinding.FragmentGridBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.shared.DataAdapter
import com.nasa.pictures.demo.ui.grid.shared.SharedViewModel
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
        binding.gridRecyclerView.visibility = View.GONE
    }

    private fun onDataFetchDone(data: List<Data>) {
        binding.loadingText.visibility = View.GONE
        binding.gridRecyclerView.visibility = View.VISIBLE
        binding.gridRecyclerView.adapter = DataAdapter(data, false, ::openDetailScreen)
        binding.detailView.setData(data) { selectedIndicatorItemPosition ->
            binding.gridRecyclerView.scrollToPosition(selectedIndicatorItemPosition)
        }

        binding.gridRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val firstVisiblePosition =
                        (recyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                    binding.detailView.onGridViewScrolled(firstVisiblePosition)
                }
            }
        })
    }

    private fun openDetailScreen(clickedPosition: Int, data: Data) {
        with(binding) {
            val transitionName = data.transitionName

            //Get the clicked view from grid recyclerView.
            val mainView = gridRecyclerView.children
                .map { it.findViewById<ImageView>(R.id.imageView) }
                .find { it.transitionName == transitionName }!!

            //Get target view from detailView.
            val endView = detailView.findDetailImage(transitionName)
            if (endView != null) {
                //Target view will be null for first time opening an item (or nearby item).
                val transition = MaterialContainerTransform().apply {
                    startView = mainView
                    this.endView = endView
                    addTarget(endView)
                }
                TransitionManager.beginDelayedTransition(binding.constraintLayout, transition)
            }

            val backPressedCallback = detailView.openDetail(clickedPosition) {
                onDetailViewClosing()
            }
            requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
        }
    }

    private fun onDetailViewClosing() {
        val transitionName = binding.detailView.getCurrentItemData().transitionName
        val mainView = binding.detailView.findDetailImage(transitionName)!!
        val endView = binding.gridRecyclerView.children
            .map { it.findViewById<ImageView>(R.id.imageView) }
            .find { it.transitionName == transitionName }!!
        val transition = MaterialContainerTransform().apply {
            startView = mainView
            this.endView = endView
            addTarget(endView)
        }
        TransitionManager.beginDelayedTransition(binding.constraintLayout, transition)
    }
}