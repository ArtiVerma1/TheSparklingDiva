package com.shopify.shopifyapp.dbconnection.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.shopify.shopifyapp.dbconnection.entities.CartItemData

@Dao
interface CartItemDataDao {

    @get:Query("SELECT * FROM cartitemdata")
    val all: List<CartItemData>

    @Query("SELECT * FROM cartitemdata WHERE variant_id = :id")
    fun getSingleData(id: String): CartItemData

    @Insert
    fun insert(data: CartItemData)

    @Delete
    fun delete(data: CartItemData)

    @Update
    fun update(data: CartItemData)

    @Query("DELETE  FROM cartitemdata")
    fun deleteCart()

}
