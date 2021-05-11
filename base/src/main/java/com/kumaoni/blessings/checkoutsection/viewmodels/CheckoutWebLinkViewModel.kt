package com.kumaoni.blessings.checkoutsection.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonElement
import com.kumaoni.blessings.MyApplication.Companion.context
import com.kumaoni.blessings.dbconnection.entities.CustomerTokenData
import com.kumaoni.blessings.dbconnection.entities.UserLocalData
import com.kumaoni.blessings.network_transaction.CustomResponse
import com.kumaoni.blessings.network_transaction.doRetrofitCall
import com.kumaoni.blessings.repositories.Repository
import com.kumaoni.blessings.utils.ApiResponse
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CheckoutWebLinkViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<ApiResponse>()
    lateinit var context: Context
    var customeraccessToken: CustomerTokenData
        get() {
            return repository.accessToken[0]
        }
        set(value) {}
    val isLoggedIn: Boolean
        get() = repository.isLogin
    val data: UserLocalData?
        get() {
            val user = arrayOf<UserLocalData>()
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    user[0] = repository.allUserData[0]
                    user[0]
                }
                val future = executor.submit(callable)
                user[0] = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return user[0]
        }

    fun setOrder(mid: String, checkout_token: String?) {
        try {
            var postData = repository.setOrder(mid, checkout_token)
//            doRetrofitCall(postData, disposables, customResponse = object : CustomResponse {
//                override fun onSuccessRetrofit(result: JsonElement) {
//                    responseLiveData.setValue(ApiResponse.success(result))
//                }
//
//                override fun onErrorRetrofit(error: Throwable) {
//                    responseLiveData.setValue(ApiResponse.error(error))
//                }
//            }, context = context)

            disposables.add(postData
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                responseLiveData.value = ApiResponse.success(result)
                            },
                            { throwable ->
                                responseLiveData.value = ApiResponse.error(throwable)
                            }
                    ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteCart() {
        try {
            val runnable = Runnable { repository.deletecart() }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
    }
}
