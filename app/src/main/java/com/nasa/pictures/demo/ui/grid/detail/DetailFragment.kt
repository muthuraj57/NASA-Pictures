/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.databinding.DetailViewBinding
import com.nasa.pictures.demo.ui.grid.adapter.SharedViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.launch

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailFragment : Fragment(R.layout.detail_view) {
    private val binding by viewBinding(DetailViewBinding::bind)
    private val viewModel by activityViewModels<SharedViewModel>()

    private val clickedPosition by lazy { requireArguments().getInt(EXTRA_DATA_POSITION) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_image)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val data = viewModel.getData(clickedPosition)
            binding.viewModel = DetailViewModel(data)
        }
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