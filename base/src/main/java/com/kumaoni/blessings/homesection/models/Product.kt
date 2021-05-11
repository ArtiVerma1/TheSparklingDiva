package com.kumaoni.blessings.homesection.models

import android.content.Intent
import android.view.View

import com.kumaoni.blessings.basesection.models.ListData
import com.kumaoni.blessings.productsection.activities.ProductView
import com.kumaoni.blessings.utils.Constant

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
