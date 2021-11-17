package com.shopify.shopifyapp.productsection.viewholders

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopify.shopifyapp.R

class CategoryHolders(view: View, context: Activity) : RecyclerView.ViewHolder(view) {
    var raddfirst: TextView
    var raddsecond: TextView
    var rpickuptime: TextView
    var rphonenumber: TextView

    init {
        raddsecond = view.findViewById(R.id.raddsecond)
        raddfirst = view.findViewById(R.id.raddfirst)
        rpickuptime = view.findViewById(R.id.rpickuptime)
        rphonenumber = view.findViewById(R.id.rphonenumber)
    }
}
