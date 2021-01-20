package com.shopify.shopifyapp.quickadd_section.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.CustomVariantListitemBinding
import com.shopify.shopifyapp.productsection.models.VariantData

class QuickVariantAdapter : RecyclerView.Adapter<QuickVariantAdapter.QuickVariantViewHolder>() {

    var variants: List<Storefront.ProductVariantEdge>? = null
    var context: Context? = null

    companion object {
        var itemClickVariant: ItemClick? = null
        var selectedPosition: Int = -1
    }

    fun setData(variants: List<Storefront.ProductVariantEdge>, context: Context, itemClick: ItemClick) {
        this.variants = variants
        itemClickVariant = itemClick
        this.context = context
    }

    interface ItemClick {
        fun variantSelection(variantData: VariantData)
    }

    class QuickVariantViewHolder : RecyclerView.ViewHolder {
        var customVariantListitemBinding: CustomVariantListitemBinding? = null

        constructor(itemView: CustomVariantListitemBinding) : super(itemView.root) {
            customVariantListitemBinding = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickVariantViewHolder {
        var binding = DataBindingUtil.inflate<CustomVariantListitemBinding>(LayoutInflater.from(parent.context), R.layout.custom_variant_listitem, null, false)
        return QuickVariantViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return variants!!.size
    }

    override fun onBindViewHolder(holder: QuickVariantViewHolder, position: Int) {
        val data = VariantData()
        data.position = position
        data.variant_id = variants!![position].node.id.toString()
        data.variantimage = variants!![position].node.image.transformedSrc
        setVariants(data, holder, variants!![position].node.selectedOptions)
        var sdk: Int = android.os.Build.VERSION.SDK_INT;
        if (selectedPosition == position) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.customVariantListitemBinding?.mainview?.setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.drawable.variant_select_bg));
            } else {
                holder.customVariantListitemBinding?.mainview?.setBackground(ContextCompat.getDrawable(context!!, R.drawable.variant_select_bg));
            }
        } else {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.customVariantListitemBinding?.mainview?.setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.drawable.variant_default_bg));
            } else {
                holder.customVariantListitemBinding?.mainview?.setBackground(ContextCompat.getDrawable(context!!, R.drawable.variant_default_bg));
            }
        }
        holder.customVariantListitemBinding?.mainview?.setOnClickListener {
            itemClickVariant?.variantSelection(data)
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    private fun setVariants(data: VariantData, holder: QuickVariantViewHolder, selectedOptions: List<Storefront.SelectedOption>) {
        try {
            val iterator1 = selectedOptions.iterator()
            var counter = 0
            var option: Storefront.SelectedOption
            while (iterator1.hasNext()) {
                counter = counter + 1
                option = iterator1.next()
                val finalvalue = option.name + " : " + option.value
                if (counter == 1) {
                    data.selectedoption_one = finalvalue
                }
                if (counter == 2) {
                    data.selectedoption_two = finalvalue
                }
                if (counter == 3) {
                    data.selectedoption_three = finalvalue
                }
                if (counter > 3) {
                    break
                }
            }
            holder.customVariantListitemBinding?.varaintData = data
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}