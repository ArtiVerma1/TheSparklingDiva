package com.shopify.shopifyapp.utils
import com.google.gson.JsonElement
import io.reactivex.annotations.NonNull
import com.shopify.shopifyapp.utils.Status.ERROR
import com.shopify.shopifyapp.utils.Status.LOADING
import com.shopify.shopifyapp.utils.Status.SUCCESS
class ApiResponse private constructor(val status: Status, val data: JsonElement?,val error: Throwable?) {
    companion object {
        fun loading(): ApiResponse {
            return ApiResponse(LOADING, null, null)
        }

        fun success(@NonNull data: JsonElement): ApiResponse {
            return ApiResponse(SUCCESS, data, null)
        }

        fun error(@NonNull error: Throwable): ApiResponse {
            return ApiResponse(ERROR, null, error)
        }
    }
}
