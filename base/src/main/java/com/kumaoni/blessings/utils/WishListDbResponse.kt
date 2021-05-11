package com.kumaoni.blessings.utils

import com.kumaoni.blessings.dbconnection.entities.ItemData

import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable

import com.kumaoni.blessings.utils.Status.ERROR
import com.kumaoni.blessings.utils.Status.SUCCESS

class WishListDbResponse private constructor(val status: Status, @param:Nullable @field:Nullable
val data: List<ItemData>?, @param:Nullable @field:Nullable
                                             val error: String?) {
    companion object {
        fun success(@NonNull data: List<ItemData>): WishListDbResponse {
            return WishListDbResponse(SUCCESS, data, null)
        }

        fun error(@NonNull error: String): WishListDbResponse {
            return WishListDbResponse(ERROR, null, error)
        }
    }

}
