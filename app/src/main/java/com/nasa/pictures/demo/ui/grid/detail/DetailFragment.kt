/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.ui.grid.DataFetchStatus
import com.nasa.pictures.demo.ui.grid.adapter.SharedViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailFragment : Fragment(/*R.layout.detail_view*/) {
    //    private val binding by viewBinding(DetailViewBinding::bind)
    private val viewModel by activityViewModels<SharedViewModel>()
    private val detailView by lazy {
        DetailView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            visibility = View.VISIBLE
        }
    }

    private val clickedPosition by lazy { requireArguments().getInt(EXTRA_DATA_POSITION) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return detailView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sharedElementEnterTransition = MaterialContainerTransform().apply {
//            drawingViewId = R.id.fragmentContainer
//            scrimColor = Color.TRANSPARENT
//        }
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_image)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        viewModel.getDataFlow()
            .onEach {
                if (it is DataFetchStatus.Done) {
                    detailView.setData(it.dataset) {}
                    detailView.onGridViewScrolled(clickedPosition)
                    detailView.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
//        viewLifecycleOwner.lifecycleScope.launch {
//            val data = viewModel.getData(clickedPosition)
//            binding.viewModel = DetailViewModel(data)
//        }
    }

    companion object {

        private const val EXTRA_DATA_POSITION = "extra_data_position"

        fun newInstance(dataPosition: Int): DetailFragment {
            return DetailFragment().apply {
                arguments = bundleOf(EXTRA_DATA_POSITION to dataPosition)
            }
        }
    }
}