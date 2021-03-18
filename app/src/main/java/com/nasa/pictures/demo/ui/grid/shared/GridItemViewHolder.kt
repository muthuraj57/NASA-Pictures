/* $Id$ */
package com.nasa.pictures.demo.ui.grid.shared

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.clear
import coil.load
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.databinding.HorizontalListItemBinding
import com.nasa.pictures.demo.model.Data

/**
 * Created by Muthuraj on 17/03/21.
 */
sealed class ItemViewHolder(
    binding: ViewBinding,
    onItemClicked: (clickedPosition: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClicked(adapterPosition)
            }
        }
    }

    abstract fun bind(data: Data)

    abstract fun onRecycled()

    protected fun bind(data: Data, imageView: ImageView) {
        imageView.transitionName = data.transitionName
        imageView.load(data.url)
    }
}

class GridItemViewHolder(
    private val binding: GridItemBinding,
    onItemClicked: (clickedPosition: Int) -> Unit
) : ItemViewHolder(binding, onItemClicked) {

    override fun bind(data: Data) {
        bind(data, binding.imageView)
    }

    override fun onRecycled() {
        binding.imageView.clear()
    }
}

class HorizontalListItemViewHolder(
    private val binding: HorizontalListItemBinding,
    onItemClicked: (clickedPosition: Int) -> Unit
) : ItemViewHolder(binding, onItemClicked) {

    override fun bind(data: Data) {
        bind(data, binding.imageView)
    }

    override fun onRecycled() {
        binding.imageView.clear()
    }
}