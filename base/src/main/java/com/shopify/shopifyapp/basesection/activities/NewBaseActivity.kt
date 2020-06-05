package com.shopify.shopifyapp.basesection.activities
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.basesection.ItemDecoration.GridSpacingItemDecoration
import com.shopify.shopifyapp.basesection.adapters.RecylerAdapter
import com.shopify.shopifyapp.basesection.fragments.BaseFragment
import com.shopify.shopifyapp.basesection.fragments.LeftMenu
import com.shopify.shopifyapp.basesection.viewmodels.LeftMenuViewModel
import com.shopify.shopifyapp.customviews.MageNativeTextView
import com.shopify.shopifyapp.searchsection.activities.AutoSearch
import javax.inject.Inject
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.R2
import com.shopify.shopifyapp.cartsection.activities.CartList
import com.shopify.shopifyapp.utils.*
import com.shopify.shopifyapp.wishlistsection.activities.WishList
import info.androidhive.fontawesome.FontTextView
import okio.HashingSink.md5
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException
import java.util.*

open class NewBaseActivity : AppCompatActivity(), BaseFragment.OnFragmentInteractionListener {

    @BindView(R2.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R2.id.fab)
    lateinit var fab: FloatingActionButton
    @BindView(R2.id.toolimage)
    lateinit var toolimage: ImageView
    @BindView(R2.id.tooltext)
    lateinit var tooltext: MageNativeTextView
    @BindView(R2.id.searchsection)
    lateinit var searchsection: RelativeLayout
    @BindView(R2.id.drawer_layout)
    lateinit var drawer_layout: DrawerLayout
    @BindView(R2.id.search)
    lateinit var search: MageNativeTextView
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    var model: LeftMenuViewModel? = null
     var wishtextView:TextView?=null
    var textView:TextView?=null
    private set
    @Inject
    lateinit var adapter: RecylerAdapter
    private var listDialog: Dialog? = null
     val cartCount: Int
        get() {
            Log.i("MageNative", "Cart Count : " + model!!.cartCount)
            return model!!.cartCount
        }
   lateinit var item:MenuItem
   lateinit var wishitem:MenuItem
   lateinit var cartitem:MenuItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m_newbaseactivity)
        ButterKnife.bind(this)
        (application as MyApplication).mageNativeAppComponent!!.doBaseActivityInjection(this)
        model = ViewModelProvider(this, viewModelFactory).get(LeftMenuViewModel::class.java)
        model!!.Response().observe(this, Observer<ApiResponse> { this.consumeResponse(it) })
        setSupportActionBar(toolbar)
        setToggle()
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayShowTitleEnabled(false)
        tooltext!!.textSize = 14f
        showHumburger()
        fab.setOnClickListener {
            var builder =  CustomTabsIntent.Builder()
            var customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse("https://cedcommerce.com/magenativeapp.html?name="+resources.getString(R.string.app_name)));
        }
    }
    init {
        updateConfig(this)
    }

    fun updateConfig(wrapper: ContextThemeWrapper) {
        var dLocale =Locale(Constant.locale)
        Locale.setDefault(dLocale)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }
    private fun setToggle() {
        mDrawerToggle = object : ActionBarDrawerToggle(this@NewBaseActivity, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        mDrawerToggle!!.syncState()
    }

    protected fun showBackButton() {
        mDrawerToggle!!.isDrawerIndicatorEnabled = false
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mDrawerToggle!!.toolbarNavigationClickListener = View.OnClickListener { onBackPressed() }
        drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        mDrawerToggle!!.onDrawerStateChanged(DrawerLayout.STATE_IDLE)
        mDrawerToggle!!.isDrawerIndicatorEnabled = false
        mDrawerToggle!!.syncState()
    }

    fun showHumburger() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        mDrawerToggle!!.isDrawerIndicatorEnabled = true
        mDrawerToggle!!.toolbarNavigationClickListener = null
    }

    protected fun showTittle(tittle: String) {
        Objects.requireNonNull<MageNativeTextView>(tooltext).visibility = View.VISIBLE
        Objects.requireNonNull<ImageView>(toolimage).visibility = View.GONE
        tooltext!!.text = tittle
    }

    override fun onFragmentInteraction(view: View) {}
    private fun consumeResponse(reponse: ApiResponse) {
        when (reponse.status) {
            Status.SUCCESS -> LeftMenu.renderSuccessResponse(reponse.data!!)
            Status.ERROR -> {
                reponse.error!!.printStackTrace()
                showToast(resources.getString(R.string.errorString))
            }
            else -> {
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun getCurrency() {
        model!!.currencyResponse().observe(this, Observer<List<Storefront.CurrencyCode>> { this.preparePopUp(it) })
        model!!.message.observe(this, Observer<String> { this.showToast(it) })
    }

    private fun preparePopUp(currencyCodes: List<Storefront.CurrencyCode>) {
        if (listDialog == null) {
            showPopUp(currencyCodes)
        } else {
            if (!listDialog!!.isShowing) {
                showPopUp(currencyCodes)
            }
        }
    }

    private fun showPopUp(enabledPresentmentCurrencies: List<Storefront.CurrencyCode>) {
        try {
            val metrics = resources.displayMetrics
            val width = metrics.widthPixels
            val height = metrics.heightPixels
            listDialog = Dialog(this, R.style.PauseDialog)
            ((Objects.requireNonNull<Window>(listDialog!!.window).getDecorView() as ViewGroup).getChildAt(0) as ViewGroup)
                    .getChildAt(1)
                    .setBackgroundColor(resources.getColor(R.color.white))
            val view = View.inflate(this@NewBaseActivity, R.layout.m_list, null)
            listDialog!!.setTitle(Html.fromHtml("<center><font color='#ffffff'>" + resources.getString(R.string.currency) + "</font></center>"))
            var listview = view.findViewById<RecyclerView>(R.id.list)
            listview = setLayout(listview, "vertical")
            adapter!!.setData(enabledPresentmentCurrencies, this@NewBaseActivity)
            listview.adapter = adapter
            listDialog!!.setContentView(view)
            listDialog!!.window!!.setLayout(5 * width / 7, 2 * height / 5)
            listDialog!!.setCancelable(true)
            listDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setLayout(view: RecyclerView, orientation: String): RecyclerView {
        view.setHasFixedSize(true)
        view.isNestedScrollingEnabled = false
        view.itemAnimator = DefaultItemAnimator()
        val manager = LinearLayoutManager(this)
        when (orientation) {
            "horizontal" -> {
                manager.orientation = RecyclerView.HORIZONTAL
                view.layoutManager = manager
                view.addItemDecoration(GridSpacingItemDecoration(1, dpToPx(1), true))
            }
            "vertical" -> {
                manager.orientation = RecyclerView.VERTICAL
                view.layoutManager = manager
                view.addItemDecoration(GridSpacingItemDecoration(1, dpToPx(2), true))
            }
            "grid" -> {
                view.layoutManager = GridLayoutManager(this, 2)
                view.addItemDecoration(GridSpacingItemDecoration(2, dpToPx(0), true))
            }
            "3grid" -> {
                view.layoutManager = GridLayoutManager(this, 3)
                view.addItemDecoration(GridSpacingItemDecoration(3, dpToPx(4), true))
            }
            "customisablegrid"->{
                view.layoutManager = GridLayoutManager(this, 3)
                view.addItemDecoration(GridSpacingItemDecoration(3, dpToPx(4), true))
            }
        }
        return view
    }

    fun closePopUp() {
        listDialog!!.dismiss()
    }

    private fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_search, menu)
        item = menu.findItem(R.id.search_item)
        wishitem = menu.findItem(R.id.wish_item)
        cartitem = menu.findItem(R.id.cart_item)
        item.setActionView(R.layout.m_search)
        wishitem.setActionView(R.layout.m_wishcount)
        cartitem.setActionView(R.layout.m_count)
        val search = item.actionView
        search.setOnClickListener {
            val searchpage = Intent(this@NewBaseActivity, AutoSearch::class.java)
            startActivity(searchpage)
        }
        val notifCount = cartitem.actionView
         textView = notifCount.findViewById<TextView>(R.id.count)
        textView!!.text = "" + cartCount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this, CartList::class.java)
            startActivity(mycartlist)
        }
        val wishcount = wishitem.actionView
        wishtextView = wishcount.findViewById<TextView>(R.id.count)
        wishtextView!!.text = "" + model!!.wishListcount
        wishcount.setOnClickListener {
            val wishlist = Intent(this, WishList::class.java)
            startActivity(wishlist)
        }
        return true
    }
    override fun onResume() {
        super.onResume()
        when(getPackageName()){
            "com.shopify.shopifyapp"->{
               fab.visibility=View.VISIBLE
            }else->{
               fab.visibility=View.GONE
            }
        }
    }
    fun setSearchOption(type:String,placeholder:String){
        when(type){
            "middle-width-search"->{
                item.setVisible(false)
                toolimage.visibility=View.GONE
                searchsection.visibility=View.VISIBLE
                search.text=placeholder
                search.setOnClickListener {
                    val searchpage = Intent(this@NewBaseActivity, AutoSearch::class.java)
                    startActivity(searchpage)
                }
            }
            "full-width-search"->{
                item.setVisible(false)
                toolimage.visibility=View.VISIBLE
                searchsection.visibility=View.GONE
            }
            else->{
                item.setVisible(true)
                toolimage.visibility=View.VISIBLE
                searchsection.visibility=View.GONE
            }
        }
    }
    fun setWishList(visiblity:String) {
        when (visiblity) {
            "1" -> {
                wishitem.setVisible(true)
            }
            else -> {
                wishitem.setVisible(false)
            }
        }
    }
    fun setLogoImage(url:String){
        if(!this.isDestroyed){
            Log.i("MageNative","Image URL"+url)
            Glide.with(this)
                    .load(url)
                    .thumbnail(0.5f)
                    .apply(RequestOptions().placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(toolimage)
        }
    }
    fun setPanelBackgroundColor(color:String){
        toolbar.setBackgroundColor(Color.parseColor(color.toUpperCase()))
    }
    fun setIconColors(countback:String,counttext:String,iconcolor:String){
        val wishview = wishitem.actionView
        val cartview = cartitem.actionView
        val searchview = item.actionView
        val wishrelative = wishview.findViewById<RelativeLayout>(R.id.back)
        val wishtext = wishview.findViewById<TextView>(R.id.count)
        val wishicon = wishview.findViewById<FontTextView>(R.id.cart_icon)
        val cartrelative = cartview.findViewById<RelativeLayout>(R.id.back)
        val carttext = cartview.findViewById<TextView>(R.id.count)
        val carticon = cartview.findViewById<FontTextView>(R.id.cart_icon)
        wishrelative.backgroundTintList=ColorStateList.valueOf(Color.parseColor(countback))
        cartrelative.backgroundTintList=ColorStateList.valueOf(Color.parseColor(countback))
        wishtext.setTextColor(Color.parseColor(counttext))
        carttext.setTextColor(Color.parseColor(counttext))
        wishicon.setTextColor(Color.parseColor(iconcolor))
        carticon.setTextColor(Color.parseColor(iconcolor))
        val searchicon = searchview.findViewById<FontTextView>(R.id.search_icon)
        searchicon.setTextColor(Color.parseColor(iconcolor))
        mDrawerToggle!!.getDrawerArrowDrawable().setColor(Color.parseColor(iconcolor));
    }
    fun setSearchOptions(searchback:String,searchtext:String,searhcborder:String){
        var draw: GradientDrawable = search.background as GradientDrawable
        draw.setColor(Color.parseColor(searchback))
        search.setTextColor(Color.parseColor(searchtext))
        search.setHintTextColor(Color.parseColor(searchtext))
        draw.setStroke(5, Color.parseColor(searhcborder));
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "" + resources.getString(R.string.noresultfound), Toast.LENGTH_LONG).show()
                finish()
            } else {
                when(result.formatName){
                    "QR_CODE"->{
                        try{
                            AESEnDecryption().data()
                            var json=JSONObject(result.contents)
                            if(json.has("mid")){
                                Log.i("MageNative","Barcode"+result)
                                Log.i("MageNative","Barcode"+result.contents)

                                model!!.insertPreviewData(json)
                                model!!.logOut()
                                var intent =Intent(this,Splash::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }catch (ex:Exception){
                            ex.printStackTrace()
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
