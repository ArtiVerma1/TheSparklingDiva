package com.kumaoni.blessings.addresssection.viewholders
import androidx.recyclerview.widget.RecyclerView
import com.kumaoni.blessings.databinding.MAddressitemBinding
class AddressViewHolder :RecyclerView.ViewHolder{
    var binding: MAddressitemBinding
    constructor(binding: MAddressitemBinding) : super(binding.root) {
        this.binding=binding;
    }
}

