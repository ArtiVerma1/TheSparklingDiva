package com.shopify.shopifyapp.productsection.viewmodels

import android.os.Handler
import android.os.Looper

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.GraphResponse
import com.shopify.buy3.QueryGraphCall
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.shopifyqueries.Query
import com.shopify.shopifyapp.utils.GraphQLResponse
import com.shopify.shopifyapp.utils.Status

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProductListModel(private val repository: Repository) : ViewModel() {
    private var categoryID = ""
    var shopID = ""
    var presentmentCurrency: String? = null
    private var categoryHandle = ""
    var cursor = "nocursor"
        set(cursor) {
            field = cursor
            Response()
        }
    var isDirection = false
    var sortKeys: Storefront.ProductCollectionSortKeys = Storefront.ProductCollectionSortKeys.BEST_SELLING
    var keys: Storefront.ProductSortKeys = Storefront.ProductSortKeys.BEST_SELLING
    var number = 10
    private val disposables = CompositeDisposable()
    val message = MutableLiveData<String>()
    val filteredproducts = MutableLiveData<MutableList<Storefront.ProductEdge>>()
    fun getcategoryID(): String {
        return categoryID
    }

    fun setcategoryID(categoryID: String) {
        this.categoryID = categoryID
    }

    fun getcategoryHandle(): String {
        return categoryHandle
    }

    fun setcategoryHandle(categoryHandle: String) {
        this.categoryHandle = categoryHandle
    }

    fun Response() {
        setPresentmentCurrencyForModel()
        if (!getcategoryID().isEmpty()) {
            getProductsById()
        }
        if (!getcategoryHandle().isEmpty()) {
            getProductsByHandle()
        }
        if (!shopID.isEmpty()) {
            getAllProducts()
        }
    }

    private fun getAllProducts() {
        try {
            val call = repository.graphClient.queryGraph(Query.getAllProducts(cursor, keys, isDirection, number))
            call.enqueue(Handler(Looper.getMainLooper())) { result: GraphCallResult<Storefront.QueryRoot> -> this.invoke(result) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getProductsById() {
        try {
            val call = repository.graphClient.queryGraph(Query.getProductsById(getcategoryID(), cursor, sortKeys, isDirection, number))
            call.enqueue(Handler(Looper.getMainLooper())) { result: GraphCallResult<Storefront.QueryRoot> -> this.invoke(result) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getProductsByHandle() {
        try {
            val call = repository.graphClient.queryGraph(Query.getProductsByHandle(getcategoryHandle(), cursor, sortKeys, isDirection, number))
            call.enqueue(Handler(Looper.getMainLooper())) { result: GraphCallResult<Storefront.QueryRoot> -> this.invoke(result) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setPresentmentCurrencyForModel() {
        try {
            val runnable = Runnable {
                if (repository.localData[0].currencycode == null) {
                    presentmentCurrency = "nopresentmentcurrency"
                } else {
                    presentmentCurrency = repository.localData[0].currencycode
                }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private operator fun invoke(result: GraphCallResult<Storefront.QueryRoot>): Unit {
        if (result is GraphCallResult.Success<*>) {
            consumeResponse(GraphQLResponse.success(result as GraphCallResult.Success<*>))
        } else {
            consumeResponse(GraphQLResponse.error(result as GraphCallResult.Failure))
        }
        return Unit
    }

    private fun consumeResponse(reponse: GraphQLResponse) {
        when (reponse.status) {
            Status.SUCCESS -> {
                val result = (reponse.data as GraphCallResult.Success<Storefront.QueryRoot>).response
                if (result.hasErrors) {
                    val errors = result.errors
                    val iterator = errors.iterator()
                    val errormessage = StringBuilder()
                    var error: Error? = null
                    while (iterator.hasNext()) {
                        error = iterator.next()
                        errormessage.append(error.message())
                    }
                    message.setValue(errormessage.toString())
                } else {
                    var edges: List<Storefront.ProductEdge>? = null
                    if (!getcategoryHandle().isEmpty()) {
                        edges = result.data!!.collectionByHandle.products.edges
                    }
                    if (!getcategoryID().isEmpty()) {
                        edges = (result.data!!.node as Storefront.Collection).products.edges
                    }
                    if (!shopID.isEmpty()) {
                        edges = result.data!!.products.edges
                    }
                    filterProduct(edges)
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun filterProduct(list: List<Storefront.ProductEdge>?) {
        try {
            disposables.add(repository.getProductList(list!!)
                    .subscribeOn(Schedulers.io())
                    .filter { x -> x.node.availableForSale }
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result -> filteredproducts.setValue(result) })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
    }
}
