/* $Id$ */
package com.nasa.pictures.demo.ui.grid.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.databinding.HorizontalListItemBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.GridItemViewHolder
import com.nasa.pictures.demo.ui.grid.HorizontalListItemViewHolder
import com.nasa.pictures.demo.ui.grid.ItemViewHolder

/**
 * Created by Muthuraj on 17/03/21.
 *
 * Used to populate data in both grid view and detail view.
 * [isForIndicatorView] determines whether current view is grid view or indicator view inside detail view.
 */
class DataAdapter(
    private val dataset: List<Data>,
    private val isForIndicatorView: Boolean,
    private val onItemClicked: (clickedPosition: Int, data: Data) -> Unit
) :
    RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when {
            isForIndicatorView -> {
                val binding = HorizontalListItemBinding.inflate(layoutInflater)
                HorizontalListItemViewHolder(binding, onItemClicked)
            }
            else -> {
                val binding = GridItemBinding.inflate(LayoutInflater.from(parent.context))
                GridItemViewHolder(binding, onItemClicked)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }

    override fun getItemCount() = dataset.size
}