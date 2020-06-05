package com.shopify.shopifyapp.dbconnection.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity
class ItemData : Serializable {
    @PrimaryKey
    @ColumnInfo(name = "variant_id")
    lateinit var variant_id: String
    @ColumnInfo(name = "productname")
    lateinit var productname: String
    @ColumnInfo(name = "normalprice")
    lateinit var normalprice: String
    @ColumnInfo(name = "specialprice")
    lateinit var specialprice: String
    @ColumnInfo(name = "variant_one")
    lateinit var variant_one: String
    @ColumnInfo(name = "variant_two")
    lateinit var variant_two: String
    @ColumnInfo(name = "variant_three")
    lateinit var variant_three: String
    @ColumnInfo(name = "set_strike")
     var isSet_strike: Boolean = false
    @ColumnInfo(name = "image")
    lateinit var image: String
    @ColumnInfo(name = "offertext")
    lateinit var offertext: String
}
