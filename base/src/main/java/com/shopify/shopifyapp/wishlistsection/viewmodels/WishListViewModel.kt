package com.shopify.shopifyapp.wishlistsection.viewmodels

import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.shopify.shopifyapp.dbconnection.entities.CartItemData
import com.shopify.shopifyapp.dbconnection.entities.ItemData
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.utils.WishListDbResponse

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class WishListViewModel(private val repository: Repository) : ViewModel() {
    private val data = MutableLiveData<WishListDbResponse>()
    private val changes = MutableLiveData<Boolean>()
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
    val wishListCount: Int
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

    fun Response(): MutableLiveData<WishListDbResponse> {
        FetchData()
        return data
    }

    fun updateResponse(): MutableLiveData<Boolean> {
        return changes
    }

    private fun FetchData() {
        try {
            val runnable = Runnable {
                if (repository.wishListData.size > 0) {
                    Log.i("MageNative", "inwish")
                    Log.i("MageNative", "wish count 3 : " + repository.wishListData.size)
                    data.postValue(WishListDbResponse.success(repository.wishListData))
                } else {
                    Log.i("MageNative", "nowish")
                    data.postValue(WishListDbResponse.error("No Data in WishList"))
                }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun deleteData(variant_id: String) {
        try {
            val runnable = Runnable {
                try {
                    val data = repository.getSingleData(variant_id)
                    repository.deleteSingleData(data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

    fun update(value: Boolean) {
        changes.value = value
    }
}
