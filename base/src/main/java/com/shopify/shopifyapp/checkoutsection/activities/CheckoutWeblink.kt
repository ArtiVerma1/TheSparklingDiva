package com.shopify.shopifyapp.checkoutsection.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MWebpageBinding
import com.shopify.shopifyapp.basesection.activities.BaseActivity
import com.shopify.shopifyapp.checkoutsection.viewmodels.CheckoutWebLinkViewModel
import com.shopify.shopifyapp.homesection.activities.HomePage
import com.shopify.shopifyapp.utils.Urls
import com.shopify.shopifyapp.utils.ViewModelFactory

import java.net.URLEncoder

import javax.inject.Inject

class CheckoutWeblink : BaseActivity() {
    private var webView: WebView? = null
    private var binding: MWebpageBinding? = null
    private var currentUrl: String? = null
    private var id: String? = null
    private var postData: String? = null
    @Inject
    lateinit var factory: ViewModelFactory
    private var model: CheckoutWebLinkViewModel? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_webpage, content, true)
        (application as MyApplication).mageNativeAppComponent!!.doCheckoutWeblinkActivityInjection(this)
        model = ViewModelProviders.of(this, factory).get(CheckoutWebLinkViewModel::class.java)
        showTittle(resources.getString(R.string.checkout))
        showBackButton()
        webView = binding!!.webview
        currentUrl = intent.getStringExtra("link")
        id = intent.getStringExtra("id")
        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.loadWithOverviewMode = true
        webView!!.settings.useWideViewPort = true
        setUpWebViewDefaults(webView!!)
        if (model!!.isLoggedIn) {
            try {
                val checkouturl = "https://" + resources.getString(R.string.shopdomain) + "/account/login"
                postData = ("checkout_url=" + URLEncoder.encode(currentUrl!!.replace("https://" + resources.getString(R.string.shopdomain), ""), "UTF-8") +
                        "&form_type=" + URLEncoder.encode("customer_login", "UTF-8")
                        + "&customer[email]=" + URLEncoder.encode(model!!.data!!.email, "UTF-8")
                        + "&customer[password]=" + URLEncoder.encode(model!!.data!!.password, "UTF-8"))
                webView!!.postUrl(checkouturl, postData!!.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            webView!!.loadUrl(currentUrl)
        }
        webView!!.webChromeClient = WebChromeClient()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebViewDefaults(webView: WebView) {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.i("URL", "" + description)
            }

            override fun onLoadResource(view: WebView, url: String) {
                Log.i("URL", "" + url)
                if (url.contains("thank_you")) {
                    model!!.setOrder(Urls((application as MyApplication))!!.mid, id)
                    model!!.deleteCart()

                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.i("pageURL", "" + url)
                val javascript = "javascript: document.getElementsByClassName('section__header')[0].style.display = 'none' "
                val javascript1 = "javascript: document.getElementsByClassName('logged-in-customer-information')[0].style.display = 'none' "
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(javascript) { value -> Log.i("pageVALUE1", "" + value) }
                    webView.evaluateJavascript(javascript1) { value -> Log.i("pageVALUE1", "" + value) }
                } else {
                    webView.loadUrl(javascript)
                    webView.loadUrl(javascript1)
                }
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
                Log.i("URL", "" + error.url)
            }
        }
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
            val intent = Intent(this@CheckoutWeblink, HomePage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
    }
}
