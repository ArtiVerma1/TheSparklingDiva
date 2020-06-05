package com.shopify.shopifyapp.basesection.viewmodels

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.GraphResponse
import com.shopify.buy3.MutationGraphCall
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.dbconnection.entities.AppLocalData
import com.shopify.shopifyapp.dbconnection.entities.CustomerTokenData
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.shopifyqueries.MutationQuery
import com.shopify.shopifyapp.utils.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SplashViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<LocalDbResponse>()
    private val apiresponseLiveData = MutableLiveData<ApiResponse>()
    private val fireBaseResponseMutableLiveData = MutableLiveData<FireBaseResponse>()
    val errorMessageResponse = MutableLiveData<String>()
    val isLogin: Boolean
        get() = repository.isLogin

    fun Response(shop:String): MutableLiveData<LocalDbResponse> {
        connectFirebaseForTrial(shop)
        return responseLiveData
    }

    fun firebaseResponse(): MutableLiveData<FireBaseResponse> {
        connectFireBaseForSplashData()
        return fireBaseResponseMutableLiveData
    }

    private fun connectFirebaseForTrial(shop:String) {
        try {
            MyApplication.dataBaseReference.child("additional_info").child("validity").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.getValue(Boolean::class.java)!!
                    val runnable = Runnable {
                        Log.i("MageNative:", "TrialExpired$value")
                        Log.i("MageNative:", "LocalData" + repository.localData)
                        var appLocalData: AppLocalData? = null
                        if (repository.localData.size == 0) {
                            appLocalData = AppLocalData()
                            appLocalData.isIstrialexpire = value
                            repository.insertData(appLocalData)
                        } else {
                            appLocalData = repository.localData[0]
                            appLocalData!!.isIstrialexpire = value
                            repository.updateData(appLocalData)
                        }
                        Log.i("MageNative:", "Currency" + appLocalData.currencycode)
                        disposables.add(repository.getSingle(appLocalData)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { result -> responseLiveData.setValue(LocalDbResponse.success(result)) },
                                        { throwable -> responseLiveData.setValue(LocalDbResponse.error(throwable)) }
                                ))
                    }
                    Thread(runnable).start()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i("DBConnectionError", "" + databaseError.details)
                    Log.i("DBConnectionError", "" + databaseError.message)
                    Log.i("DBConnectionError", "" + databaseError.code)
                }
            })
            MyApplication.dataBaseReference.child("additional_info").child("personalise").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                   Constant.ispersonalisedEnable = dataSnapshot.getValue(Boolean::class.java)!!
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i("DBConnectionError", "" + databaseError.details)
                    Log.i("DBConnectionError", "" + databaseError.message)
                    Log.i("DBConnectionError", "" + databaseError.code)
                }
            })
            MyApplication.dataBaseReference.child("additional_info").child("locale").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Constant.locale = dataSnapshot.getValue(String::class.java)!!
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i("DBConnectionError", "" + databaseError.details)
                    Log.i("DBConnectionError", "" + databaseError.message)
                    Log.i("DBConnectionError", "" + databaseError.code)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun connectFireBaseForSplashData() {
        try {
            MyApplication.dataBaseReference.child("additional_info").child("splash").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    disposables.add(Single.just(dataSnapshot)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { result -> fireBaseResponseMutableLiveData.setValue(FireBaseResponse.success(result)) },
                                    { throwable -> fireBaseResponseMutableLiveData.setValue(FireBaseResponse.error(throwable)) }
                            ))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i("DBConnectionError", "" + databaseError.details)
                    Log.i("DBConnectionError", "" + databaseError.message)
                    Log.i("DBConnectionError", "" + databaseError.code)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
    }

    fun refreshTokenIfRequired() {
        val runnable = Runnable {
            if (repository.accessToken[0].expireTime != null) {
                Log.i("Magenative", "ExpireTime" + repository.accessToken[0].expireTime)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                var expiretime: Date? = null
                try {
                    expiretime = sdf.parse(repository.accessToken[0].expireTime!!.split("t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                val currentDate = Date()
                val diff = expiretime!!.time - currentDate.time
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                Log.i("Magenative", "Days$days")
                if (days == 0L) {
                    renewToken(repository.accessToken[0].customerAccessToken)
                }
            }
        }
        Thread(runnable).start()
    }

    private fun renewToken(customerAccessToken: String?) {
        try {
            val call = repository.graphClient.mutateGraph(MutationQuery.renewToken(customerAccessToken))
            call.enqueue(Handler(Looper.getMainLooper())) { graphCallResult: GraphCallResult<Storefront.Mutation> -> this.invoke(graphCallResult) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private operator fun invoke(graphCallResult: GraphCallResult<Storefront.Mutation>): Unit {
        if (graphCallResult is GraphCallResult.Success<*>) {
            consumeResponse(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
        } else {
            consumeResponse(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
        }
        return Unit
    }

    private fun consumeResponse(reponse: GraphQLResponse) {
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
                    errorMessageResponse.setValue(errormessage.toString())
                    Log.i("MageNative", "" + errormessage)
                } else {
                    val errors = result.data!!.customerAccessTokenRenew.userErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.UserError
                            err += error.message
                        }
                        errorMessageResponse.setValue(err)
                        Log.i("MageNative", "" + err)
                    } else {
                        val token = result.data!!.customerAccessTokenRenew.customerAccessToken
                        val data = repository.accessToken[0]
                        data.customerAccessToken = token.accessToken
                        data.expireTime = token.expiresAt.toString()
                        repository.updateAccessToken(data)
                    }
                }
            }
            Status.ERROR -> {
                errorMessageResponse.setValue(reponse.error!!.error.message)
                Log.i("MageNative", "" + reponse.error.error.message)
            }
            else -> {
            }
        }
    }

    fun sendTokenToServer(unique_id: String) {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.i("MageNative", "token_error : " + task.exception!!)
                        return@OnCompleteListener
                    }
                    val token = task.result!!.token
                    Log.i("MageNative", "token$token")
                    FirebaseMessaging.getInstance().subscribeToTopic("magenativeANDROID")
                    disposables.add(repository.setDevice(Urls(MyApplication.context)!!.mid, token, " ", "android", unique_id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { result -> apiresponseLiveData.setValue(ApiResponse.success(result)) },
                                    { throwable -> apiresponseLiveData.setValue(ApiResponse.error(throwable)) }
                            ))
                })
    }
}



