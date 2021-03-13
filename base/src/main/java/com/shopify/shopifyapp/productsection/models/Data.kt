package com.shopify.shopifyapp.productsection.models


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("reviews")
    val reviews: List<Review>?
)