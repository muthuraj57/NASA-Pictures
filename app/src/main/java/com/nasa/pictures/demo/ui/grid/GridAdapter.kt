/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import com.nasa.pictures.demo.databinding.GridItemBinding
import com.nasa.pictures.demo.model.Data

/**
 * Created by Muthuraj on 17/03/21.
 */
class GridAdapter(private val dataset: List<Data>) :
    RecyclerView.Adapter<GridAdapter.GridItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemViewHolder {
        return GridItemViewHolder(GridItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: GridItemViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun onViewRecycled(holder: GridItemViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }

    override fun getItemCount() = dataset.size

    class GridItemViewHolder(private val binding: GridItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Data) {
            binding.imageView.load(data.url)
        }

        fun onRecycled() {
            binding.imageView.clear()
        }
    }
}