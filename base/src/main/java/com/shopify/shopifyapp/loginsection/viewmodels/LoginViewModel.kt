package com.shopify.shopifyapp.loginsection.viewmodels

import android.os.Handler
import android.os.Looper

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.GraphResponse
import com.shopify.buy3.MutationGraphCall
import com.shopify.buy3.QueryGraphCall
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.shopifyapp.dbconnection.entities.CustomerTokenData
import com.shopify.shopifyapp.dbconnection.entities.UserLocalData
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.shopifyqueries.MutationQuery
import com.shopify.shopifyapp.shopifyqueries.Query
import com.shopify.shopifyapp.utils.GraphQLResponse
import com.shopify.shopifyapp.utils.Status

class LoginViewModel(private val repository: Repository) : ViewModel() {
    private val responsedata = MutableLiveData<Storefront.CustomerAccessToken>()
    private val response = MutableLiveData<Storefront.Customer>()
    val errormessage = MutableLiveData<String>()
    private var username = ""
    private var password = ""
    fun getResponsedata_(): MutableLiveData<Storefront.Customer> {
        return response
    }

    fun Response(): MutableLiveData<Storefront.CustomerAccessToken> {
        return responsedata
    }

    fun getUser(username: String, password: String) {
        this.username = username
        this.password = password
        getLoginData(username, password)
    }

    private fun getLoginData(username: String, password: String) {
        try {
            val call = repository.graphClient.mutateGraph(MutationQuery.getLoginDetails(username, password))
            call.enqueue(Handler(Looper.getMainLooper())) { graphCallResult: GraphCallResult<Storefront.Mutation> -> this.invoke(graphCallResult) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private operator fun invoke(graphCallResult: GraphCallResult<Storefront.Mutation>): Unit {
        if (graphCallResult is GraphCallResult.Success<*>) {
            consumeResponseLogin(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
        } else {
            consumeResponseLogin(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
        }
        return Unit
    }

    private fun consumeResponseLogin(reponse: GraphQLResponse) {
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
                    this.errormessage.setValue(errormessage.toString())
                } else {
                    val errors = result.data!!.customerAccessTokenCreate.customerUserErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.CustomerUserError
                            err += error.message
                        }
                        errormessage.setValue(err)
                    } else {
                        responsedata.setValue(result.data!!.customerAccessTokenCreate.customerAccessToken)
                    }
                }
            }
            Status.ERROR -> errormessage.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun savetoken(token: Storefront.CustomerAccessToken) {
        val customerTokenData = CustomerTokenData(token.accessToken, token.expiresAt.toLocalDateTime().toString())
        repository.saveaccesstoken(customerTokenData)
        try {
            val call = repository.graphClient.queryGraph(Query.getCustomerDetails(token.accessToken))
            call.enqueue(Handler(Looper.getMainLooper())) { graphCallResult: GraphCallResult<Storefront.QueryRoot> -> this.invokes(graphCallResult) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun invokes(graphCallResult: GraphCallResult<Storefront.QueryRoot>): Unit {
        if (graphCallResult is GraphCallResult.Success<*>) {
            MapLoginDetails(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
        } else {
            MapLoginDetails(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
        }
        return Unit
    }

    private fun MapLoginDetails(graphQLResponse: GraphQLResponse) {
        when (graphQLResponse.status) {
            Status.SUCCESS -> {
                val result = (graphQLResponse.data as GraphCallResult.Success<Storefront.QueryRoot>).response
                if (result.hasErrors) {
                    val errors = result.errors
                    val iterator = errors.iterator()
                    val errormessage = StringBuilder()
                    var error: Error? = null
                    while (iterator.hasNext()) {
                        error = iterator.next()
                        errormessage.append(error.message())
                    }
                    this.errormessage.setValue(errormessage.toString())
                } else {
                    response.setValue(result.data!!.customer)
                }
            }
            Status.ERROR -> errormessage.setValue(graphQLResponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun saveUser(firstName: String, lastName: String) {
        val userLocalData = UserLocalData(firstName, lastName, username, password)
        repository.insertUserData(userLocalData)
    }

    fun recoverCustomer(email: String) {
        try {
            val call = repository.graphClient.mutateGraph(MutationQuery.recoverCustomer(email))
            call.enqueue(Handler(Looper.getMainLooper())) { graphCallResult: GraphCallResult<Storefront.Mutation> -> this.recoverCustomerinvoke(graphCallResult) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun recoverCustomerinvoke(graphCallResult: GraphCallResult<Storefront.Mutation>): Unit {
        try {
            if (graphCallResult is GraphCallResult.Success<*>) {
                consumeResponse(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
            } else {
                consumeResponse(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                    this.errormessage.setValue(errormessage.toString())
                } else {
                    val errors = result.data!!.customerRecover.customerUserErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.CustomerUserError
                            err += error.message
                        }
                        errormessage.setValue(err)
                    } else {
                        errormessage.setValue("Please Check Your Mail")
                    }
                }
            }
            Status.ERROR -> errormessage.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun isValidEmail(target: String): Boolean {
        var valid = false
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (target.matches(emailPattern.toRegex())) {
            valid = true
        }
        return valid
    }
}
