package com.shopify.shopifyapp.productsection.fragments

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

import java.util.Objects

class ImageFragment(var images: List<Storefront.ImageEdge>) : BaseFragment(), View.OnClickListener {
    private val TAG = "ImageFragment"
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
      //  Log.d(TAG, "onClick: " + images)
    }
}