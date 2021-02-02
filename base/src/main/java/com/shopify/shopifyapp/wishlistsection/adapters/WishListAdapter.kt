package com.shopify.shopifyapp.wishlistsection.adapters

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront

import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MWishitemBinding
import com.shopify.shopifyapp.basesection.models.CommanModel
import com.shopify.shopifyapp.dbconnection.entities.ItemData
import com.shopify.shopifyapp.productsection.models.VariantData
import com.shopify.shopifyapp.quickadd_section.activities.QuickAddActivity
import com.shopify.shopifyapp.wishlistsection.models.WishListItem
import com.shopify.shopifyapp.wishlistsection.viewholders.WishItem
import com.shopify.shopifyapp.wishlistsection.viewmodels.WishListViewModel
import retrofit2.http.Field

import javax.inject.Inject

class WishListAdapter @Inject
constructor() : RecyclerView.Adapter<WishItem>() {
    private var data: MutableList<Storefront.Product>? = null
    private var context: Context? = null
    private var layoutInflater: LayoutInflater? = null

    companion object {
        var variantCallback: variantCallback? = null
    }

    private var model: WishListViewModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishItem {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<MWishitemBinding>(layoutInflater!!, R.layout.m_wishitem, parent, false)
        return WishItem(binding)
    }

    override fun onBindViewHolder(holder: WishItem, position: Int) {
        val model = CommanModel()
        model.imageurl = data?.get(position)?.images!!.edges[0]?.node?.transformedSrc
        holder.binding.commondata = model
        holder.binding.productId = data?.get(position)?.id.toString()
        holder.binding.variantData = data?.get(position)
        holder.binding.position = position
        holder.binding.name.text = data?.get(position)?.title
        //val wishdata = WishListItem()
        Log.i("MageNative : " + data?.get(position)?.title, "" + position)
        holder.binding.movetocart.setTextColor(holder.binding.movetocart.context.resources.getColor(R.color.colorAccent))
        holder.binding.movetocart.textSize = 11f
        holder.binding.name.textSize = 12f
        holder.binding.handler = ClickHandlers()
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    interface variantCallback {
        fun callback(variantSize: Int)
    }

    fun setData(products: MutableList<Storefront.Product>?, context: Context, model: WishListViewModel) {
        this.data = products
        this.model = model
        this.context = context
        Log.i("MageNative", "wishcount 2 : " + this.data!!.size)
    }

    inner class ClickHandlers {
        fun removeWishList(view: View, product_id: String, position: Int) {
            try {
                Log.i("MageNative", "Position : " + position)
                model!!.deleteData(product_id)
                data!!.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, data!!.size)
                model!!.update(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun moveToCart(view: View, product_id: String, variantData: Storefront.Product, position: Int) {
//            try {
//                Log.i("MageNative", "Position : " + item.position)
//                model!!.addToCart(item.variant_id!!)
//                removeWishList(view, item)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            var customQuickAddActivity = QuickAddActivity(context = context!!,activity = context, theme = R.style.WideDialogFull, product_id = product_id, repository = model?.repository!!, wishListViewModel = model, position = position,wishlistData = data)
            if (variantData.variants.edges.size == 1) {
                customQuickAddActivity.addToCart(variantData.variants.edges[0].node.id.toString(), 1)
                removeWishList(view, product_id, position)
            } else {
                customQuickAddActivity.show()
            }
        }
    }
}
