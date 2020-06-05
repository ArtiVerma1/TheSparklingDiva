package com.shopify.shopifyapp.collectionsection.activities

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.basesection.activities.BaseActivity
import com.shopify.shopifyapp.collectionsection.adapters.CollectionRecylerAdapter
import com.shopify.shopifyapp.collectionsection.viewmodels.CollectionViewModel
import com.shopify.shopifyapp.databinding.MCollectionlistBinding
import com.shopify.shopifyapp.utils.ViewModelFactory

import javax.inject.Inject

class CollectionList : BaseActivity() {
    private var binding: MCollectionlistBinding? = null
    @Inject
    lateinit var factory: ViewModelFactory
    private var model: CollectionViewModel? = null
    @Inject
    lateinit var adapter: CollectionRecylerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_collectionlist, group, true)
        showBackButton()
        showTittle(resources.getString(R.string.collection))
        setLayout(binding!!.categorylist, "3grid")
        (application as MyApplication).mageNativeAppComponent!!.doCollectionInjection(this)
        model = ViewModelProviders.of(this, factory).get(CollectionViewModel::class.java)
        model!!.Response().observe(this, Observer<List<Storefront.CollectionEdge>> { this.setRecylerData(it) })
        model!!.message.observe(this, Observer<String> { this.showToast(it) })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun setRecylerData(collections: List<Storefront.CollectionEdge>) {
        try {
            if (collections.size > 0) {
                Log.i("MageNative", "images" + collections.size)
                Log.i("MageNative", "collection id" + collections.get(0).node.id)
                adapter!!.setData(collections, this)
                binding!!.categorylist.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else {
                showToast(resources.getString(R.string.nocollection))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
