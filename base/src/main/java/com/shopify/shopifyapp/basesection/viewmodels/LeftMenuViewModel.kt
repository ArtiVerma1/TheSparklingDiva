package com.shopify.shopifyapp.basesection.viewmodels
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.GraphResponse
import com.shopify.buy3.QueryGraphCall
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.dbconnection.entities.AppLocalData
import com.shopify.shopifyapp.dbconnection.entities.LivePreviewData
import com.shopify.shopifyapp.dbconnection.entities.UserLocalData
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.shopifyqueries.Query
import com.shopify.shopifyapp.utils.ApiResponse
import com.shopify.shopifyapp.utils.GraphQLResponse
import com.shopify.shopifyapp.utils.Status
import com.shopify.shopifyapp.utils.Urls

import java.util.HashMap
import java.util.Objects

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class LeftMenuViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<ApiResponse>()
    val message = MutableLiveData<String>()
    val data = MutableLiveData<HashMap<String, String>>()
    private val currencyResponseLiveData = MutableLiveData<List<Storefront.CurrencyCode>>()
    private val handler = Handler()
    val isLoggedIn: Boolean
        get() = repository.isLogin

    fun Response(): MutableLiveData<ApiResponse> {
        getMenus()
        return responseLiveData
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
    fun fetchUserData() {
        try {
            val runnable = Runnable {
                val hashdata = HashMap<String, String>()
                if (repository.isLogin) {
                    val localData = repository.allUserData[0]
                    hashdata.put("firstname", localData.firstname!!)
                    hashdata.put("secondname", localData.lastname!!)
                    hashdata.put("tag", "login")
                    Log.i("MageNative", "LeftMenuResume 2" + localData.firstname!!)

                } else {
                    Log.i("MageNative", "LeftMenuResume 2" + "Sign")
                    hashdata["firstname"] = "Sign"
                    hashdata["secondname"] = "In"
                    hashdata["tag"] = "Sign In"
                }
                handler.post { data.value = hashdata }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getMenus() {
        try {
            disposables.add(repository.getMenus(Urls(MyApplication.context)!!.mid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                            { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
                    ))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun currencyResponse(): MutableLiveData<List<Storefront.CurrencyCode>> {
        getCurrency()
        return currencyResponseLiveData
    }

    private fun getCurrency() {
        try {
            val call = repository.graphClient.queryGraph(Query.shopDetails)
            call.enqueue(Handler(Looper.getMainLooper())) { result: GraphCallResult<Storefront.QueryRoot> -> this.invoke(result) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
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
                    currencyResponseLiveData.setValue(Objects.requireNonNull<Storefront.QueryRoot>(result.data).getShop().getPaymentSettings().getEnabledPresentmentCurrencies())
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun setCurrencyData(currencyCode: String?) {
        val runnable = Runnable {
            val appLocalData = repository.localData[0]
            appLocalData.currencycode = currencyCode
            repository.updateData(appLocalData)
        }
        Thread(runnable).start()

    }

    fun logOut() {
        val runnable = Runnable {
            Log.i("MageNative", "LeftMenuResume 5")
            repository.deletecart()
            repository.deleteWishListData()
            repository.deleteUserData()
            fetchUserData()
        }
        Thread(runnable).start()
    }
    fun insertPreviewData(data:JSONObject){
        val runnable = Runnable {
            var lpreview=repository.getPreviewData()
            if(lpreview.size==0){
                var preview =LivePreviewData(data.getString("mid"),data.getString("shopUrl"),data.getString("token"))
                repository.insertPreviewData(preview)
            }else{
                var preview=lpreview.get(0)
                preview.mid=data.getString("mid")
                preview.shopurl=data.getString("shopUrl")
                preview.apikey=data.getString("token")
                repository.updatePreviewData(preview)
            }
        }
        Thread(runnable).start()
    }
}
