package com.shopify.shopifyapp.productsection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.ReviewListItemBinding
import com.shopify.shopifyapp.productsection.models.Review
import javax.inject.Inject

class ReviewListAdapter @Inject
constructor() : RecyclerView.Adapter<ReviewListAdapter.ReviewListViewHolder>() {

     var reviwList: List<Review>? = null

    fun setData(reviwList: List<Review>?) {
        this.reviwList = reviwList
    }

    class ReviewListViewHolder : RecyclerView.ViewHolder {
        var binding: ReviewListItemBinding? = null

        constructor(itemBinding: ReviewListItemBinding) : super(itemBinding.root) {
            this.binding = itemBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
        var binding = DataBindingUtil.inflate<ReviewListItemBinding>(LayoutInflater.from(parent.context), R.layout.review_list_item, parent, false)
        return ReviewListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reviwList?.size!!
    }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        holder.binding?.reviewList = reviwList?.get(position)
        holder.binding?.ratingBar?.rating=reviwList?.get(position)?.rating?.toFloat()!!
    }
}