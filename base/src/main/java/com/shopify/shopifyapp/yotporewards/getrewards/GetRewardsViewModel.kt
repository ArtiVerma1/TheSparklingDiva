package com.shopify.shopifyapp.yotporewards.getrewards

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

class GetRewardsViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    var getrewards = MutableLiveData<ApiResponse>()
    var redeemPoints = MutableLiveData<ApiResponse>()
    lateinit var context: Context

    fun getRewards() {
        doRetrofitCall(repository.getRewards(Urls.XGUID, Urls.X_API_KEY, MagePrefs.getCustomerEmail()
                ?: "", MagePrefs.getCustomerID()
                ?: ""), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                getrewards.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                getrewards.value = ApiResponse.error(error)
            }
        }, context = context)
    }

    fun redeemPoints(redeemptionId: String) {
        doRetrofitCall(repository.redeemPoints(Urls.XGUID, Urls.X_API_KEY, MagePrefs.getCustomerID()
                ?: "", MagePrefs.getCustomerEmail()
                ?: "", redeemptionId), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                redeemPoints.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                redeemPoints.value = ApiResponse.error(error)
            }
        }, context = context)
    }
}