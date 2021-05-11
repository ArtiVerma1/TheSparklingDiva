package com.shopify.shopifyapp.collectionsection.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonElement
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.network_transaction.CustomResponse
import com.shopify.shopifyapp.network_transaction.doRetrofitCall
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.utils.ApiResponse
import com.shopify.shopifyapp.utils.Urls
import io.reactivex.disposables.CompositeDisposable

class CollectionMenuViewModel(private val repository: Repository) : ViewModel() {
    private val responseLiveData = MutableLiveData<ApiResponse>()
    val message = MutableLiveData<String>()
    private val disposables = CompositeDisposable()
    var context: Context? = null

    val isLoggedIn: Boolean
        get() = repository.isLogin

    fun Response(): MutableLiveData<ApiResponse> {
        getMenus()
        return responseLiveData
    }

    private fun getMenus() {
        try {
            doRetrofitCall(repository.getMenus(Urls(MyApplication.context)!!.mid), disposables, customResponse = object : CustomResponse {
                override fun onSuccessRetrofit(result: JsonElement) {
                    responseLiveData.setValue(ApiResponse.success(result))
                }

                override fun onErrorRetrofit(error: Throwable) {
                    responseLiveData.setValue(ApiResponse.error(error))
                }
            }, context = context!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}