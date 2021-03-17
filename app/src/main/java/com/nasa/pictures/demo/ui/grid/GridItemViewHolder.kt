/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.clear
import coil.load
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.databinding.HorizontalListItemBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.util.TransitionNameSetter

/**
 * Created by Muthuraj on 17/03/21.
 */
sealed class ItemViewHolder(
    private val binding: ViewBinding,
    private val onItemClicked: (clickedPosition: Int, transitionData: TransitionData) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(data: Data)

    abstract fun onRecycled()

    protected fun bind(data: Data, imageView: ImageView) {
        TransitionNameSetter.set(imageView, data)
        imageView.load(data.url)
        binding.root.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val transitionData =
                    TransitionData(imageView, data)
                onItemClicked(adapterPosition, transitionData)
            }
        }
    }
}

class GridItemViewHolder(
    private val binding: GridItemBinding,
    onItemClicked: (clickedPosition: Int, transitionData: TransitionData) -> Unit
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
    onItemClicked: (clickedPosition: Int, transitionData: TransitionData) -> Unit
) : ItemViewHolder(binding, onItemClicked) {

    override fun bind(data: Data) {
        bind(data, binding.imageView)
    }

    override fun onRecycled() {
        binding.imageView.clear()
    }
}