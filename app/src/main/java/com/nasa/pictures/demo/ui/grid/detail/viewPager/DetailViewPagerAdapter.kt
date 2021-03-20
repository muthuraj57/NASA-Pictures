/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail.viewPager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.nasa.pictures.demo.databinding.DetailListViewBinding
import com.nasa.pictures.demo.model.Data
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailViewPagerAdapter @AssistedInject constructor(
    @Assisted val dataset: List<Data>,
    private val detailViewModelFactor: DetailViewModel.Factory
) :
    RecyclerView.Adapter<DetailViewPagerAdapter.DetailViewHolder>() {

    var detailItemAnimatePosition = -1

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
        holder.binding.viewModel = detailViewModelFactor.create(dataset[position])
        if (detailItemAnimatePosition == position) {
            animateDetailView(holder.binding)
            detailItemAnimatePosition = -1
        }
    }

    private fun animateDetailView(detailViewBinding: DetailListViewBinding) {
        with(detailViewBinding.detailViewItem) {
            animateFromBottom(title, 0)
            animateFromBottom(explanation, 100)
            animateFromBottom(publishedDateLabel, 150)
            animateFromBottom(publishedDate, 150)
            if (copyrightFlow.isVisible) {
                animateFromBottom(copyrightLabel, 200)
                animateFromBottom(copyrightName, 200)
            }
        }
    }

    private fun animateFromBottom(view: View, startDelay: Long) {
        view.translationY = 1000f
        view.alpha = 0f
        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(startDelay)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(800)
            .start()
    }

    override fun getItemCount() = dataset.size

    class DetailViewHolder(val binding: DetailListViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    @AssistedFactory
    interface Factory {
        fun create(dataset: List<Data>): DetailViewPagerAdapter
    }
}