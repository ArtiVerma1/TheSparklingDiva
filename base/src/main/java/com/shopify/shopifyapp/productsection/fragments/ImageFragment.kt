package com.shopify.shopifyapp.productsection.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shopify.buy3.Storefront

import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MImagefragmentBinding
import com.shopify.shopifyapp.basesection.fragments.BaseFragment
import com.shopify.shopifyapp.basesection.models.CommanModel
import com.shopify.shopifyapp.productsection.activities.ZoomActivity
import com.shopify.shopifyapp.utils.Constant

import java.util.Objects

class ImageFragment(var images: List<Storefront.ImageEdge>) : BaseFragment(), View.OnClickListener {
    private val TAG = "ImageFragment"
    private var image_list: ArrayList<String>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<MImagefragmentBinding>(layoutInflater, R.layout.m_imagefragment, null, false)
        val url = Objects.requireNonNull<Bundle>(arguments).getString("url")
        val model = CommanModel()
        model.imageurl = url!!
        binding.commondata = model
        binding.image.setOnClickListener(this)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onClick(v: View?) {
        image_list = ArrayList<String>()
        images.forEach {
            image_list?.add(it.node.originalSrc)
        }
        var intent = Intent(context, ZoomActivity::class.java)
        intent.putStringArrayListExtra("images", image_list)
        context?.startActivity(intent)
        Constant.activityTransition(v?.context!!)
    }
}