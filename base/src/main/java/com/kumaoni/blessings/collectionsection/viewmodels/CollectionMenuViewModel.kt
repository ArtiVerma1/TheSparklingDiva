package com.kumaoni.blessings.collectionsection.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonElement
import com.kumaoni.blessings.MyApplication
import com.kumaoni.blessings.network_transaction.CustomResponse
import com.kumaoni.blessings.network_transaction.doRetrofitCall
import com.kumaoni.blessings.repositories.Repository
import com.kumaoni.blessings.utils.ApiResponse
import com.kumaoni.blessings.utils.Urls
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