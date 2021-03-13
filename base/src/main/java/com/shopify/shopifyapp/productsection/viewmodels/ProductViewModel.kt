package com.shopify.shopifyapp.productsection.viewmodels

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonElement

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.QueryGraphCall
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.dbconnection.entities.CartItemData
import com.shopify.shopifyapp.dbconnection.entities.ItemData
import com.shopify.shopifyapp.dependecyinjection.Body
import com.shopify.shopifyapp.dependecyinjection.InnerData
import com.shopify.shopifyapp.network_transaction.CustomResponse
import com.shopify.shopifyapp.network_transaction.doGraphQLQueryGraph
import com.shopify.shopifyapp.network_transaction.doRetrofitCall
import com.shopify.shopifyapp.productsection.models.VariantData
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.shopifyqueries.Query
import com.shopify.shopifyapp.utils.ApiResponse
import com.shopify.shopifyapp.utils.GraphQLResponse
import com.shopify.shopifyapp.utils.Urls
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import me.jessyan.retrofiturlmanager.RetrofitUrlManager

class ProductViewModel(private val repository: Repository) : ViewModel() {
    var handle = ""
    var id = ""
    var presentmentCurrency: String? = null
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<GraphQLResponse>()
    var reviewResponse: MutableLiveData<ApiResponse>? = MutableLiveData<ApiResponse>()
    var reviewBadges: MutableLiveData<ApiResponse>? = MutableLiveData<ApiResponse>()
    var createreviewResponse = MutableLiveData<ApiResponse>()
    lateinit var context: Context
    val filteredlist = MutableLiveData<List<Storefront.ProductVariantEdge>>()


    fun getReviews(mid: String, product_id: String): MutableLiveData<ApiResponse> {
        getProductReviews(mid, product_id)
        return reviewResponse!!
    }

    fun getReviewBadges(mid: String, product_id: String): MutableLiveData<ApiResponse> {
        getbadgeReviews(mid, product_id)
        return reviewBadges!!
    }

    fun getProductReviews(mid: String, product_id: String) {

        doRetrofitCall(repository.getProductReviews(mid, product_id), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                reviewResponse?.setValue(ApiResponse.success(result))
            }

            override fun onErrorRetrofit(error: Throwable) {
                reviewResponse?.setValue(ApiResponse.error(error))
            }
        }, context = context)

    }


    fun getbadgeReviews(mid: String, product_id: String) {

        doRetrofitCall(repository.getbadgeReviews(mid, product_id), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                reviewBadges?.setValue(ApiResponse.success(result))
            }

            override fun onErrorRetrofit(error: Throwable) {
                reviewBadges?.setValue(ApiResponse.error(error))
            }
        }, context = context)

    }

    fun getcreateReview(mid: String, reviewRating: String, product_id: String, reviewAuthor: String, reviewEmail: String, reviewTitle: String, reviewBody: String) {
        doRetrofitCall(repository.getcreateReview(mid, reviewRating, product_id, reviewAuthor, reviewEmail, reviewTitle, reviewBody), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                createreviewResponse.setValue(ApiResponse.success(result))
            }

            override fun onErrorRetrofit(error: Throwable) {
                createreviewResponse.setValue(ApiResponse.error(error))
            }
        }, context = context)
    }

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

    fun Response(): MutableLiveData<GraphQLResponse> {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentCurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentCurrency!!))
        }
        if (!id.isEmpty()) {
            getProductsById(currency_list)
        }
        if (!handle.isEmpty()) {
            getProductsByHandle(currency_list)
        }
        return responseLiveData
    }

    private fun getProductsById(currency_list: ArrayList<Storefront.CurrencyCode>) {
        try {
            doGraphQLQueryGraph(repository, Query.getProductById(id, currency_list), customResponse = object : CustomResponse {
                override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                    invoke(result)
                }
            }, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getProductsByHandle(currency_list: ArrayList<Storefront.CurrencyCode>) {
        try {
            doGraphQLQueryGraph(repository, Query.getProductByHandle(handle, currency_list), customResponse = object : CustomResponse {
                override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                    invoke(result)
                }
            }, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private operator fun invoke(result: GraphCallResult<Storefront.QueryRoot>): Unit {
        if (result is GraphCallResult.Success<*>) {
            responseLiveData.setValue(GraphQLResponse.success(result as GraphCallResult.Success<*>))
        } else {
            responseLiveData.setValue(GraphQLResponse.error(result as GraphCallResult.Failure))
        }
        return Unit
    }

    fun setPresentmentCurrencyForModel(): Boolean {
        val isadded = booleanArrayOf(false)
        try {
            val executor = Executors.newSingleThreadExecutor()
            val callable = Callable {
                if (repository.localData[0].currencycode == null) {
                    presentmentCurrency = "nopresentmentcurrency"
                } else {
                    presentmentCurrency = repository.localData[0].currencycode
                }
                isadded[0] = true
                isadded[0]
            }
            val future = executor.submit(callable)
            isadded[0] = future.get()
            executor.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isadded[0]
    }

    fun setWishList(product_id: String): Boolean {
        val isadded = booleanArrayOf(false)
        try {
            val executor = Executors.newSingleThreadExecutor()
            val callable = Callable {
                if (repository.getSingleData(product_id) == null) {
                    Log.i("MageNative", "WishListCount : " + repository.wishListData.size)
                    val data = ItemData()
                    data.product_id = product_id
                    repository.insertWishListData(data)
                    Log.i("MageNative", "WishListCount 2: " + repository.wishListData.size)
                    isadded[0] = true
                }
                isadded[0]
            }
            val future = executor.submit(callable)
            isadded[0] = future.get()
            executor.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isadded[0]
    }

    fun filterList(list: List<Storefront.ProductVariantEdge>) {
        try {
            disposables.add(repository.getList(list)
                    .subscribeOn(Schedulers.io())
                    .filter { x -> x.node.availableForSale }
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result -> filteredlist.setValue(result) })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
    }

    public fun isInwishList(variantId: String): Boolean {
        val isadded = booleanArrayOf(false)
        try {
            val executor = Executors.newSingleThreadExecutor()
            val callable = Callable {
                if (repository.getSingleData(variantId) != null) {

                    Log.i("MageNative", "item already in wishlist : ")
                    isadded[0] = true
                }
                isadded[0]
            }
            val future = executor.submit(callable)
            isadded[0] = future.get()
            executor.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isadded[0]
    }

    fun addToCart(variantId: String) {
        try {
            val runnable = Runnable {
                val data: CartItemData
                if (repository.getSingLeItem(variantId) == null) {
                    data = CartItemData()
                    data.variant_id = variantId
                    data.qty = 1
                    repository.addSingLeItem(data)
                } else {
                    data = repository.getSingLeItem(variantId)
                    val qty = data.qty + 1
                    data.qty = qty
                    repository.updateSingLeItem(data)
                }
                Log.i("MageNative", "CartCount : " + repository.allCartItems.size)
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private val api = MutableLiveData<ApiResponse>()
    fun getApiResponse(): MutableLiveData<ApiResponse> {
        return api
    }

    fun getRecommendations(id: String) {
        RetrofitUrlManager.getInstance().putDomain("douban", Urls.PERSONALISED);
        try {
            var query = InnerData()
            query.id = "query1"
            query.maxRecommendations = 8
            query.recommendationType = "similar_products"
            var list = mutableListOf<Long>()
            var s = String(Base64.decode(id, Base64.DEFAULT))
            list.add(s.replace("gid://shopify/Product/", "").toLong())
            query.productIds = list
            var body = Body()
            body.queries = mutableListOf(query)
            Log.i("Body", "" + list)
            doRetrofitCall(repository.getRecommendation(body), disposables, customResponse = object : CustomResponse {
                override fun onSuccessRetrofit(result: JsonElement) {
                    api.setValue(ApiResponse.success(result))
                }

                override fun onErrorRetrofit(error: Throwable) {
                    api.setValue(ApiResponse.error(error))
                }
            }, context = context)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
