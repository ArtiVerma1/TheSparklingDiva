package com.shopify.shopifyapp.checkoutsection.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopify.shopifyapp.dbconnection.entities.UserLocalData
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.utils.ApiResponse
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
            disposables.add(repository.setOrder(mid, checkout_token)
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
