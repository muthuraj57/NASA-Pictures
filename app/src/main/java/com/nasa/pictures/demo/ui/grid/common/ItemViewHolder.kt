/* $Id$ */
package com.nasa.pictures.demo.ui.grid.common

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.databinding.HorizontalListItemBinding
import com.nasa.pictures.demo.util.ImageLoader
import com.nasa.pictures.demo.model.Data
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Created by Muthuraj on 17/03/21.
 */
sealed class ItemViewHolder(
    binding: ViewBinding,
    private val imageLoader: ImageLoader,
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

    protected fun bind(data: Data, imageView: ImageView) {
        imageView.transitionName = data.transitionName
        imageLoader.load(imageView, data.url)
    }
}

class GridItemViewHolder @AssistedInject constructor(
    @Assisted private val binding: GridItemBinding,
    imageLoader: ImageLoader,
    @Assisted onItemClicked: (clickedPosition: Int) -> Unit
) : ItemViewHolder(binding, imageLoader, onItemClicked) {

    override fun bind(data: Data) {
        bind(data, binding.imageView)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            binding: GridItemBinding,
            onItemClicked: (clickedPosition: Int) -> Unit
        ): GridItemViewHolder
    }
}

class HorizontalListItemViewHolder @AssistedInject constructor(
    @Assisted private val binding: HorizontalListItemBinding,
    imageLoader: ImageLoader,
    @Assisted onItemClicked: (clickedPosition: Int) -> Unit
) : ItemViewHolder(binding, imageLoader, onItemClicked) {

    override fun bind(data: Data) {
        bind(data, binding.imageView)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            binding: HorizontalListItemBinding,
            onItemClicked: (clickedPosition: Int) -> Unit
        ): HorizontalListItemViewHolder
    }
}