package com.shopify.shopifyapp.addresssection.viewholders
import androidx.recyclerview.widget.RecyclerView
import com.shopify.shopifyapp.databinding.MAddressitemBinding
class AddressViewHolder :RecyclerView.ViewHolder{
    var binding: MAddressitemBinding
    constructor(binding: MAddressitemBinding) : super(binding.root) {
        this.binding=binding;
    }
}

