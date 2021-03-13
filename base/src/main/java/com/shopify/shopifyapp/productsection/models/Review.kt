package com.shopify.shopifyapp.productsection.models


import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("content")
    val content: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("outof")
    val outof: String?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("review_date")
    val reviewDate: String?,
    @SerializedName("reviewer_name")
    val reviewerName: String?
)