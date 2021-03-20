/* $Id$ */
package com.nasa.pictures.demo.ui.grid.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.databinding.HorizontalListItemBinding
import com.nasa.pictures.demo.model.Data
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Created by Muthuraj on 17/03/21.
 *
 * Used to populate data in both grid view and detail view.
 * [isForIndicatorView] determines whether current view is grid view or indicator view inside detail view.
 */
class DataAdapter @AssistedInject constructor(
    @Assisted private val dataset: List<Data>,
    @Assisted private val isForIndicatorView: Boolean,
    private val gridItemViewHolderFactory: GridItemViewHolder.Factory,
    private val horizontalListItemViewHolderFactory: HorizontalListItemViewHolder.Factory,
    @Assisted private val onItemClicked: (clickedPosition: Int) -> Unit
) :
    RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when {
            isForIndicatorView -> {
                val binding = HorizontalListItemBinding.inflate(layoutInflater)
                horizontalListItemViewHolderFactory.create(binding, onItemClicked)
            }
            else -> {
                val binding = GridItemBinding.inflate(LayoutInflater.from(parent.context))
                gridItemViewHolderFactory.create(binding, onItemClicked)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun getItemCount() = dataset.size

    @AssistedFactory
    interface Factory {
        fun create(
            dataset: List<Data>,
            isForIndicatorView: Boolean,
            onItemClicked: (clickedPosition: Int) -> Unit
        ): DataAdapter
    }
}