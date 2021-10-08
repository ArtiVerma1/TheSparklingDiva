package com.shopify.shopifyapp.yotporewards.earnrewards

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopify.shopifyapp.network_transaction.CustomResponse
import com.shopify.shopifyapp.network_transaction.doRetrofitCall
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.utils.ApiResponse
import com.shopify.shopifyapp.utils.Urls
import com.google.gson.JsonElement
import io.reactivex.disposables.CompositeDisposable

class EarnRewardsViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    lateinit var context: Context
    var earnrewards = MutableLiveData<ApiResponse>()

    fun earnRewards() {
        doRetrofitCall(repository.earnRewards(Urls.XGUID, Urls.X_API_KEY), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                earnrewards.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                earnrewards.value = ApiResponse.error(error)
            }
        }, context = context)
    }

}