/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nasa.pictures.demo.R
import com.nasa.pictures.demo.databinding.FragmentGridBinding
import com.nasa.pictures.demo.util.log
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by Muthuraj on 17/03/21.
 */
@AndroidEntryPoint
class GridFragment : Fragment(R.layout.fragment_grid) {

    private val binding by viewBinding(FragmentGridBinding::bind)
    private val viewModel by viewModels<GridScreenViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding
        viewLifecycleOwner.lifecycleScope.launch {
            val data = viewModel.getData()
            this@GridFragment.log { "onViewCreated() called with $data" }
        }
    }
}