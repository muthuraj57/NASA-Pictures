/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.databinding.FragmentDetailBinding
import com.nasa.pictures.demo.ui.grid.adapter.SharedViewModel
import com.nasa.pictures.demo.util.TransitionNameSetter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.launch

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailFragment : Fragment(R.layout.fragment_detail) {
    private val binding by viewBinding(FragmentDetailBinding::bind)
    private val viewModel by activityViewModels<SharedViewModel>()

    private val clickedPosition by lazy { requireArguments().getInt(EXTRA_DATA_POSITION) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val data = viewModel.getData(clickedPosition)
            TransitionNameSetter.set(binding.imageView, data)
            binding.imageView.load(data.url)
            binding.title.text = data.title
            binding.explanation.text = data.explanation
            binding.publishedDate.text = data.date
            if (data.copyright == null) {
                binding.copyrightName.visibility = View.GONE
            } else {
                binding.copyrightName.text = data.copyright
            }
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