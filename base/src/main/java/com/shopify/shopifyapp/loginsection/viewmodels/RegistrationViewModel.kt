package com.shopify.shopifyapp.loginsection.viewmodels

import android.os.Handler
import android.os.Looper

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.GraphResponse
import com.shopify.buy3.MutationGraphCall
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.shopifyapp.dbconnection.entities.CustomerTokenData
import com.shopify.shopifyapp.dbconnection.entities.UserLocalData
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.shopifyqueries.MutationQuery
import com.shopify.shopifyapp.utils.GraphQLResponse
import com.shopify.shopifyapp.utils.Status

class RegistrationViewModel(private val repository: Repository) : ViewModel() {
    val message = MutableLiveData<String>()
    private val responsedata = MutableLiveData<Storefront.Customer>()
    private val loginresponsedata = MutableLiveData<Storefront.CustomerAccessToken>()
    private var password = ""
    fun LoginResponse(): MutableLiveData<Storefront.CustomerAccessToken> {
        return loginresponsedata
    }

    fun Response(): MutableLiveData<Storefront.Customer> {
        return responsedata
    }

    fun getRegistrationDetails(firstname: String, lastname: String, email: String, password: String) {
        this.password = password
        registeruseer(firstname, lastname, email, password)
    }

    private fun registeruseer(firstname: String, lastname: String, email: String, password: String) {
        try {
            val call = repository.graphClient.mutateGraph(MutationQuery.createaccount(firstname, lastname, email, password))
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
                    message.setValue(errormessage.toString())
                } else {
                    val errors = result.data!!.customerCreate.customerUserErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.CustomerUserError
                            err += error.message
                        }
                        message.setValue(err)
                    } else {
                        responsedata.setValue(result.data!!.customerCreate.customer)
                    }
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun insertUserData(customer: Storefront.Customer) {
        val userLocalData = UserLocalData(customer.firstName, customer.lastName, customer.email, password)
        repository.insertUserData(userLocalData)
        getLoginData(customer.email, password)
    }

    private fun getLoginData(email: String, password: String) {
        try {
            val call = repository.graphClient.mutateGraph(MutationQuery.getLoginDetails(email, password))
            call.enqueue(Handler(Looper.getMainLooper())) { graphCallResult: GraphCallResult<Storefront.Mutation> -> this.invokes(graphCallResult) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun invokes(graphCallResult: GraphCallResult<Storefront.Mutation>): Unit {
        if (graphCallResult is GraphCallResult.Success<*>) {
            consumeLoginResponse(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
        } else {
            consumeLoginResponse(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
        }
        return Unit
    }

    private fun consumeLoginResponse(graphQLResponse: GraphQLResponse) {
        when (graphQLResponse.status) {
            Status.SUCCESS -> {
                val result = (graphQLResponse.data as GraphCallResult.Success<Storefront.Mutation>).response
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
                    val errors = result.data!!.customerAccessTokenCreate.customerUserErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.UserError
                            err += error.message
                        }
                        message.setValue(err)
                    } else {
                        loginresponsedata.setValue(result.data!!.customerAccessTokenCreate.customerAccessToken)
                    }
                }
            }
            Status.ERROR -> message.setValue(graphQLResponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun savetoken(token: Storefront.CustomerAccessToken) {
        val customerTokenData = CustomerTokenData(token.accessToken, token.expiresAt.toLocalDateTime().toString())
        repository.saveaccesstoken(customerTokenData)
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
