package com.shopify.shopifyapp.productsection.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.productsection.viewholders.CategoryHolders
import javax.inject.Inject

class CustomAdapters @Inject constructor() : RecyclerView.Adapter<CategoryHolders>() {

    private lateinit var alledges: MutableList<Storefront.StoreAvailabilityEdge>
    private var activity: Activity? = null

    fun setData(alledges: MutableList<Storefront.StoreAvailabilityEdge>, activity: Activity) {
        this.alledges = alledges
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolders {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.categorydesign, parent, false)
        return CategoryHolders(view, activity!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CategoryHolders, position: Int) {
        holder.raddfirst.text = alledges[position].node.location.address.city
        holder.raddsecond.text =
            alledges[position].node.location.address.address2 + " " + alledges[position].node.location.address.address1 + " " +
                    alledges[position].node.location.address.province + " " + alledges[position].node.location.address.city + " " + alledges[position].node.location.address.zip
        holder.rpickuptime.text = alledges[position].node.pickUpTime
        holder.rphonenumber.text = alledges[position].node.location.address.phone
    }

    override fun getItemCount(): Int {
        return alledges.size
    }
}
