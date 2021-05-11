package com.shopify.shopifyapp.ordersection.activities

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity
import com.shopify.shopifyapp.databinding.ActivityOrderDetailsBinding
import com.shopify.shopifyapp.ordersection.adapters.OrderDetailsListAdapter
import com.shopify.shopifyapp.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class OrderDetails : NewBaseActivity(), OnMapReadyCallback {
    private var binding: ActivityOrderDetailsBinding? = null
    private var orderEdge: Storefront.Order? = null
    private val TAG = "OrderDetails"
    private lateinit var mMap: GoogleMap

    @Inject
    lateinit var orderDetailsListAdapter: OrderDetailsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_order_details, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doOrderDetailsInjection(this)
        showBackButton()
        showTittle("Order Details")
        binding?.itemlist?.isNestedScrollingEnabled = false
        binding?.itemlist?.adapter = orderDetailsListAdapter
        if (intent.hasExtra("orderData")) {
            orderEdge = intent.getSerializableExtra("orderData") as Storefront.Order
            bindData(orderEdge)
        }
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun bindData(orderEdge: Storefront.Order?) {
        binding?.orderId?.text = getString(R.string.order_id) + orderEdge?.orderNumber.toString()
        val sdf2 = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val expiretime = sdf.parse(orderEdge?.processedAt?.toLocalDateTime().toString())
        val time = sdf2.format(expiretime!!)
        binding?.orderdateTxt?.text = time
        var shippingAddress = StringBuffer()
        shippingAddress.append(orderEdge?.shippingAddress?.firstName + " " + orderEdge?.shippingAddress?.lastName)
        shippingAddress.append("\n")
        shippingAddress.append(orderEdge?.shippingAddress?.address1)
        shippingAddress.append("\n")
        shippingAddress.append(orderEdge?.shippingAddress?.city)
        shippingAddress.append("\n")
        shippingAddress.append(orderEdge?.shippingAddress?.country)
        shippingAddress.append("\n")
        shippingAddress.append(orderEdge?.shippingAddress?.zip)
        binding?.shippingaddressTxt?.text = shippingAddress
        orderDetailsListAdapter.setData(orderEdge?.lineItems?.edges)
        binding?.totalamountTxt?.text = CurrencyFormatter.setsymbol(orderEdge?.subtotalPriceV2?.amount
                ?: "", orderEdge?.subtotalPriceV2?.currencyCode.toString())
        binding?.shippingamountTxt?.text = CurrencyFormatter.setsymbol(orderEdge?.totalShippingPriceV2?.amount
                ?: "", orderEdge?.totalShippingPriceV2?.currencyCode.toString())
        binding?.taxamountTxt?.text = CurrencyFormatter.setsymbol(orderEdge?.totalTaxV2?.amount
                ?: "", orderEdge?.totalTaxV2?.currencyCode.toString())
        binding?.netamountTxt?.text = CurrencyFormatter.setsymbol(orderEdge?.totalPriceV2?.amount
                ?: "", orderEdge?.totalPriceV2?.currencyCode.toString())

        Log.d(TAG, "bindData: " + orderEdge?.shippingAddress?.latitude + " " + orderEdge?.shippingAddress?.longitude)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val sydney = LatLng(orderEdge?.shippingAddress?.latitude
                ?: -34.0, orderEdge?.shippingAddress?.longitude ?: 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title(getString(R.string.order_delivery_location)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f))
    }
}