package com.shopify.shopifyapp.cartsection.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class CartBottomData : BaseObservable() {
    var subtotal: String? = null
    var tax: String? = null
    var grandtotal: String? = null
    @get:Bindable
    var subtotaltext: String? = null
        set(subtotaltext) {
            field = subtotaltext
            notifyPropertyChanged(BR.subtotaltext)
        }
    var checkoutId: String? = null

    var checkouturl: String? = null

}
