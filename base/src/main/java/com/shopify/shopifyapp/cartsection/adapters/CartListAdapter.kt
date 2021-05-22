package com.shopify.shopifyapp.cartsection.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog


import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MCartitemBinding
import com.shopify.shopifyapp.basesection.models.CommanModel
import com.shopify.shopifyapp.cartsection.models.CartListItem
import com.shopify.shopifyapp.cartsection.viewholders.CartItem
import com.shopify.shopifyapp.cartsection.viewmodels.CartListViewModel
import com.shopify.shopifyapp.utils.Constant
import com.shopify.shopifyapp.utils.CurrencyFormatter
import org.json.JSONArray
import org.json.JSONObject

import java.math.BigDecimal

import javax.inject.Inject

class CartListAdapter @Inject constructor() : RecyclerView.Adapter<CartItem>() {
    var data: MutableList<Storefront.CheckoutLineItemEdge>? = null
    private var layoutInflater: LayoutInflater? = null
    private var model: CartListViewModel? = null
    var cartlistArray = JSONArray()
    private var context: Context? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItem {
        val binding = DataBindingUtil.inflate<MCartitemBinding>(LayoutInflater.from(parent.context), R.layout.m_cartitem, parent, false)
        return CartItem(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CartItem, position: Int) {
        val item = CartListItem()
        item.position = position
        item.product_id = data?.get(position)!!.node.variant.product.id.toString()
        item.variant_id = data?.get(position)!!.node.variant.id.toString()
        item.productname = data?.get(position)!!.node.title
        item.quantity_available=data?.get(position)!!.node.variant.quantityAvailable
        val variant = data?.get(position)!!.node.variant
        item.normalprice = CurrencyFormatter.setsymbol(variant.presentmentPrices.edges[0].node.price.amount, variant.presentmentPrices.edges[0].node.price.currencyCode.toString())
        if (variant.compareAtPriceV2 != null) {
            val special = java.lang.Double.valueOf(variant.presentmentPrices.edges[0].node.compareAtPrice.amount)
            val regular = java.lang.Double.valueOf(variant.presentmentPrices.edges[0].node.price.amount)
            if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                item.normalprice = CurrencyFormatter.setsymbol(variant.presentmentPrices.edges[0].node.compareAtPrice.amount, variant.presentmentPrices.edges[0].node.compareAtPrice.currencyCode.toString())
                item.specialprice = CurrencyFormatter.setsymbol(variant.presentmentPrices.edges[0].node.price.amount, variant.presentmentPrices.edges[0].node.price.currencyCode.toString())
                item.offertext = getDiscount(special, regular).toString() + "%off"
            } else {
                item.normalprice = CurrencyFormatter.setsymbol(variant.presentmentPrices.edges[0].node.price.amount, variant.presentmentPrices.edges[0].node.price.currencyCode.toString())
                item.specialprice = CurrencyFormatter.setsymbol(variant.presentmentPrices.edges[0].node.compareAtPrice.amount, variant.presentmentPrices.edges[0].node.compareAtPrice.currencyCode.toString())
                item.offertext = getDiscount(regular, special).toString() + "%off"
            }
            holder.binding!!.regularprice.setTextColor(holder.binding!!.regularprice.context.resources?.getColor(R.color.black)!!)
            holder.binding!!.specialprice.setTextColor(holder.binding!!.specialprice.context?.resources?.getColor(R.color.price_red)!!)
            var typeface = Typeface.createFromAsset(holder.binding!!.regularprice.context?.assets, "fonts/normal.ttf")
            holder.binding!!.regularprice.setTypeface(typeface)
            holder.binding.regularprice.paintFlags = holder.binding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.specialprice.visibility = View.VISIBLE
            holder.binding.offertext.visibility = View.VISIBLE
            holder.binding.offertext.setTextColor(holder.binding.offertext.context.resources.getColor(R.color.green))
        } else {
            holder.binding.specialprice.visibility = View.GONE
            holder.binding.offertext.visibility = View.GONE
            holder.binding!!.specialprice.visibility = View.GONE
            holder.binding!!.offertext.visibility = View.GONE
            holder.binding!!.regularprice.setTextColor(holder.binding!!.regularprice.context?.resources?.getColor(R.color.price_red)!!)
            holder.binding!!.regularprice.textSize = 13f
            var layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.marginStart = 0
            holder.binding!!.regularprice.layoutParams = layoutParams
            var typeface = Typeface.createFromAsset(holder.binding!!.regularprice.context?.assets, "fonts/bold.ttf")
            holder.binding!!.regularprice.setTypeface(typeface)
            holder.binding.regularprice.paintFlags = holder.binding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        val model = CommanModel()
        model.imageurl = variant?.image?.originalSrc
        holder.binding.commondata = model
        holder.binding.currencyCode = variant.presentmentPrices.edges[0].node.price.currencyCode.toString()
        holder.binding.productPrice = variant.presentmentPrices.edges[0].node.price.amount.toDouble()
        item.image = variant?.image?.originalSrc
        item.qty = data?.get(position)!!.node.quantity!!.toString()
        holder.binding.name.textSize = 14f
        holder.binding.specialprice.textSize = 13f
        // holder.binding.regularprice.textSize = 11f
        holder.binding.offertext.textSize = 11f
        holder.binding.variantOne.textSize = 11f
        holder.binding.variantTwo.textSize = 11f
        holder.binding.variantThree.textSize = 11f
        holder.binding.remove.textSize = 11f
        holder.binding.movetowish.textSize = 11f
        holder.binding.quantity.textSize = 11f
        holder.binding.handlers = ClickHandlers()
        setVariants(item, holder, variant.selectedOptions)
    }

    private fun setVariants(item: CartListItem, holder: CartItem, selectedOptions: List<Storefront.SelectedOption>) {
        try {
            val iterator1 = selectedOptions.iterator()
            var counter = 0
            var option: Storefront.SelectedOption
            while (iterator1.hasNext()) {
                counter = counter + 1
                option = iterator1.next()
                if (!option.value.equals("Default Title", true)) {
                    val finalvalue = option.name + " : " + option.value
                    if (counter == 1) {
                        item.variant_one = finalvalue
                    }
                    if (counter == 2) {
                        item.variant_two = finalvalue
                    }
                    if (counter == 3) {
                        item.variant_three = finalvalue
                    }
                    if (counter > 3) {
                        break
                    }
                }
            }
            holder.binding.variantdata = item
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun setData(data: MutableList<Storefront.CheckoutLineItemEdge>, model: CartListViewModel?, context: Context) {
        this.data = data
        this.model = model
        this.context = context
    }

    fun getDiscount(regular: Double, special: Double): Int {
        return ((regular - special) / regular * 100).toInt()
    }

    inner class ClickHandlers {
        fun moveToWishList(view: View, item: CartListItem, currencyCode: String, price: Double) {
            var cartlistData = JSONObject()
            cartlistData.put("id", item.product_id)
            cartlistData.put("quantity", item.qty)
            cartlistArray.put(cartlistData.toString())
            Constant.logAddToWishlistEvent(cartlistArray.toString(), item.product_id, "product", currencyCode
                    ?: "", price
                    ?: 0.0, context ?: Activity())
            model!!.moveToWishList(item)
            data!!.removeAt(item.position)
            notifyItemRemoved(item.position)
            notifyItemRangeChanged(item.position, data!!.size)
        }

        fun removeFromCart(view: View, item: CartListItem) {
            var alertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            alertDialog.setTitleText(context?.getString(R.string.warning_message))
            alertDialog.setContentText(context?.getString(R.string.delete_single_cart_warning))
            alertDialog.setConfirmText(context?.getString(R.string.yes_delete))
            alertDialog.setCancelText(context?.getString(R.string.no))
            alertDialog.setConfirmClickListener { sweetAlertDialog ->
                sweetAlertDialog.setTitleText(context?.getString(R.string.deleted))
                        .setContentText(context?.getString(R.string.cart_single_delete_message))
                        .setConfirmText(context?.getString(R.string.done))
                        .showCancelButton(false)
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                model!!.removeFromCart(item)
                data!!.removeAt(item.position)
                notifyItemRemoved(item.position)
                notifyItemRangeChanged(item.position, data!!.size)
            }
            alertDialog.show()
        }

        fun increase(view: View, item: CartListItem) {
            if (item.qty?.toInt() == item.quantity_available) {
                Toast.makeText(view.context, view.context.getString(R.string.variant_quantity_warning), Toast.LENGTH_LONG).show()
            } else {
                item.qty = (Integer.parseInt(item.qty!!) + 1).toString()
                model!!.updateCart(item)
            }
        }

        fun decrease(view: View, item: CartListItem) {
            if (Integer.parseInt(item.qty!!) == 1) {
                model!!.removeFromCart(item)
                data!!.removeAt(item.position)
                notifyItemRemoved(item.position)
                notifyItemRangeChanged(item.position, data!!.size)
            } else {
                item.qty = (Integer.parseInt(item.qty!!) - 1).toString()
                model!!.updateCart(item)
            }
        }
    }
}
