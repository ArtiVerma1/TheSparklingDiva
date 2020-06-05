package com.shopify.shopifyapp.wishlistsection.adapters

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MWishitemBinding
import com.shopify.shopifyapp.basesection.models.CommanModel
import com.shopify.shopifyapp.dbconnection.entities.ItemData
import com.shopify.shopifyapp.wishlistsection.models.WishListItem
import com.shopify.shopifyapp.wishlistsection.viewholders.WishItem
import com.shopify.shopifyapp.wishlistsection.viewmodels.WishListViewModel

import javax.inject.Inject

class WishListAdapter @Inject
constructor() : RecyclerView.Adapter<WishItem>() {
    private var data: MutableList<ItemData>? = null
    private var layoutInflater: LayoutInflater? = null
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
        model.imageurl = data!![position].image
        holder.binding.commondata = model
        val wishdata = WishListItem()
        Log.i("MageNative : " + data!![position].productname, "" + position)
        wishdata.position = position
        wishdata.productname = data!![position].productname
        wishdata.variant_id = data!![position].variant_id
        if (data!![position].isSet_strike) {
            wishdata.specialprice = data!![position].specialprice
            wishdata.offertext = data!![position].offertext
            holder.binding.specialprice.visibility = View.VISIBLE
            holder.binding.offertext.visibility = View.VISIBLE
            holder.binding.offertext.setTextColor(holder.binding.offertext.context.resources.getColor(R.color.colorAccent))
            holder.binding.regularprice.paintFlags = holder.binding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.specialprice.visibility = View.GONE
            holder.binding.offertext.visibility = View.GONE
            holder.binding.regularprice.paintFlags = holder.binding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        wishdata.normalprice = data!![position].normalprice
        wishdata.isSet_strike = data!![position].isSet_strike
        wishdata.variant_one = data!![position].variant_one.toLowerCase()
        wishdata.variant_two = data!![position].variant_two.toLowerCase()
        wishdata.variant_three = data!![position].variant_three.toLowerCase()
        holder.binding.movetocart.setTextColor(holder.binding.movetocart.context.resources.getColor(R.color.colorAccent))
        holder.binding.movetocart.textSize = 11f
        holder.binding.name.textSize = 12f
        holder.binding.specialprice.textSize = 13f
        holder.binding.regularprice.textSize = 11f
        holder.binding.offertext.textSize = 11f
        holder.binding.variantOne.textSize = 11f
        holder.binding.variantTwo.textSize = 11f
        holder.binding.variantThree.textSize = 11f
        holder.binding.variantdata = wishdata
        holder.binding.handler = ClickHandlers()
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun setData(data: MutableList<ItemData>, model: WishListViewModel) {
        this.data = data
        this.model = model
        Log.i("MageNative", "wishcount 2 : " + this.data!!.size)
    }

    inner class ClickHandlers {
        fun removeWishList(view: View, item: WishListItem) {
            try {
                Log.i("MageNative", "Position : " + item.position)
                model!!.deleteData(item.variant_id!!)
                data!!.removeAt(item.position)
                notifyItemRemoved(item.position)
                notifyItemRangeChanged(item.position, data!!.size)
                model!!.update(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun moveToCart(view: View, item: WishListItem) {
            try {
                Log.i("MageNative", "Position : " + item.position)
                model!!.addToCart(item.variant_id!!)
                removeWishList(view, item)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
