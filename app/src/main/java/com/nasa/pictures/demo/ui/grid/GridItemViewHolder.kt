/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.util.TransitionNameSetter

/**
 * Created by Muthuraj on 17/03/21.
 */
class GridItemViewHolder(
    private val binding: GridItemBinding,
    private val onItemClicked: (clickedPosition: Int, transitionData: TransitionData) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Data) {
        TransitionNameSetter.set(binding.imageView, data)
        binding.imageView.load(data.url)
        binding.root.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val transitionData =
                    TransitionData(binding.imageView, TransitionNameSetter.getTransitionName(data))
                onItemClicked(adapterPosition, transitionData)
            }
        }
    }

    fun onRecycled() {
        binding.imageView.clear()
    }
}