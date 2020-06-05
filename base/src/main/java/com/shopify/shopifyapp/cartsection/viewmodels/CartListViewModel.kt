package com.shopify.shopifyapp.cartsection.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.GraphResponse
import com.shopify.buy3.MutationGraphCall
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.graphql.support.ID
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.cartsection.models.CartListItem
import com.shopify.shopifyapp.dbconnection.entities.CartItemData
import com.shopify.shopifyapp.dbconnection.entities.ItemData
import com.shopify.shopifyapp.dependecyinjection.Body
import com.shopify.shopifyapp.dependecyinjection.InnerData
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.shopifyqueries.Mutation
import com.shopify.shopifyapp.utils.ApiResponse
import com.shopify.shopifyapp.utils.GraphQLResponse
import com.shopify.shopifyapp.utils.Status
import com.shopify.shopifyapp.utils.Urls
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class CartListViewModel(private val repository: Repository) : ViewModel() {
    private val data = MutableLiveData<Storefront.Checkout>()
    private val api = MutableLiveData<ApiResponse>()
    private val youmayapi = MutableLiveData<ApiResponse>()
    private val disposables = CompositeDisposable()
    val message = MutableLiveData<String>()
    val cartCount: Int
        get() {
            val count = intArrayOf(0)
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.allCartItems.size > 0) {
                        count[0] = repository.allCartItems.size
                    }
                    count[0]
                }
                val future = executor.submit(callable)
                count[0] = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return count[0]
        }
    private val lineItems: List<Storefront.CheckoutLineItemInput>
        get() {
            val checkoutLineItemInputs = ArrayList<Storefront.CheckoutLineItemInput>()
            try {
                var itemInput: Storefront.CheckoutLineItemInput? = null
                val dataList = repository.allCartItems
                val size = dataList.size
                for (i in 0 until size) {
                    itemInput = Storefront.CheckoutLineItemInput(dataList[i].qty, ID(dataList[i].variant_id))
                    checkoutLineItemInputs.add(itemInput)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return checkoutLineItemInputs
        }
    val presentCurrency: String
        get() {
            val currency = arrayOf("nopresentmentcurrency")
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.localData[0].currencycode != null) {
                        currency[0] = repository.localData[0].currencycode!!
                    }
                    currency[0]
                }
                val future = executor.submit(callable)
                currency[0] = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return currency[0]
        }
    val wishListcount: Int
        get() {
            val count = intArrayOf(0)
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.wishListData.size > 0) {
                        count[0] = repository.wishListData.size
                    }
                    count[0]
                }
                val future = executor.submit(callable)
                count[0] = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return count[0]
        }
    val isLoggedIn: Boolean
        get() = repository.isLogin

    fun Response(): MutableLiveData<Storefront.Checkout> {
        return data
    }
    fun getApiResponse(): MutableLiveData<ApiResponse> {
        return api
    }
    fun getYouMAyAPiResponse(): MutableLiveData<ApiResponse> {
        return youmayapi
    }
    fun prepareCart() {
        try {
            val runnable = object : Runnable {
                override fun run() {
                    val input = Storefront.CheckoutCreateInput()
                    input.lineItems = lineItems
                    try {
                        if (presentCurrency != "nopresentmentcurrency") {
                            val currencyCode = Storefront.CurrencyCode.valueOf(presentCurrency)
                            input.presentmentCurrencyCode = currencyCode
                        }
                        val call = repository.graphClient.mutateGraph(Mutation.createCheckout(input))
                        call.enqueue(Handler(Looper.getMainLooper())) { result: GraphCallResult<Storefront.Mutation> -> this.invoke(result) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                private operator fun invoke(result: GraphCallResult<Storefront.Mutation>): Unit {
                    if (result is GraphCallResult.Success<*>) {
                        consumeResponse(GraphQLResponse.success(result as GraphCallResult.Success<*>))
                    } else {
                        consumeResponse(GraphQLResponse.error(result as GraphCallResult.Failure))
                    }
                    return Unit
                }
            }
            Thread(runnable).start()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun moveToWishList(item: CartListItem) {
        try {
            val runnable = Runnable {
                if (repository.getSingleData(item.variant_id!!) == null) {
                    val data = ItemData()
                    data.variant_id = item.variant_id!!
                    data.productname = item.productname!!
                    data.normalprice = item.normalprice!!
                    if (item.specialprice != null) {
                        data.specialprice = item.specialprice!!
                        data.isSet_strike = true
                    } else {
                        data.specialprice = " "
                        data.isSet_strike = false
                    }
                    if (item.variant_one != null) {
                        data.variant_one = item.variant_one!!
                    } else {
                        data.variant_one = " "
                    }
                    if (item.variant_two != null) {
                        data.variant_two = item.variant_two!!
                    } else {
                        data.variant_two = " "
                    }
                    if (item.variant_three != null) {
                        data.variant_three = item.variant_three!!
                    } else {
                        data.variant_three = " "
                    }
                    if (item.offertext != null) {
                        data.offertext = item.offertext!!
                    } else {
                        data.offertext = " "
                    }
                    data.image = item.image!!
                    repository.insertWishListData(data)
                    Log.i("MageNative", "WishListCount : " + repository.wishListData.size)
                }
                removeFromCart(item)
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun removeFromCart(item: CartListItem) {
        try {
            val runnable = Runnable {
                val data = repository.getSingLeItem(item.variant_id!!)
                repository.deleteSingLeItem(data)
                prepareCart()
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun updateCart(item: CartListItem) {
        try {
            val runnable = Runnable {
                val data = repository.getSingLeItem(item.variant_id!!)
                data.qty = Integer.parseInt(item.qty!!)
                repository.updateSingLeItem(data)
                prepareCart()
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun consumeResponse(reponse: GraphQLResponse) {
        try {
            when (reponse.status) {
                Status.SUCCESS -> {
                    val result = (reponse.data as GraphCallResult.Success<Storefront.Mutation>).response
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
                        val payload = result.data!!.checkoutCreate
                        if (payload.checkoutUserErrors.size > 0) {
                            val iterator = payload.checkoutUserErrors.iterator()
                            var error: Storefront.CheckoutUserError? = null
                            while (iterator.hasNext()) {
                                error = iterator.next() as Storefront.CheckoutUserError
                                message.setValue(error.message)
                            }
                        } else {
                            val checkout = payload.checkout
                            getRecommendations(checkout)
                            getYouMayRecommendations(checkout)
                            data.setValue(checkout)
                        }
                    }
                }
                Status.ERROR -> message.setValue(reponse.error!!.error.message)
                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getYouMayRecommendations(checkout : Storefront.Checkout){
        RetrofitUrlManager.getInstance().putDomain("douban", Urls.PERSONALISED);
        try {
            var query= InnerData()
            query.id="query1"
            query.maxRecommendations=8
            query.recommendationType="cross_sell"
            var list= mutableListOf<Long>( )
            for (i in 0..checkout.lineItems.edges.size-1){
                var  s =  String(Base64.decode(checkout.lineItems.edges.get(i).node.variant.product.id.toString(),Base64.DEFAULT))
                list.add(s.replace("gid://shopify/Product/","").toLong())
            }
            query.productIds= list
            var body=Body()
            body.queries= mutableListOf(query)
            Log.i("Body",""+list)
            disposables.add(repository.getRecommendation(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->  youmayapi.setValue(ApiResponse.success(result))},
                            { throwable -> youmayapi.setValue(ApiResponse.error(throwable)) }
                    ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getRecommendations(checkout : Storefront.Checkout){
        RetrofitUrlManager.getInstance().putDomain("douban", Urls.PERSONALISED);
        try {
            var query= InnerData()
            query.id="query1"
            query.maxRecommendations=8
            query.recommendationType="bought_together"
            var list= mutableListOf<Long>( )
            for (i in 0..checkout.lineItems.edges.size-1){
                var  s =  String(Base64.decode(checkout.lineItems.edges.get(i).node.variant.product.id.toString(),Base64.DEFAULT))
                list.add(s.replace("gid://shopify/Product/","").toLong())
            }
            query.productIds= list
            var body=Body()
            body.queries= mutableListOf(query)
            Log.i("Body",""+list)
            disposables.add(repository.getRecommendation(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->  api.setValue(ApiResponse.success(result))},
                            { throwable -> api.setValue(ApiResponse.error(throwable)) }
                    ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onCleared() {
        disposables.clear()
    }
}
