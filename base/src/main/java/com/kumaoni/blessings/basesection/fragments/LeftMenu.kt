package com.kumaoni.blessings.basesection.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonElement
import com.google.zxing.integration.android.IntentIntegrator
import com.kumaoni.blessings.MyApplication
import com.kumaoni.blessings.R
import com.kumaoni.blessings.addresssection.activities.AddressList
import com.kumaoni.blessings.basesection.activities.NewBaseActivity
import com.kumaoni.blessings.basesection.activities.Splash
import com.kumaoni.blessings.basesection.activities.Weblink
import com.kumaoni.blessings.basesection.models.MenuData
import com.kumaoni.blessings.basesection.viewmodels.LeftMenuViewModel
import com.kumaoni.blessings.basesection.viewmodels.SplashViewModel.Companion.featuresModel
import com.kumaoni.blessings.cartsection.activities.CartList
import com.kumaoni.blessings.collectionsection.activities.CollectionList
import com.kumaoni.blessings.databinding.MDynamicmenuBinding
import com.kumaoni.blessings.databinding.MLeftmenufragmentBinding
import com.kumaoni.blessings.livepreviewsection.LivePreview
import com.kumaoni.blessings.loginsection.activity.LoginActivity
import com.kumaoni.blessings.ordersection.activities.OrderList
import com.kumaoni.blessings.productsection.activities.ProductList
import com.kumaoni.blessings.productsection.activities.ProductView
import com.kumaoni.blessings.searchsection.activities.AutoSearch
import com.kumaoni.blessings.userprofilesection.activities.UserProfile
import com.kumaoni.blessings.utils.Constant
import com.kumaoni.blessings.utils.ViewModelFactory
import com.kumaoni.blessings.wishlistsection.activities.WishList
import kotlinx.android.synthetic.main.review_form.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject

class LeftMenu : BaseFragment() {
    private var binding: MLeftmenufragmentBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var currentactivity: Activity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, R.layout.m_leftmenufragment, container, true)
        menulist = binding!!.menulist
        var pInfo: PackageInfo? = null
        try {
            pInfo = activity!!.packageManager.getPackageInfo(activity!!.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val version = pInfo!!.versionName
        val versioncode = pInfo.versionCode
        Log.i("MageNative", "LeftMenuResume 4")
        menuData = MenuData()
        val app_version = "App Version $version($versioncode)"
        menuData!!.appversion = app_version
        menuData!!.copyright = resources.getString(R.string.copy) + " " + resources.getString(R.string.app_name)
        binding!!.features = featuresModel
        binding!!.menudata = menuData
        binding!!.clickdata = ClickHandlers(currentcontext)
        (activity!!.application as MyApplication).mageNativeAppComponent!!.doLeftMeuInjection(this)
        leftmenu = ViewModelProvider(this, viewModelFactory).get(LeftMenuViewModel::class.java)
        leftmenu.data.observe(viewLifecycleOwner, Observer<HashMap<String, String>> { this.consumeResponse(it) })
        return binding!!.root
    }

    private fun consumeResponse(hash: HashMap<String, String>) {
        menuData!!.tag = hash.get("tag")
        Log.i("MageNative", "LeftMenuResume 3" + hash["firstname"]!!)
        menuData!!.username = hash["firstname"] + " " + hash["secondname"]
        if (hash["tag"] == "login") {
            menuData!!.visible = View.VISIBLE
        } else {
            menuData!!.visible = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentcontext = context
        currentactivity = context as NewBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    class ClickHandlers(internal var context: Context?) {
        private var open = false

        fun getMenu(view: View, menudata: MenuData) {
            when (menudata.type) {
                "home" ->{
                    val intent = Intent(context, Splash::class.java)
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                    context!!.startActivity(intent)
                }
                "collection" -> {
                    val intent = Intent(context, ProductList::class.java)
                    if (menudata.id == null) {
                        intent.putExtra("handle", menudata.handle)
                    } else {
                        try {
                            val data = Base64.encode(("gid://shopify/Collection/" + menudata.id!!).toByteArray(), Base64.DEFAULT)
                            val id = String(data, Charset.defaultCharset()).trim { it <= ' ' }
                            intent.putExtra("ID", id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    intent.putExtra("tittle", menudata.title)
                    context!!.startActivity(intent)
                    Constant.activityTransition(context!!)
                }
                "product" -> {
                    val productintent = Intent(context, ProductView::class.java)
                    if (menudata.id == null) {
                        productintent.putExtra("handle", menudata.handle)
                    } else {
                        try {
                            val data = Base64.encode(("gid://shopify/Product/" + menudata.id!!).toByteArray(), Base64.DEFAULT)
                            val id = String(data, Charset.defaultCharset()).trim { it <= ' ' }
                            productintent.putExtra("ID", id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    productintent.putExtra("tittle", menudata.title)
                    context!!.startActivity(productintent)
                    Constant.activityTransition(context!!)
                }
                "product-all" -> {
                    val product_all = Intent(context, ProductList::class.java)
                    product_all.putExtra("tittle", menudata.title)
                    context!!.startActivity(product_all)
                    Constant.activityTransition(context!!)
                }
                "collection-all" -> {
                    val collection_all = Intent(context, CollectionList::class.java)
                    context!!.startActivity(collection_all)
                    Constant.activityTransition(context!!)
                }
                "page" -> {
                    val page = Intent(context, Weblink::class.java)
                    page.putExtra("name", menudata.title)
                    page.putExtra("link", menudata.url)
                    context!!.startActivity(page)
                    Constant.activityTransition(context!!)
                }
                "blog" -> {
                    val blog = Intent(context, Weblink::class.java)
                    blog.putExtra("name", menudata.title)
                    blog.putExtra("link", menudata.url)
                    context!!.startActivity(blog)
                    Constant.activityTransition(context!!)
                }
            }
        }

        fun expandMenu(view: View) {
            val constraintLayout = view.parent as ConstraintLayout
            val linearLayoutCompat = constraintLayout.getChildAt(2) as LinearLayoutCompat
            if (open) {
                linearLayoutCompat.visibility = View.GONE
                open = false
            } else {
                linearLayoutCompat.visibility = View.VISIBLE
                linearLayoutCompat.requestFocus()
                open = true
            }
        }

        fun navigationClicks(view: View) {
            when (view.tag as String) {
                "instagram" ->{
                    val uri: Uri = Uri.parse("http://instagram.com/_u/kumaoniblessings")
                    val likeIng = Intent(Intent.ACTION_VIEW, uri)
                    likeIng.setPackage("com.instagram.android")
                    try {
                        startActivity(context!!,likeIng,null)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(context!!,Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/kumaoniblessings")),null)
                    }

                }
                "livepreview" -> {
                    val integrator = IntentIntegrator((context as NewBaseActivity)!!)
                    integrator.setPrompt("Scan Your Barcode")
                    integrator.setCameraId(0) // Use a specific camera of the device
                    integrator.setOrientationLocked(true)
                    integrator.setBeepEnabled(true)
                    integrator.captureActivity = LivePreview::class.java
                    integrator.initiateScan()
                }
                "currencyswitcher" -> {
                    Log.i("MageNative", "currencyswitcher" + " : IN")
                    (context as NewBaseActivity)!!.getCurrency()

                }
                "collections" -> {
                    val collection_all = Intent(context, CollectionList::class.java)
                    context!!.startActivity(collection_all)
                    Constant.activityTransition(context!!)
                }
                "Sign In" -> {
                    val login = Intent(context, LoginActivity::class.java)
                    context!!.startActivity(login)
                    Constant.activityTransition(context!!)
                }
                "mywishlist" -> {
                    val mywishlist = Intent(context, WishList::class.java)
                    context!!.startActivity(mywishlist)
                    Constant.activityTransition(context!!)
                }
                "mycartlist" -> {
                    val mycartlist = Intent(context, CartList::class.java)
                    context!!.startActivity(mycartlist)
                    Constant.activityTransition(context!!)
                }
                "invitefriends" -> {
                    val appPackageName = view.context.packageName // getPackageName() from Context or Activity object
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, view.context.resources.getString(R.string.app_name))
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=$appPackageName")
                    view.context.startActivity(Intent.createChooser(shareIntent, view.context.resources.getString(R.string.shareproduct)))
                    Constant.activityTransition(view.context)
                }
                "autosearch" -> {
                    val autosearch = Intent(context, AutoSearch::class.java)
                    context!!.startActivity(autosearch)
                    Constant.activityTransition(context!!)
                }
                "logout" -> {
                    Toast.makeText(context, context!!.resources.getString(R.string.successlogout), Toast.LENGTH_LONG).show()
                    leftmenu.logOut()
                }
                "myprofile" -> if (leftmenu.isLoggedIn) {
                    val myprofile = Intent(context, UserProfile::class.java)
                    context!!.startActivity(myprofile)
                    Constant.activityTransition(context!!)
                } else {
                    Toast.makeText(context, context!!.resources.getString(R.string.logginfirst), Toast.LENGTH_LONG).show()
                }
                "myorders" -> if (leftmenu.isLoggedIn) {
                    val myprofile = Intent(context, OrderList::class.java)
                    context!!.startActivity(myprofile)
                    Constant.activityTransition(context!!)
                } else {
                    Toast.makeText(context, context!!.resources.getString(R.string.logginfirst), Toast.LENGTH_LONG).show()
                }
                "myaddress" -> if (leftmenu.isLoggedIn) {
                    val myaddress = Intent(context, AddressList::class.java)
                    context!!.startActivity(myaddress)
                    Constant.activityTransition(context!!)
                } else {
                    Toast.makeText(context, context!!.resources.getString(R.string.logginfirst), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("MageNative", "LeftMenuResume")
        when (activity!!.getPackageName()) {
            "com.kumaoni.blessings" -> {
                menuData!!.previewvislible = View.VISIBLE
            }
            else -> {
                menuData!!.previewvislible = View.GONE
            }
        }
        leftmenu.context=currentcontext
        leftmenu.fetchUserData()
    }

    companion object {
        lateinit var menulist: LinearLayoutCompat
        private var currentcontext: Context? = null
        protected lateinit var leftmenu: LeftMenuViewModel
        private var menuData: MenuData? = null
        fun renderSuccessResponse(data: JsonElement) {
            Log.i("MageNative:", "MenuData$data")
            if (menulist.childCount == 0) {
                val handler = Handler()
                val runnable = Runnable {
                    try {
                        val `object` = JSONObject(data.toString())
                        if (`object`.getBoolean("success")) {
                            if (`object`.has("data")) {
                                val array = `object`.getJSONArray("data")
                                if (array.length() > 0) {
                                    for (i in 0 until array.length()) {
                                        handler.post {
                                            try {
                                                // Log.i("MageNative","CurrentContext :"+currentcontext)
                                                val binding: MDynamicmenuBinding = DataBindingUtil.inflate(currentcontext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, R.layout.m_dynamicmenu, null, false)
                                                val menuData = MenuData()
                                                if (array.getJSONObject(i).has("id")) {
                                                    menuData.id = array.getJSONObject(i).getString("id")
                                                }
                                                if (array.getJSONObject(i).has("handle")) {
                                                    menuData.handle = array.getJSONObject(i).getString("handle")
                                                }
                                                if (array.getJSONObject(i).has("type")) {
                                                    menuData.type = array.getJSONObject(i).getString("type")
                                                }
                                                menuData.title = array.getJSONObject(i).getString("title")

                                                if (menuData.title.equals("Home")){
                                                    menuData.type = "home"
                                                }

                                                if (array.getJSONObject(i).has("url")) {
                                                    menuData.url = array.getJSONObject(i).getString("url")
                                                }
                                                binding.menudata = menuData
                                                binding.clickdata = ClickHandlers(currentcontext)
                                                if (array.getJSONObject(i).has("menus")) {
                                                    binding.root.findViewById<View>(R.id.expand_collapse).visibility = View.VISIBLE
                                                    updateMenu(array.getJSONObject(i).getJSONArray("menus"), binding.root.findViewById(R.id.submenus))
                                                }
                                                menulist.addView(binding.root)
                                            } catch (e: Exception) {
                                                Log.i("MageNative", "Error" + e.message)
                                                Log.i("MageNative", "Error" + e.cause)
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Thread(runnable).start()
            }
        }

        private fun updateMenu(array: JSONArray, menulist: LinearLayoutCompat) {
            val handler = Handler()
            val runnable = Runnable {
                if (array.length() > 0) {
                    for (i in 0 until array.length()) {
                        handler.post {
                            try {
                                try {
                                    val binding = DataBindingUtil.inflate<MDynamicmenuBinding>(currentcontext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, R.layout.m_dynamicmenu, null, false)
                                    val menuData = MenuData()
                                    if (array.getJSONObject(i).has("id")) {
                                        menuData.id = array.getJSONObject(i).getString("id")
                                    }
                                    if (array.getJSONObject(i).has("handle")) {
                                        menuData.handle = array.getJSONObject(i).getString("handle")
                                    }
                                    menuData.title = array.getJSONObject(i).getString("title")
                                    if (array.getJSONObject(i).has("type")) {
                                        menuData.type = array.getJSONObject(i).getString("type")
                                    }
                                    if (array.getJSONObject(i).has("url")) {
                                        menuData.url = array.getJSONObject(i).getString("url")
                                    }
                                    binding.menudata = menuData
                                    binding.clickdata = ClickHandlers(currentcontext)
                                    if (array.getJSONObject(i).has("menus")) {
                                        binding.root.findViewById<View>(R.id.expand_collapse).visibility = View.VISIBLE
                                        updateMenu(array.getJSONObject(i).getJSONArray("menus"), binding.root.findViewById(R.id.submenus))
                                    }
                                    menulist.addView(binding.root)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            Thread(runnable).start()
        }
    }

}
