package com.shopify.shopifyapp.loader_section

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.shopify.shopifyapp.R
import java.util.*

class CustomLoader(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(this.getWindow())!!.setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.activity_custom_loader)
        this.setCancelable(true);
    }
}