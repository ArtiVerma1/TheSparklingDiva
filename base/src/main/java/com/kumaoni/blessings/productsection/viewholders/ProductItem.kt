package com.kumaoni.blessings.productsection.viewholders
import androidx.recyclerview.widget.RecyclerView
import com.kumaoni.blessings.databinding.MPersonalisedBinding
import com.kumaoni.blessings.databinding.MProductitemBinding
class ProductItem :RecyclerView.ViewHolder{
    var binding: MProductitemBinding?=null
    var personalbinding: MPersonalisedBinding?=null
    constructor(binding: MProductitemBinding ) : super(binding.root) {
        this.binding=binding
    }
    constructor(personalbinding: MPersonalisedBinding ) : super(personalbinding.root) {
        this.personalbinding=personalbinding
    }
}

