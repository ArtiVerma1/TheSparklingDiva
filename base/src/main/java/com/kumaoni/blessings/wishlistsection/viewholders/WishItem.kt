package com.kumaoni.blessings.wishlistsection.viewholders

import androidx.recyclerview.widget.RecyclerView

import com.kumaoni.blessings.databinding.MWishitemBinding
class WishItem:RecyclerView.ViewHolder{
    var binding:MWishitemBinding
    constructor( binding: MWishitemBinding):super(binding.root){
        this.binding=binding;
    }
}
