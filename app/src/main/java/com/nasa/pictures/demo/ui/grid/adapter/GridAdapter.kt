/* $Id$ */
package com.nasa.pictures.demo.ui.grid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.GridItemViewHolder
import com.nasa.pictures.demo.ui.grid.TransitionData

/**
 * Created by Muthuraj on 17/03/21.
 */
class GridAdapter(
    private val dataset: List<Data>,
    private val onItemClicked: (clickedPosition: Int, transitionData: TransitionData) -> Unit
) :
    RecyclerView.Adapter<GridItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemViewHolder {
        val binding = GridItemBinding.inflate(LayoutInflater.from(parent.context))
        return GridItemViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: GridItemViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun onViewRecycled(holder: GridItemViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }

    override fun getItemCount() = dataset.size
}