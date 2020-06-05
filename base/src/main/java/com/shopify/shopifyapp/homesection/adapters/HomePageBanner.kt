package com.shopify.shopifyapp.homesection.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shopify.shopifyapp.homesection.fragments.BannerFragment
import org.json.JSONArray

class HomePageBanner(fm: FragmentManager, context: Context, private var items: JSONArray) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {

        val f1 = BannerFragment()
        val bundle = Bundle()
        try {
            Log.i("MageNative-items","url---"+items.getJSONObject(position).getString("image_url"))
            bundle.putString("banner_image", items.getJSONObject(position).getString("image_url"))
            Log.i("MageNative-items","link_type---"+items.getJSONObject(position).getString("link_type"))
            bundle.putString("link_to", items.getJSONObject(position).getString("link_type"))
            Log.i("MageNative-items","link_value---"+items.getJSONObject(position).getString("link_value"))
            bundle.putString("id", items.getJSONObject(position).getString("link_value"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        f1.arguments = bundle
        return f1
    }

    override fun getCount(): Int {
        Log.i("MageNative-items","size---"+items)
        Log.i("MageNative-items","size---"+items.length())
        return items.length()
    }
}
