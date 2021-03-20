/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.databinding.FragmentGridBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.common.DataAdapter
import com.nasa.pictures.demo.util.log
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 */
@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_grid) {

    private val binding by viewBinding(FragmentGridBinding::bind)
    private val viewModel by viewModels<MainScreenViewModel>()

    @Inject
    lateinit var dataAdapterFactory: DataAdapter.Factory

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

    private fun onDataFetchDone(dataset: List<Data>) {
        binding.loadingText.visibility = View.GONE
        binding.gridRecyclerView.visibility = View.VISIBLE
        binding.gridRecyclerView.adapter =
            dataAdapterFactory.create(dataset, false) { clickedPosition ->
                viewModel.onGridItemClicked(clickedPosition)
            }
        binding.detailView.setData(dataset) { selectedIndicatorItemPosition ->
            if (binding.detailView.isVisible) {
                //This callback will be called with value 0 for first time even when detail view is
                //not visible. But we don't want to show first item's detail view everytime app is
                //opened, hence this check.
                viewModel.onGridItemClicked(selectedIndicatorItemPosition)
            }
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

        viewModel.selectedDetailItemFlow
            .onEach { selectedItemPair ->
                if (selectedItemPair != null) {
                    val (selectedItemPosition, data) = selectedItemPair
                    binding.gridRecyclerView.scrollToPosition(selectedItemPosition)
                    openDetailScreen(selectedItemPosition, data)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun openDetailScreen(clickedPosition: Int, data: Data) {
        if (binding.detailView.isVisible) {
            log { "openDetailScreen() call ignored since detail view is already opened." }
            return
        }
        with(binding) {
            val transitionName = data.transitionName

            //Get the clicked view from grid recyclerView. This will be null on configuration changed
            //since gridRecyclerView might not have laid out it's children when this method is called.
            val mainView = gridRecyclerView.children
                .map { it.findViewById<ImageView>(R.id.imageView) }
                .find { it.transitionName == transitionName }

            if (mainView != null) {
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
            }

            val backPressedCallback = detailView.openDetail(clickedPosition) {
                viewModel.onDetailItemClosed()
                onDetailViewClosing()
            }
            requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
        }
    }

    /**
     * Called when detail is view is closing. We set transition for image view from detail view to
     * grid view here.
     * */
    private fun onDetailViewClosing() {
        val transitionName = binding.detailView.getCurrentItemData().transitionName
        val startView = binding.detailView.findDetailImage(transitionName)!!
        val endView = binding.gridRecyclerView.children
            .map { it.findViewById<ImageView>(R.id.imageView) }
            .find { it.transitionName == transitionName }!!
        val transition = MaterialContainerTransform().apply {
            this.startView = startView
            this.endView = endView
            addTarget(endView)
        }
        TransitionManager.beginDelayedTransition(binding.constraintLayout, transition)
    }
}