package com.kumaoni.blessings.basesection.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.kumaoni.blessings.R
import com.kumaoni.blessings.databinding.MListitemBinding
import com.kumaoni.blessings.basesection.activities.NewBaseActivity
import com.kumaoni.blessings.basesection.activities.Splash
import com.kumaoni.blessings.basesection.models.ListData
import com.kumaoni.blessings.basesection.viewholders.ListItem
import com.kumaoni.blessings.databinding.CurrencyListItemBinding
import com.kumaoni.blessings.databinding.CurrencyListLayoutBinding
import com.kumaoni.blessings.homesection.activities.HomePage
import com.kumaoni.blessings.utils.Constant
import javax.inject.Inject

class RecylerAdapter @Inject constructor() : RecyclerView.Adapter<ListItem>() {
    private var layoutInflater: LayoutInflater? = null
    private var currencies: List<Storefront.CurrencyCode>? = null
    private var activity: Activity? = null
    fun setData(currencies: List<Storefront.CurrencyCode>, activity: Activity) {
        this.currencies = currencies
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItem {
        val binding = DataBindingUtil.inflate<CurrencyListItemBinding>(LayoutInflater.from(parent.context), R.layout.currency_list_item, parent, false)
        return ListItem(binding)
    }

    override fun onBindViewHolder(holder: ListItem, position: Int) {
        val data = ListData()
        data.textdata = currencies!![position].toString()
        holder.binding.listdata = data
        holder.binding.handler = ClickHandler()
    }

    override fun getItemCount(): Int {
        return currencies!!.size
    }

    inner class ClickHandler {
        fun setCurrency(view: View, data: ListData) {
            (activity as NewBaseActivity).closePopUp()
            val model = (activity as NewBaseActivity).leftMenuViewModel
            model!!.setCurrencyData(data.textdata)
            val intent = Intent(activity, Splash::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity?.startActivity(intent)
            Constant.activityTransition(activity!!)
        }
    }
}
