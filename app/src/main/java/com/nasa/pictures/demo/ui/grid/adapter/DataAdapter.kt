/* $Id$ */
package com.nasa.pictures.demo.ui.grid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.databinding.HorizontalListItemBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.GridItemViewHolder
import com.nasa.pictures.demo.ui.grid.HorizontalListItemViewHolder
import com.nasa.pictures.demo.ui.grid.ItemViewHolder
import com.nasa.pictures.demo.ui.grid.TransitionData
import com.nasa.pictures.demo.util.log
import com.nasa.pictures.demo.util.logE

/**
 * Created by Muthuraj on 17/03/21.
 */
class DataAdapter(
    private val dataset: List<Data>,
    private val isHorizontalList: Boolean,
    private val onItemClicked: (clickedPosition: Int, transitionData: TransitionData) -> Unit
) :
    RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (isHorizontalList) {
            logE { "onCreateViewHolder() called with: viewType = [$viewType]" }
        } else {
            log { "onCreateViewHolder() called with: viewType = [$viewType]" }
        }
        return when {
            isHorizontalList -> {
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
        if (isHorizontalList) {
            logE { "onBindViewHolder() called with: position = [$position]" }
        } else {
            log { "onBindViewHolder() called with: position = [$position]" }
        }
        holder.bind(dataset[position])
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }

    override fun getItemCount() = dataset.size
}