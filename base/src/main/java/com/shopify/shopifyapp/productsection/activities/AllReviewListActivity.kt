package com.shopify.shopifyapp.productsection.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity
import com.shopify.shopifyapp.cartsection.activities.CartList
import com.shopify.shopifyapp.databinding.ActivityAllReviewListBinding
import com.shopify.shopifyapp.productsection.adapters.AllReviewListAdapter
import com.shopify.shopifyapp.productsection.adapters.ReviewListAdapter
import com.shopify.shopifyapp.productsection.models.ReviewModel
import com.shopify.shopifyapp.utils.Constant
import javax.inject.Inject

class AllReviewListActivity : NewBaseActivity() {

    @Inject
    lateinit var reviewAdapter: AllReviewListAdapter

    private var reviewBinding: ActivityAllReviewListBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        reviewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_all_review_list, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doReviewListInjection(this)
        showBackButton()
        if (intent.hasExtra("reviewList")) {
            var reviewModel = intent.getSerializableExtra("reviewList") as ReviewModel
            reviewAdapter.setData(reviewModel.data?.reviews)
            reviewBinding?.reviewList?.adapter = reviewAdapter
            //  reviewBinding?.productName?.text = intent.getStringExtra("product_name")
            showTittle(intent.getStringExtra("product_name"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_product, menu)
        val item = menu.findItem(R.id.cart_item)
        item.setActionView(R.layout.m_count)
        val notifCount = item.actionView
        val textView = notifCount.findViewById<TextView>(R.id.count)
        textView.text = "" + cartCount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this, CartList::class.java)
            startActivity(mycartlist)
            Constant.activityTransition(this)
        }
        return true
    }
}