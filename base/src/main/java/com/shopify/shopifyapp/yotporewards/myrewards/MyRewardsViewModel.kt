package com.shopify.shopifyapp.yotporewards.myrewards

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopify.shopifyapp.network_transaction.CustomResponse
import com.shopify.shopifyapp.network_transaction.doRetrofitCall
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.sharedprefsection.MagePrefs
import com.shopify.shopifyapp.utils.ApiResponse
import com.shopify.shopifyapp.utils.Urls
import com.google.gson.JsonElement
import io.reactivex.disposables.CompositeDisposable

class MyRewardsViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    lateinit var context: Context
    var myrewards = MutableLiveData<ApiResponse>()

    fun getMyRewards() {
        doRetrofitCall(repository.myrewards(Urls.XGUID, Urls.X_API_KEY, MagePrefs.getCustomerEmail()
                ?: "", MagePrefs.getCustomerID() ?: ""), disposables, object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                myrewards.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                myrewards.value = ApiResponse.error(error)
            }
        }, context = context)
    }
}