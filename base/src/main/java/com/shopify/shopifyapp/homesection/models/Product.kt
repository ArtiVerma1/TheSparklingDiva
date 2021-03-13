package com.shopify.shopifyapp.homesection.models

import android.content.Intent
import android.view.View

import com.shopify.shopifyapp.basesection.models.ListData
import com.shopify.shopifyapp.productsection.activities.ProductView
import com.shopify.shopifyapp.utils.Constant

class Product {
    fun productClick(view: View, data: ListData) {
        val productintent = Intent(view.context, ProductView::class.java)
        productintent.putExtra("ID", data.product!!.id.toString())
        productintent.putExtra("tittle", data.textdata)
        productintent.putExtra("product", data.product)
        view.context.startActivity(productintent)
        Constant.activityTransition(view.context)
    }
}
