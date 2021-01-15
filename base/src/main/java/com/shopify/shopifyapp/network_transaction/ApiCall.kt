package com.shopify.shopifyapp.network_transaction

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.JsonElement
import com.lesprimeurs.app.loader_section.CustomLoader
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.utils.ApiResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


var customLoader: CustomLoader? = null
fun ViewModel.doGraphQLMutateGraph(repository: Repository, query: Storefront.MutationQuery, customResponse: CustomResponse, context: Context) {
    GlobalScope.launch(Dispatchers.Main) {
        if (customLoader != null) {
            customLoader!!.dismiss()
            customLoader = null
        }
        customLoader = CustomLoader(context)
        customLoader!!.show()
    }

    var call = repository.graphClient.mutateGraph(query)
    call.enqueue { result: GraphCallResult<Storefront.Mutation> ->
        GlobalScope.launch(Dispatchers.Main) {
            customLoader!!.dismiss()
            customResponse.onSuccessMutate(result)

        }
    }
}

fun ViewModel.doGraphQLQueryGraph(repository: Repository, query: Storefront.QueryRootQuery, customResponse: CustomResponse, context: Context) {
    GlobalScope.launch(Dispatchers.Main) {
        if (customLoader != null) {
            customLoader!!.dismiss()
            customLoader = null
        }
        customLoader = CustomLoader(context)
        customLoader!!.show()
    }
    var call = repository.graphClient.queryGraph(query)
    call.enqueue { result: GraphCallResult<Storefront.QueryRoot> ->
        GlobalScope.launch(Dispatchers.Main) {
            customLoader!!.dismiss()
            customResponse.onSuccessQuery(result)

        }
    }
}

fun ViewModel.doRetrofitCall(postData: Single<JsonElement>, disposables: CompositeDisposable, customResponse: CustomResponse, context: Context) {
    GlobalScope.launch(Dispatchers.Main) {
        if (customLoader != null) {
            customLoader!!.dismiss()
            customLoader = null
        }
        customLoader = CustomLoader(context)
        customLoader!!.show()
    }
    disposables.add(postData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        customLoader!!.dismiss()
                        customResponse.onSuccessRetrofit(result)
                    },
                    { throwable ->
                        customLoader!!.dismiss()
                        customResponse.onErrorRetrofit(throwable)
                    }
            ))
}