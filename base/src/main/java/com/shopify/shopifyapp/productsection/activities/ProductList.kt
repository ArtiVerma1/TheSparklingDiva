package com.shopify.shopifyapp.productsection.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MProductlistitemBinding
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity
import com.shopify.shopifyapp.cartsection.activities.CartList
import com.shopify.shopifyapp.databinding.SortDialogLayoutBinding
import com.shopify.shopifyapp.productsection.adapters.ProductRecyclerListAdapter
import com.shopify.shopifyapp.productsection.adapters.ProductRecylerGridAdapter
import com.shopify.shopifyapp.productsection.viewmodels.ProductListModel
import com.shopify.shopifyapp.utils.Constant
import com.shopify.shopifyapp.utils.ViewModelFactory
import kotlinx.android.synthetic.main.m_productmain.view.*

import javax.inject.Inject

class ProductList : NewBaseActivity() {
    private var binding: MProductlistitemBinding? = null
    private var productlist: RecyclerView? = null

    @Inject
    lateinit var factory: ViewModelFactory
    var productListModel: ProductListModel? = null
    private var products: MutableList<Storefront.ProductEdge>? = null
    private var productcursor: String? = null
    private var listEnabled: Boolean = false

    @Inject
    lateinit var product_grid_adapter: ProductRecylerGridAdapter

    @Inject
    lateinit var product_list_adapter: ProductRecyclerListAdapter
    private var flag = true
    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView.layoutManager!!.childCount
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            var firstVisibleItemPosition = 0
            if (recyclerView.layoutManager is LinearLayoutManager) {
                firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            } else if (recyclerView.layoutManager is GridLayoutManager) {
                firstVisibleItemPosition = (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
            }
            if (!recyclerView.canScrollVertically(1)) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition > 0
                        && totalItemCount >= products!!.size) {
                    productListModel!!.number = 20
                    productListModel!!.cursor = productcursor!!
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_productlistitem, group, true)
        productlist = setLayout(binding!!.root.findViewById(R.id.productlist), "grid")
        showBackButton()
        if (intent.hasExtra("tittle") && intent.getStringExtra("tittle") != null) {
            showTittle(intent.getStringExtra("tittle"))
        }
        (application as MyApplication).mageNativeAppComponent!!.doProductListInjection(this)
        productListModel = ViewModelProvider(this, factory).get(ProductListModel::class.java)
        productListModel!!.context = this
        productListModel?.collectionData?.observe(this, Observer { this.collectionResponse(it) })
        if (intent.getStringExtra("ID") != null) {
            productListModel!!.setcategoryID(intent.getStringExtra("ID"))
        }
        if (intent.getStringExtra("handle") != null) {
            productListModel!!.setcategoryHandle(intent.getStringExtra("handle"))
        }
        if (intent.getStringExtra("ID") == null && intent.getStringExtra("handle") == null) {
            productListModel!!.shopID = "allproduct"
            flag = false
        }
        productListModel!!.message.observe(this, Observer { this.showToast(it) })
        productListModel!!.Response()
        productListModel!!.filteredproducts.observe(this, Observer<MutableList<Storefront.ProductEdge>> { this.setRecylerData(it) })
        productlist!!.addOnScrollListener(recyclerViewOnScrollListener)
        binding?.mainview?.sort_but?.setOnClickListener {
            openSortDialog()
        }
        binding?.mainview?.grid_but?.setOnClickListener {
            listEnabled = false
            products = null
            productListModel!!.cursor = "nocursor"
            binding?.mainview?.productListContainer?.visibility = View.GONE
            productlist = setLayout(binding!!.root.findViewById(R.id.productlist), "grid")
        }
        binding?.mainview?.list_but?.setOnClickListener {
            productlist?.layoutManager = LinearLayoutManager(this)
            listEnabled = true
            products = null
            binding?.mainview?.productListContainer?.visibility = View.GONE
            productListModel!!.cursor = "nocursor"
        }
    }

    private fun openSortDialog() {
        var dialog = BottomSheetDialog(this, R.style.WideDialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        var sortDialogLayoutBinding = DataBindingUtil.inflate<SortDialogLayoutBinding>(layoutInflater, R.layout.sort_dialog_layout, null, false)
        dialog.setContentView(sortDialogLayoutBinding.root)
        sortDialogLayoutBinding.atoz.setOnClickListener {
            if (flag) {
                productListModel!!.sortKeys = Storefront.ProductCollectionSortKeys.TITLE
            } else {
                productListModel!!.keys = Storefront.ProductSortKeys.TITLE
            }
            productListModel!!.isDirection = false
            products = null
            productListModel!!.number = 10
            productListModel!!.cursor = "nocursor"
            dialog.dismiss()
        }
        sortDialogLayoutBinding.ztoa.setOnClickListener {
            if (flag) {
                productListModel!!.sortKeys = Storefront.ProductCollectionSortKeys.TITLE
            } else {
                productListModel!!.keys = Storefront.ProductSortKeys.TITLE
            }
            productListModel!!.isDirection = true
            products = null
            productListModel!!.number = 10
            productListModel!!.cursor = "nocursor"
            dialog.dismiss()
        }
        sortDialogLayoutBinding.htol.setOnClickListener {
            if (flag) {
                productListModel!!.sortKeys = Storefront.ProductCollectionSortKeys.PRICE
            } else {
                productListModel!!.keys = Storefront.ProductSortKeys.PRICE
            }
            productListModel!!.isDirection = true
            products = null
            productListModel!!.number = 10
            productListModel!!.cursor = "nocursor"
            dialog.dismiss()
        }
        sortDialogLayoutBinding.ltoh.setOnClickListener {
            if (flag) {
                productListModel!!.sortKeys = Storefront.ProductCollectionSortKeys.PRICE
            } else {
                productListModel!!.keys = Storefront.ProductSortKeys.PRICE
            }
            productListModel!!.isDirection = false
            products = null
            productListModel!!.number = 10
            productListModel!!.cursor = "nocursor"
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun collectionResponse(it: Storefront.Collection?) {
        if (it?.title != null) {
            showTittle(it?.title)
        }
    }

    override fun onResume() {
        super.onResume()
        if (textView != null) {
            textView!!.text = "" + productListModel!!.cartCount
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_product, menu)
        val item = menu.findItem(R.id.cart_item)
        item.setActionView(R.layout.m_count)
        val notifCount = item.actionView
        textView = notifCount.findViewById<TextView>(R.id.count)
        textView?.text = "" + cartCount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this, CartList::class.java)
            startActivity(mycartlist)
            Constant.activityTransition(this)
        }
        return true
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun setRecylerData(products: MutableList<Storefront.ProductEdge>) {
        try {
            if (products.size > 0) {
                binding?.mainview?.productListContainer?.visibility = View.VISIBLE
                if (!listEnabled) {
                    product_grid_adapter!!.presentmentcurrency = productListModel!!.presentmentCurrency
                    if (this.products == null) {
                        this.products = products
                        product_grid_adapter!!.setData(this.products, this@ProductList, productListModel!!.repository)
                        productlist!!.adapter = product_grid_adapter
                    } else {
                        this.products!!.addAll(products)
                        product_grid_adapter!!.notifyDataSetChanged()
                    }
                    productcursor = this.products!![this.products!!.size - 1].cursor
                    Log.i("MageNative", "Cursor : " + productcursor!!)
                } else {
                    product_list_adapter!!.presentmentcurrency = productListModel!!.presentmentCurrency
                    if (this.products == null) {
                        this.products = products
                        product_list_adapter!!.setData(this.products, this@ProductList, productListModel!!.repository)
                        productlist!!.adapter = product_list_adapter
                    } else {
                        this.products!!.addAll(products)
                        product_list_adapter!!.notifyDataSetChanged()
                    }
                    productcursor = this.products!![this.products!!.size - 1].cursor
                    Log.i("MageNative", "Cursor : " + productcursor!!)
                }
            } else {
                showToast(resources.getString(R.string.noproducts))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
