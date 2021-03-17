/* $Id$ */
package com.nasa.pictures.demo.ui.grid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasa.pictures.demo.databinding.DetailListViewBinding
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.ui.grid.detail.DetailViewModel

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailViewAdapter(private val dataset: List<Data>) :
    RecyclerView.Adapter<DetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {

        //Somehow even if we set width and height as match_parent in xml, it is changed to wrap_content
        //and viewPager2 crashes because of this. So manually make it as match_parent to solve that issue.
        val params = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val binding = DetailListViewBinding.inflate(LayoutInflater.from(parent.context))
        binding.root.layoutParams = params
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.binding.viewModel = DetailViewModel(dataset[position])
    }

    override fun getItemCount() = dataset.size
}