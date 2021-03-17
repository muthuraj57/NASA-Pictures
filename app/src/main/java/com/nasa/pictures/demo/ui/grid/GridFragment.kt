/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.databinding.FragmentGridBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.adapter.GridAdapter
import com.nasa.pictures.demo.ui.grid.adapter.SharedViewModel
import com.nasa.pictures.demo.ui.grid.detail.DetailFragment
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
        binding.recyclerView.visibility = View.GONE
    }

    private fun onDataFetchDone(data: List<Data>) {
        binding.loadingText.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.recyclerView.adapter = GridAdapter(data, ::openDetailScreen)
    }

    private fun openDetailScreen(clickedPosition: Int, transitionData: TransitionData) {
        //To ensure navigation happens only when app is in started state.
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            requireActivity().supportFragmentManager.commit {
                addSharedElement(transitionData.imageView, transitionData.transitionName)
                replace(
                    R.id.fragmentContainer,
                    DetailFragment.newInstance(clickedPosition),
                    DetailFragment::class.java.simpleName
                )
                addToBackStack(transitionData.transitionName)
            }
        }
    }
}