package com.shopify.shopifyapp.personalised.viewmodels
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.shopifyapp.personalised.adapters.PersonalisedAdapter
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.shopifyqueries.Query
import com.shopify.shopifyapp.utils.GraphQLResponse
import com.shopify.shopifyapp.utils.Status
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.nio.charset.Charset

class PersonalisedViewModel(private val repository: Repository) : ViewModel() {

    fun setPersonalisedData(data:JSONArray,adapter:PersonalisedAdapter,presentmentcurrency: String,recyler:RecyclerView){
        try {
            val edges= mutableListOf<Storefront.Product>()
            var runnable = Runnable {
                for(i in 0..data.length()-1){
                    getProductById(data.getJSONObject(i).getString("product_id"), adapter, presentmentcurrency, recyler,edges,data)
                }
            }
            Thread(runnable).start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    fun getProductById(id:String,adapter:PersonalisedAdapter,presentmentcurrency: String,recyler:RecyclerView,edges:MutableList<Storefront.Product>,data:JSONArray) {
        try {
            val call = repository.graphClient.queryGraph(Query.getProductById(getID(id)))
            call.enqueue(Handler(Looper.getMainLooper())) { result ->
                if (result is GraphCallResult.Success<*>) {
                    consumeResponse(GraphQLResponse.success(result as GraphCallResult.Success<*>), adapter, presentmentcurrency, recyler,edges,data)
                } else {
                    consumeResponse(GraphQLResponse.error(result as GraphCallResult.Failure),  adapter, presentmentcurrency, recyler,edges,data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun consumeResponse(reponse: GraphQLResponse,  adapter:PersonalisedAdapter,presentmentcurrency: String,recyler:RecyclerView,edges:MutableList<Storefront.Product>,data:JSONArray) {
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
                    Log.i("MageNatyive", "ERROR" + errormessage.toString())
                    // message.setValue(errormessage.toString())
                } else {
                    val edge = result.data!!.node as Storefront.Product
                    edges.add(edge)
                    if (edges.size == data.length()) {
                        filterProduct(edges, recyler, adapter, presentmentcurrency)
                    }
                }
            }
            Status.ERROR -> {
                Log.i("MageNatyive", "ERROR-1" + reponse.error!!.error.message)
            }
        }
    }
    private fun filterProduct(list: List<Storefront.Product>, productdata: RecyclerView?, adapter:PersonalisedAdapter, currency: String) {
        try {
            repository.getProductListSlider(list)
                    .subscribeOn(Schedulers.io())
                    .filter { x -> x.availableForSale }
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<List<Storefront.Product>> {
                        override fun onSubscribe(d: Disposable) {
                        }
                        override fun onSuccess(list: List<Storefront.Product>) {
                            adapter!!.presentmentcurrency = currency
                            adapter!!.setData(list)
                            productdata!!.adapter = adapter
                        }
                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getID(id:String):String{
        val data = Base64.encode(("gid://shopify/Product/" + id).toByteArray(), Base64.DEFAULT)
         return String(data, Charset.defaultCharset()).trim { it <= ' ' }
    }
}