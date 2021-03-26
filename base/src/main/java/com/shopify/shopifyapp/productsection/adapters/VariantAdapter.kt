package com.shopify.shopifyapp.productsection.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity.Companion.themeColor
import com.shopify.shopifyapp.databinding.MVariantoptionBinding
import com.shopify.shopifyapp.basesection.models.CommanModel
import com.shopify.shopifyapp.basesection.models.ListData
import com.shopify.shopifyapp.databinding.SwatchesListItemBinding
import com.shopify.shopifyapp.productsection.models.VariantData
import com.shopify.shopifyapp.productsection.viewholders.VariantItem
import com.shopify.shopifyapp.productsection.viewmodels.ProductViewModel
import org.json.JSONArray
import java.lang.IndexOutOfBoundsException

import javax.inject.Inject

class VariantAdapter : RecyclerView.Adapter<VariantItem>() {
    private var variants: List<Storefront.ProductVariantEdge>? = null
    private var model: ProductViewModel? = null
    private var data: ListData? = null
    private var variant_data: List<String>? = null
    private val TAG = "VariantAdapter"
    private var selectedPosition = -1
    private var variant_title: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantItem {
        val binding = DataBindingUtil.inflate<SwatchesListItemBinding>(LayoutInflater.from(parent.context), R.layout.swatches_list_item, parent, false)
        binding.variantName.textSize = 14f
        return VariantItem(binding)
    }

    companion object {
        var variantCallback: VariantCallback? = null
    }

    interface VariantCallback {
        fun clickVariant(variant: Storefront.ProductVariantEdge, variant_title: String)
    }

    override fun onBindViewHolder(holder: VariantItem, position: Int) {
        val model = CommanModel()
        model.imageurl = variants?.get(position)?.node?.image?.originalSrc
        val data = VariantData()
        data.position = position
        data.variant_id = variants!![position].node.id.toString()
        data.variantimage = variants!![position]?.node?.image?.transformedSrc
        data.data = this.data
        data.model = this.model
        holder.binding.variantName.text = variant_data?.get(position)
        Log.d(TAG, "onBindViewHolder: " + variant_data?.get(position))
        if (position == selectedPosition) {
            holder.binding.variantCard.setCardBackgroundColor(Color.parseColor(themeColor))
            holder.binding.variantName.setTextColor(Color.WHITE)
        } else {
            holder.binding.variantCard.setCardBackgroundColor(Color.WHITE)
            holder.binding.variantName.setTextColor(Color.BLACK)
        }
        holder.binding.variantCard.setOnClickListener {
            selectedPosition = position
            variantCallback?.clickVariant(variants?.get(position)!!, variant_title!!)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return variant_data?.size!!
    }

    fun setData(variants: List<Storefront.ProductVariantEdge>, variant_data: List<String>, variant_title: String, model: ProductViewModel?, data: ListData?, variantCallback_: VariantCallback) {
        this.variants = variants
        this.model = model
        this.data = data
        this.variant_title = variant_title
        this.variant_data = variant_data
        variantCallback = variantCallback_
    }

}
