package com.shopify.shopifyapp.productsection.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity.Companion.themeColor
import com.shopify.shopifyapp.basesection.models.CommanModel
import com.shopify.shopifyapp.basesection.models.ListData
import com.shopify.shopifyapp.databinding.SwatchesListItemBinding
import com.shopify.shopifyapp.productsection.models.VariantData
import com.shopify.shopifyapp.productsection.viewholders.VariantItem
import com.shopify.shopifyapp.productsection.viewmodels.ProductViewModel

class VariantAdapter : RecyclerView.Adapter<VariantItem>() {
    private var variants: MutableList<String>? = null
    private var context: Context? = null
    private val TAG = "VariantAdapter"
    private var outofStockList: MutableList<String>? = null
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantItem {
        val binding = DataBindingUtil.inflate<SwatchesListItemBinding>(LayoutInflater.from(parent.context), R.layout.swatches_list_item, parent, false)
        binding.variantName.textSize = 14f
        return VariantItem(binding)
    }

    companion object {
        var variantCallback: VariantCallback? = null
    }

    interface VariantCallback {
        fun clickVariant(variantName: String)
    }

    override fun onBindViewHolder(holder: VariantItem, position: Int) {
//        val model = CommanModel()
//        model.imageurl = variants?.get(position)?.node?.image?.originalSrc
//        val data = VariantData()
//        data.position = position
//        data.variant_id = variants!![position].node.id.toString()
//        data.variantimage = variants!![position]?.node?.image?.transformedSrc
//        data.data = this.data
//        data.model = this.model
//        holder.binding.variantName.text = variant_data?.get(position)
//        Log.d(TAG, "onBindViewHolder: " + variant_data?.get(position))
//        if (position == selectedPosition) {
//            holder.binding.variantCard.setCardBackgroundColor(Color.parseColor(themeColor))
//            holder.binding.variantName.setTextColor(Color.WHITE)
//        } else {
//            holder.binding.variantCard.setCardBackgroundColor(Color.WHITE)
//            holder.binding.variantName.setTextColor(Color.BLACK)
//        }
//        holder.binding.variantCard.setOnClickListener {
//            selectedPosition = position
//            variantCallback?.clickVariant(variants?.get(position)!!, variant_title!!)
//            notifyDataSetChanged()
//        }


        try {
            holder.binding.variantName.text = variants?.get(position)
            if (selectedPosition == position) {
                holder.binding.variantCard.setCardBackgroundColor(Color.parseColor(themeColor))
                holder.binding.variantName.setTextColor(Color.WHITE)
                holder.binding.variantName.isEnabled = true
                holder.binding.variantName.tag = "selected"
            } else {
//                if (outofStockList?.contains(variants?.get(position))!!) {
////                    holder.binding.variantName.background =
////                            context?.resources?.getDrawable(R.drawable.unselect_variant_bg)
//                    holder.binding.variantName.setTextColor(Color.parseColor("#D3D3D3"))
//                    holder.binding.variantName.isEnabled = false
//                } else {
                holder.binding.variantCard.setCardBackgroundColor(Color.WHITE)
                holder.binding.variantName.setTextColor(Color.BLACK)
                holder.binding.variantName.isEnabled = true
//                }
                holder.binding.variantName.tag = "unselected"
            }

            holder.binding.variantName.setOnClickListener {
                if (it.tag.equals("unselected")) {
                    selectedPosition = position
                    variantCallback?.clickVariant(variants?.get(position) ?: "")
                    notifyDataSetChanged()
                }
            }
            holder.setIsRecyclable(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return variants?.size!!
    }

    fun setData(variants: MutableList<String>, outofStockList: MutableList<String>, context: Context, variantCallback_: VariantCallback) {
        this.variants = variants
        this.outofStockList = outofStockList
        variantCallback = variantCallback_
        this.context = context
    }

}
