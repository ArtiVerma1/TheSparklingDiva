package com.shopify.shopifyapp.cartsection.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MCartlistBinding
import com.shopify.shopifyapp.basesection.activities.BaseActivity
import com.shopify.shopifyapp.cartsection.adapters.CartListAdapter
import com.shopify.shopifyapp.cartsection.models.CartBottomData
import com.shopify.shopifyapp.cartsection.viewmodels.CartListViewModel
import com.shopify.shopifyapp.checkoutsection.activities.CheckoutWeblink
import com.shopify.shopifyapp.loginsection.activity.LoginActivity
import com.shopify.shopifyapp.personalised.adapters.PersonalisedAdapter
import com.shopify.shopifyapp.personalised.viewmodels.PersonalisedViewModel
import com.shopify.shopifyapp.utils.*
import com.shopify.shopifyapp.wishlistsection.activities.WishList
import org.json.JSONObject
import java.util.Objects
import javax.inject.Inject
class CartList : BaseActivity() {
    @Inject
    lateinit var factory: ViewModelFactory
    private var cartlist: RecyclerView? = null
    private var model: CartListViewModel? = null
    private var personamodel: PersonalisedViewModel? = null
    @Inject
    lateinit var adapter: CartListAdapter
    @Inject
    lateinit var personalisedadapter: PersonalisedAdapter
    @Inject
    lateinit var padapter: PersonalisedAdapter
    private var binding: MCartlistBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_cartlist, group, true)
        cartlist = setLayout(binding!!.cartlist, "vertical")
        cartlist!!.isNestedScrollingEnabled = false
        showTittle(resources.getString(R.string.yourcart))
        showBackButton()
        (application as MyApplication).mageNativeAppComponent!!.doCartListActivityInjection(this)
        model = ViewModelProvider(this, factory).get(CartListViewModel::class.java)
        personamodel = ViewModelProvider(this, factory).get(PersonalisedViewModel::class.java)
        model!!.Response().observe(this, Observer<Storefront.Checkout> { this.consumeResponse(it) })
       if(Constant.ispersonalisedEnable){
           model!!.getApiResponse().observe(this, Observer<ApiResponse> { this.consumeResponse(it) })
           model!!.getYouMAyAPiResponse().observe(this, Observer<ApiResponse> { this.Response(it) })
       }
        model!!.message.observe(this, Observer<String> { this.showToast(it) })
        if (model!!.cartCount > 0) {
            model!!.prepareCart()
        } else {
            showToast(resources.getString(R.string.emptycart))
            finish()
        }
        binding!!.subtotaltext.textSize = 12f
        binding!!.subtotal.textSize = 12f
        binding!!.taxtext.textSize = 12f
        binding!!.tax.textSize = 12f
        binding!!.proceedtocheck.textSize = 13f
        binding!!.handler = ClickHandler()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@CartList, msg, Toast.LENGTH_LONG).show()
    }

    private fun consumeResponse(reponse: Storefront.Checkout) {
        if (reponse.lineItems.edges.size > 0) {
            showTittle(resources.getString(R.string.yourcart) + " ( " + reponse.lineItems.edges.size + " items )")
            if (adapter!!.data != null) {
                adapter!!.data = reponse.lineItems.edges
                adapter!!.notifyDataSetChanged()
            } else {
                adapter!!.setData(reponse.lineItems.edges, model)
                cartlist!!.adapter = adapter
            }
            setBottomData(reponse)
            invalidateOptionsMenu()
        } else {
            showToast(resources.getString(R.string.emptycart))
            finish()
        }
    }
    private fun consumeResponse(reponse: ApiResponse) {
        when (reponse.status) {
            Status.SUCCESS -> setPersonalisedData(reponse.data!!)
            Status.ERROR -> {
                reponse.error!!.printStackTrace()
                showToast(resources.getString(R.string.errorString))
            }
        }
    }
    private fun Response(reponse: ApiResponse) {
        when (reponse.status) {
            Status.SUCCESS -> setYouMayPersonalisedData(reponse.data!!)
            Status.ERROR -> {
                reponse.error!!.printStackTrace()
                showToast(resources.getString(R.string.errorString))
            }
        }
    }

    private fun setPersonalisedData(data: JsonElement) {
        try{
            val jsondata = JSONObject(data.toString())
            if(jsondata.has("query1")){
                binding!!.personalisedsection.visibility=View.VISIBLE
                setLayout(binding!!.personalised, "horizontal")
                personamodel!!.setPersonalisedData(jsondata.getJSONObject("query1").getJSONArray("products"),personalisedadapter, model!!.presentCurrency!!,binding!!.personalised)
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }
    private fun setYouMayPersonalisedData(data: JsonElement) {
        try{
            val jsondata = JSONObject(data.toString())
            if(jsondata.has("query1")){
                binding!!.personalisedsection2.visibility=View.VISIBLE
                setLayout(binding!!.personalised2, "horizontal")
                personamodel!!.setPersonalisedData(jsondata.getJSONObject("query1").getJSONArray("products"),padapter, model!!.presentCurrency!!,binding!!.personalised2)
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

    private fun setBottomData(checkout: Storefront.Checkout) {
        try {
            val bottomData = CartBottomData()
            bottomData.checkoutId = checkout.id.toString()
            bottomData.subtotaltext = resources.getString(R.string.subtotaltext) + " ( " + model!!.cartCount + " items )"
            bottomData.subtotal = CurrencyFormatter.setsymbol(checkout.subtotalPriceV2.amount, checkout.subtotalPriceV2.currencyCode.toString())
            if (checkout.taxExempt!!) {
                binding!!.taxtext.visibility = View.VISIBLE
                binding!!.tax.visibility = View.VISIBLE
                bottomData.tax = CurrencyFormatter.setsymbol(checkout.totalTaxV2.amount, checkout.totalTaxV2.currencyCode.toString())
            }
            bottomData.grandtotal = CurrencyFormatter.setsymbol(checkout.totalPriceV2.amount, checkout.totalPriceV2.currencyCode.toString())
            bottomData.checkouturl = checkout.webUrl
            binding!!.bottomdata = bottomData
            binding!!.root.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_wish, menu)
        val item = menu.findItem(R.id.wish_item)
        item.setActionView(R.layout.m_wishcount)
        val notifCount = item.actionView
        val textView = notifCount.findViewById<TextView>(R.id.count)
        textView.text = "" + model!!.wishListcount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this@CartList, WishList::class.java)
            startActivity(mycartlist)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    inner class ClickHandler {
        fun loadCheckout(view: View, data: CartBottomData) {
            try {
                if (model!!.isLoggedIn) {
                    val intent = Intent(view.context, CheckoutWeblink::class.java)
                    intent.putExtra("link", data.checkouturl)
                    intent.putExtra("id", data.checkoutId)
                    view.context.startActivity(intent)
                } else {
                    showPopUp(data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        private fun showPopUp(data: CartBottomData) {
            try {
                val listDialog = Dialog(this@CartList, R.style.PauseDialog)
                ((Objects.requireNonNull<Window>(listDialog.window).getDecorView() as ViewGroup).getChildAt(0) as ViewGroup)
                        .getChildAt(1)
                        .setBackgroundColor(this@CartList.resources.getColor(R.color.black))
                listDialog.setTitle(Html.fromHtml("<font color='#ffffff'>" + resources.getString(R.string.logintype) + "</font>"))
                val li = this@CartList.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                @SuppressLint("InflateParams") val loginoptions = Objects.requireNonNull(li).inflate(R.layout.m_login_options, null, false)
                val Guest = loginoptions.findViewById<RadioButton>(R.id.Guest)
                val User = loginoptions.findViewById<RadioButton>(R.id.User)
                val id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android")
                Guest.setButtonDrawable(id)
                User.setButtonDrawable(id)
                Guest.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        listDialog.dismiss()
                        val intent = Intent(buttonView.context, CheckoutWeblink::class.java)
                        intent.putExtra("link", data.checkouturl)
                        intent.putExtra("id", data.checkoutId)
                        buttonView.context.startActivity(intent)
                    }
                }
                User.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        listDialog.dismiss()
                        val intent = Intent(buttonView.context, LoginActivity::class.java)
                        intent.putExtra("checkout_id", data.checkoutId)
                        buttonView.context.startActivity(intent)
                    }
                }
                listDialog.setContentView(loginoptions)
                listDialog.setCancelable(true)
                listDialog.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
