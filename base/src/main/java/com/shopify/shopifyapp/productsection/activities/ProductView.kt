package com.shopify.shopifyapp.productsection.activities

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity
import com.shopify.shopifyapp.basesection.models.ListData
import com.shopify.shopifyapp.basesection.viewmodels.SplashViewModel.Companion.featuresModel
import com.shopify.shopifyapp.cartsection.activities.CartList
import com.shopify.shopifyapp.databinding.MProductviewBinding
import com.shopify.shopifyapp.databinding.ReviewFormBinding
import com.shopify.shopifyapp.personalised.adapters.PersonalisedAdapter
import com.shopify.shopifyapp.personalised.viewmodels.PersonalisedViewModel
import com.shopify.shopifyapp.productsection.adapters.ImagSlider
import com.shopify.shopifyapp.productsection.adapters.ReviewListAdapter
import com.shopify.shopifyapp.productsection.adapters.VariantAdapter
import com.shopify.shopifyapp.productsection.models.ReviewModel
import com.shopify.shopifyapp.productsection.viewmodels.ProductViewModel
import com.shopify.shopifyapp.utils.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class ProductView : NewBaseActivity() {
    private var binding: MProductviewBinding? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: ProductViewModel? = null
    private var variantlist: RecyclerView? = null
    private val TAG = "ProductView"
    var productID = "noid"

    @Inject
    lateinit var reviewAdapter: ReviewListAdapter

    @Inject
    lateinit var adapter: VariantAdapter
    private var data: ListData? = null
    private var personamodel: PersonalisedViewModel? = null
    private var inStock: Boolean = true

    @Inject
    lateinit var personalisedadapter: PersonalisedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constant.previous = null
        Constant.current = null
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_productview, group, true)
        binding?.features = featuresModel
        showBackButton()
        showTittle(" ")
        variantlist = setLayout(binding!!.productvariant, "horizontal")
        (application as MyApplication).mageNativeAppComponent!!.doProductViewInjection(this)
        model = ViewModelProvider(this, factory).get(ProductViewModel::class.java)
        model!!.context = this
        model?.createreviewResponse?.observe(this, Observer { this.createReview(it) })
        personamodel = ViewModelProvider(this, factory).get(PersonalisedViewModel::class.java)
        if (intent.getStringExtra("handle") != null) {
            model!!.handle = intent.getStringExtra("handle")
        }
        if (intent.getStringExtra("ID") != null) {
            model!!.id = intent.getStringExtra("ID")
            productID = model!!.id
        }
        if (featuresModel.productReview!!) {
            model?.getReviewBadges(Urls(application as MyApplication).mid, getBase64Decode(productID)!!)?.observe(this, Observer { this.consumeBadges(it) })
            model?.getReviews(Urls(application as MyApplication).mid, getBase64Decode(productID)!!)?.observe(this, Observer { this.consumeReview(it) })
        }

        data = ListData()
        if (model!!.setPresentmentCurrencyForModel()) {
            model!!.filteredlist.observe(this, Observer<List<Storefront.ProductVariantEdge>> { this.filterResponse(it) })
            if (featuresModel.ai_product_reccomendaton) {
                model!!.getApiResponse().observe(this, Observer<ApiResponse> { this.consumeResponse(it) })
            }
            if (intent.getSerializableExtra("product") != null) {
                setProductData(intent.getSerializableExtra("product") as Storefront.Product)
            } else {
                model!!.Response().observe(this, Observer<GraphQLResponse> { this.consumeResponse(it) })
            }
        }
    }

    private fun createReview(response: ApiResponse?) {
        if (response?.data != null) {
            var data = JSONObject(response?.data.toString())
            if (data.getBoolean("success")) {
                Toast.makeText(this, getString(R.string.review_submitted), Toast.LENGTH_SHORT).show()
                GlobalScope.launch(Dispatchers.Main) {
                    delay(2000)
                    model?.getProductReviews(Urls(application as MyApplication).mid, getBase64Decode(productID)!!)
                    model?.getbadgeReviews(Urls(application as MyApplication).mid, getBase64Decode(productID)!!)
                }
            }
        }
    }

    private fun consumeBadges(response: ApiResponse?) {
        if (response?.data != null) {
            var data = JSONObject(response?.data.toString()).getJSONObject("data")
            binding?.ratingTxt?.text = data.getJSONObject(getBase64Decode(productID)).getString("total-rating") + " " + getString(R.string.rating)
        }
    }

    private fun consumeReview(response: ApiResponse?) {
        if (response?.data != null) {
            try {
                Log.d(TAG, "consumeReview: " + JSONObject(response.data.toString()))

                if (JSONObject(response.data.toString()).getJSONObject("data").has("reviews")) {
                    var reviewModel = Gson().fromJson<ReviewModel>(response.data.toString(), ReviewModel::class.java) as ReviewModel
                    if (reviewModel.success!!) {
                        if (reviewModel.data?.reviews?.size!! > 0) {
                            binding?.noReviews?.visibility = View.GONE
                            binding?.reviewList?.visibility = View.VISIBLE
                            reviewAdapter.setData(reviewModel.data?.reviews)
                            binding?.reviewList?.adapter = reviewAdapter
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                binding?.noReviews?.visibility = View.VISIBLE
                binding?.reviewList?.visibility = View.GONE
            }
        }
    }

    fun getBase64Decode(id: String?): String? {
        val data = Base64.decode(id, Base64.DEFAULT)
        var text = String(data, StandardCharsets.UTF_8)
        val datavalue = text.split("/".toRegex()).toTypedArray()
        val valueid = datavalue[datavalue.size - 1]
        val datavalue2 = valueid.split("key".toRegex()).toTypedArray()
        text = datavalue2[0]
        return text
    }

    private fun filterResponse(list: List<Storefront.ProductVariantEdge>) {
        adapter!!.setData(list, model, data, variantCallback_ = object : VariantAdapter.VariantCallback {
            override fun clickVariant(variant: Storefront.ProductVariantEdge) {
                Log.d(TAG, "clickVariant: " + variant)
                data?.regularprice = CurrencyFormatter.setsymbol(variant.node.priceV2.amount, variant.node.priceV2.currencyCode.toString())
                //  data?.specialprice = CurrencyFormatter.setsymbol(variant?.node?.compareAtPriceV2?.amount!!, variant?.node?.compareAtPriceV2?.currencyCode?.toString()!!)
                // data?.offertext = getDiscount(data?.regularprice?.toDouble()!!, data?.specialprice?.toDouble()!!).toString() + "%off"

            }
        })
        variantlist!!.adapter = adapter
        adapter!!.notifyDataSetChanged()
        if (list.size > 1) {
            binding!!.variantheading.visibility = View.VISIBLE
        } else {
            binding!!.variantheading.visibility = View.GONE
        }
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
                    Toast.makeText(this, "" + errormessage, Toast.LENGTH_SHORT).show()
                } else {
                    var productedge: Storefront.Product? = null
                    if (!model!!.handle.isEmpty()) {
                        productedge = result.data!!.productByHandle
                    }
                    if (!model!!.id.isEmpty()) {
                        productedge = result.data!!.node as Storefront.Product
                    }


                    // a.previewImage

                    Log.i("MageNative", "Product_id" + productedge!!.id.toString())
                    setProductData(productedge)
                }
            }
            Status.ERROR -> Toast.makeText(this, reponse.error!!.error.message, Toast.LENGTH_SHORT).show()
            else -> {
            }
        }
    }

    private fun consumeResponse(reponse: ApiResponse) {
        when (reponse.status) {
            Status.SUCCESS -> setPersonalisedData(reponse.data!!)
            Status.ERROR -> {
                reponse.error!!.printStackTrace()
                Toast.makeText(this, resources.getString(R.string.errorString), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setPersonalisedData(data: JsonElement) {
        try {
            val jsondata = JSONObject(data.toString())
            if (jsondata.has("query1")) {
                binding!!.personalisedsection.visibility = View.VISIBLE
                setLayout(binding!!.personalised, "horizontal")
                personamodel!!.setPersonalisedData(jsondata.getJSONObject("query1").getJSONArray("products"), personalisedadapter, model!!.presentmentCurrency!!, binding!!.personalised)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun setProductData(productedge: Storefront.Product?) {
        try {
            loop@ for (i in 0..productedge!!.media.edges.size - 1) {
                var a: String = productedge!!.media.edges.get(i).node.graphQlTypeName
                if (a.equals("Model3d")) {
                    var d = productedge!!.media.edges.get(i).node as Storefront.Model3d
                    for (j in 0..d.sources.size - 1) {
                        if (d.sources.get(j).url.contains(".glb")) {
                            data!!.arimage = d.sources.get(j).url
                            if (featuresModel.ardumented_reality) {
                                binding!!.aricon.visibility = View.VISIBLE
                            } else {
                                binding!!.aricon.visibility = View.GONE
                            }
                            break@loop
                        }
                    }
                }

            }
            if (Constant.ispersonalisedEnable) {
                model!!.getRecommendations(productedge!!.id.toString())
            }

            if (featuresModel.outOfStock!!) {
                if (!productedge.availableForSale) {
                    binding?.outOfStock?.visibility = View.VISIBLE
                    binding?.shareicon?.visibility = View.GONE
                    inStock = false
                } else {
                    inStock = true
                    binding?.outOfStock?.visibility = View.GONE
                    binding?.shareicon?.visibility = View.VISIBLE
                }
            }

            val variant = productedge!!.variants.edges[0].node
            val slider = ImagSlider(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
            slider.setData(productedge.images.edges)
            data!!.product = productedge
            binding!!.images.adapter = slider
            binding!!.indicator.setViewPager(binding!!.images)
            data!!.textdata = productedge.title
            Log.i("here", productedge.descriptionHtml)
            /* data!!.descriptionhmtl = Html.fromHtml(productedge.description)*/
            /*Html.fromHtml(productedge.descriptionHtml)*/
            /*
            * Testing Code for images in HTML
            * */
            /* data!!.descriptionhmtl = Html.fromHtml(productedge.descriptionHtml, object : Html.ImageGetter {
                 override fun getDrawable(source: String): Drawable? {

                     Log.i("here",source)
                     *//*val bmp: Drawable? = Drawable.createFromPath(source)
                    bmp?.setBounds(0, 0, bmp.getIntrinsicWidth(), bmp.getIntrinsicHeight())
                    return bmp*//*
                    val d = LevelListDrawable()
                    val empty = resources.getDrawable(R.mipmap.ic_launcher)
                    d.addLevel(0, 0, empty)
                    d.setBounds(0, 0, empty.intrinsicWidth, empty.intrinsicHeight)

                    LoadImage().execute(source, d)

                    return d
                }
            }, null)*/
            binding?.description?.loadData(productedge.descriptionHtml, "text/html", "utf-8")
            if (model?.isInwishList(model?.id!!)!!) {
                data!!.addtowish = resources.getString(R.string.alreadyinwish)
            } else {
                data!!.addtowish = resources.getString(R.string.addtowish)
            }

            if (model!!.presentmentCurrency == "nopresentmentcurrency") {
                data!!.regularprice = CurrencyFormatter.setsymbol(variant.priceV2.amount, variant.priceV2.currencyCode.toString())
                if (variant.compareAtPriceV2 != null) {
                    val special = java.lang.Double.valueOf(variant.compareAtPriceV2.amount)
                    val regular = java.lang.Double.valueOf(variant.priceV2.amount)
                    if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                        data!!.regularprice = CurrencyFormatter.setsymbol(variant.compareAtPriceV2.amount, variant.compareAtPriceV2.currencyCode.toString())
                        data!!.specialprice = CurrencyFormatter.setsymbol(variant.priceV2.amount, variant.priceV2.currencyCode.toString())
                        data!!.offertext = getDiscount(special, regular).toString() + "%off"

                    } else {
                        data!!.regularprice = CurrencyFormatter.setsymbol(variant.priceV2.amount, variant.priceV2.currencyCode.toString())
                        data!!.specialprice = CurrencyFormatter.setsymbol(variant.compareAtPriceV2.amount, variant.compareAtPriceV2.currencyCode.toString())
                        data!!.offertext = getDiscount(regular, special).toString() + "%off"
                    }
                    data!!.isStrike = true
                    binding!!.regularprice.paintFlags = binding!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    binding!!.specialprice.visibility = View.VISIBLE
                    binding!!.offertext.visibility = View.VISIBLE
                    binding!!.offertext.setTextColor(resources.getColor(R.color.green))
                } else {
                    data!!.isStrike = false
                    binding!!.specialprice.visibility = View.GONE
                    binding!!.offertext.visibility = View.GONE
                    binding!!.regularprice.paintFlags = binding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            } else {
                val edge = variant.presentmentPrices.edges[0]
                data!!.regularprice = CurrencyFormatter.setsymbol(edge!!.node.price.amount, edge.node.price.currencyCode.toString())
                if (variant.compareAtPriceV2 != null) {
                    val special = java.lang.Double.valueOf(edge.node.compareAtPrice.amount)
                    val regular = java.lang.Double.valueOf(edge.node.price.amount)
                    if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                        data!!.regularprice = CurrencyFormatter.setsymbol(edge.node.compareAtPrice.amount, edge.node.compareAtPrice.currencyCode.toString())
                        data!!.specialprice = CurrencyFormatter.setsymbol(edge.node.price.amount, edge.node.price.currencyCode.toString())
                        data!!.offertext = getDiscount(special, regular).toString() + "%off"

                    } else {
                        data!!.regularprice = CurrencyFormatter.setsymbol(edge.node.price.amount, edge.node.price.currencyCode.toString())
                        data!!.specialprice = CurrencyFormatter.setsymbol(edge.node.compareAtPrice.amount, edge.node.compareAtPrice.currencyCode.toString())
                        data!!.offertext = getDiscount(regular, special).toString() + "%off"
                    }
                    data!!.isStrike = true
                    binding!!.regularprice.paintFlags = binding!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    binding!!.specialprice.visibility = View.VISIBLE
                    binding!!.offertext.visibility = View.VISIBLE
                    binding!!.offertext.setTextColor(resources.getColor(R.color.green))
                } else {
                    data!!.isStrike = false
                    binding!!.specialprice.visibility = View.GONE
                    binding!!.offertext.visibility = View.GONE
                    binding!!.regularprice.paintFlags = binding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
            model!!.filterList(productedge.variants.edges)
            binding!!.productdata = data
            binding!!.clickhandlers = ClickHandlers()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getDiscount(regular: Double, special: Double): Int {
        return ((regular - special) / regular * 100).toInt()
    }

    private fun getEdge(edges: List<Storefront.ProductVariantPricePairEdge>): Storefront.ProductVariantPricePairEdge? {
        var pairEdge: Storefront.ProductVariantPricePairEdge? = null
        try {
            for (i in edges.indices) {
                if (edges[i].node.price.currencyCode.toString() == model!!.presentmentCurrency) {
                    pairEdge = edges[i]
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pairEdge
    }

    inner class ClickHandlers {
        fun addtoCart(view: View, data: ListData) {
            if (inStock) {
                if (Constant.current == null) {
                    Toast.makeText(view.context, resources.getString(R.string.selectvariant), Toast.LENGTH_LONG).show()
                } else {
                    model!!.addToCart(Constant.current!!.variant_id!!)
                    Toast.makeText(view.context, resources.getString(R.string.successcart), Toast.LENGTH_LONG).show()
                    invalidateOptionsMenu()
                }
            } else {
                Toast.makeText(view.context, getString(R.string.outofstock_warning), Toast.LENGTH_SHORT).show()
            }

        }

        fun addtoWish(view: View, data: ListData) {
            if (inStock) {
                Log.i("MageNative", "In Wish")
                if (model!!.setWishList(data.product?.id.toString())) {
                    Toast.makeText(view.context, resources.getString(R.string.successwish), Toast.LENGTH_LONG).show()
                    data.addtowish = resources.getString(R.string.alreadyinwish)
                }
            } else {
                Toast.makeText(view.context, getString(R.string.outofstock_warning), Toast.LENGTH_SHORT).show()
            }

        }

        fun shareProduct(view: View, data: ListData) {
            //Toast.makeText(ProductView.this,data.getProduct().getOnlineStoreUrl(),Toast.LENGTH_LONG).show();
            val shareString = resources.getString(R.string.hey) + "  " + data.product!!.title + "  " + resources.getString(R.string.on) + "  " + resources.getString(R.string.app_name) + "\n" + data.product!!.onlineStoreUrl + "?pid=" + data.product!!.id.toString()
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, view.context.resources.getString(R.string.app_name))
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareString)
            view.context.startActivity(Intent.createChooser(shareIntent, view.context.resources.getString(R.string.share)))
            Constant.activityTransition(view.context)
        }

        fun showAR(view: View, data: ListData) {
            var sceneViewerIntent = Intent(Intent.ACTION_VIEW)
            var intentUri: Uri =
                    Uri.parse("https://arvr.google.com/scene-viewer/1.1").buildUpon()
                            .appendQueryParameter("file", data.arimage)
                            .build()
            sceneViewerIntent.setData(intentUri)
            sceneViewerIntent.setPackage("com.google.ar.core")
            startActivity(sceneViewerIntent)
            Constant.activityTransition(view.context)
        }

        fun rateProduct(view: View, data: ListData) {
            var bottomsheet = BottomSheetDialog(this@ProductView, R.style.WideDialog)
            bottomsheet.window?.setBackgroundDrawableResource(android.R.color.transparent)
            bottomsheet.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            var reviewFormBinding = DataBindingUtil.inflate<ReviewFormBinding>(layoutInflater, R.layout.review_form, null, false)
            bottomsheet.setContentView(reviewFormBinding.root)
            bottomsheet.setCancelable(false)
            reviewFormBinding.closeBut.setOnClickListener {
                bottomsheet.dismiss()
            }
            reviewFormBinding.submitReview.setOnClickListener {
                if (TextUtils.isEmpty(reviewFormBinding.nameEdt.text.toString().trim())) {
                    reviewFormBinding.nameEdt.error = getString(R.string.name_validation)
                    reviewFormBinding.nameEdt.requestFocus()
                } else if (TextUtils.isEmpty(reviewFormBinding.titleEdt.text.toString().trim())) {
                    reviewFormBinding.titleEdt.error = getString(R.string.review_title_validation)
                    reviewFormBinding.titleEdt.requestFocus()
                } else if (TextUtils.isEmpty(reviewFormBinding.bodyEdt.text.toString().trim())) {
                    reviewFormBinding.bodyEdt.error = getString(R.string.review_validation)
                    reviewFormBinding.bodyEdt.requestFocus()
                } else if (TextUtils.isEmpty(reviewFormBinding.emailEdt.text.toString().trim())) {
                    reviewFormBinding.emailEdt.error = getString(R.string.email_validation)
                    reviewFormBinding.emailEdt.requestFocus()
                } else {
                    model?.getcreateReview(Urls(application as MyApplication).mid, reviewFormBinding.ratingBar.rating.toString(), getBase64Decode(productID)!!,
                            reviewFormBinding.nameEdt.text.toString().trim(), reviewFormBinding.emailEdt.text.toString().trim(), reviewFormBinding.titleEdt.text.toString().trim()
                            , reviewFormBinding.bodyEdt.text.toString().trim())
                    bottomsheet.dismiss()
                }
            }
            bottomsheet.show()
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
            val mycartlist = Intent(this@ProductView, CartList::class.java)
            startActivity(mycartlist)
            Constant.activityTransition(this)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }
}
