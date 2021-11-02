package com.shopify.shopifyapp.loginsection.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MLoginPageBinding
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity
import com.shopify.shopifyapp.cartsection.activities.CartList
import com.shopify.shopifyapp.databinding.MForgotbottomsheetBinding
import com.shopify.shopifyapp.homesection.activities.HomePage
import com.shopify.shopifyapp.loginsection.viewmodels.LoginViewModel
import com.shopify.shopifyapp.sharedprefsection.MagePrefs
import com.shopify.shopifyapp.utils.Constant
import com.shopify.shopifyapp.utils.ViewModelFactory
import kotlinx.android.synthetic.main.m_newbaseactivity.*
import java.nio.charset.StandardCharsets

import javax.inject.Inject

class LoginActivity : NewBaseActivity() {
    private var binding: MLoginPageBinding? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: LoginViewModel? = null
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_login_page, group, true)
        nav_view.visibility = View.GONE
        showBackButton()
        showTittle(resources.getString(R.string.login))
        (application as MyApplication).mageNativeAppComponent!!.doLoginActivtyInjection(this)
        model = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        model!!.context = this
        firebaseAnalytics = Firebase.analytics
        model!!.Response().observe(this, Observer<Storefront.CustomerAccessToken> { this.consumeResponse(it) })
        model!!.getResponsedata_().observe(this, Observer<Storefront.Customer> { this.MapLoginDetails(it) })
        model!!.errormessage.observe(this, Observer<String> { this.showToast(it) })
        var hand = MyClickHandlers(this)
        try {
            MyApplication.dataBaseReference?.child("additional_info")?.child("login")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.getValue(String::class.java)!!
                    hand.image = value
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i("DBConnectionError", "" + databaseError.details)
                    Log.i("DBConnectionError", "" + databaseError.message)
                    Log.i("DBConnectionError", "" + databaseError.code)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding!!.handlers = hand
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    private fun showToast(toast: String) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show()
    }

    private fun consumeResponse(result: Storefront.CustomerAccessToken) {
        model!!.savetoken(result)
    }

    private fun MapLoginDetails(customer: Storefront.Customer) {
        model!!.saveUser(customer.firstName, customer.lastName)
        MagePrefs.setCustomerEmail(customer.email)
        MagePrefs.setCustomerId(getBase64Decode(customer.id.toString())!!)
        if (intent.getStringExtra("checkout_id") != null) {
            val intent = Intent(this@LoginActivity, CartList::class.java)
            intent.putExtra("checkout_id", getIntent().getStringExtra("checkout_id"))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            Constant.activityTransition(this)
        } else {
            val intent = Intent(this@LoginActivity, HomePage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Constant.activityTransition(this)
        }
        finish()
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
    inner class MyClickHandlers(private val context: Context) : BaseObservable() {
        @get:Bindable
        var image: String? = null
            set(image) {
                field = image
                notifyPropertyChanged(BR.image)
            }

        fun onSignUpClicked(view: View) {
            if (binding!!.includedlogin.username.text!!.toString().isEmpty()) {
                binding!!.includedlogin.username.error = resources.getString(R.string.empty)
                binding!!.includedlogin.username.requestFocus()
            } else {
                if (!model!!.isValidEmail(binding!!.includedlogin.username.text!!.toString())) {
                    binding!!.includedlogin.username.error = resources.getString(R.string.invalidemail)
                    binding!!.includedlogin.username.requestFocus()
                } else {
                    if (binding!!.includedlogin.password.text!!.toString().isEmpty()) {
                        binding!!.includedlogin.password.error = resources.getString(R.string.empty)
                        binding!!.includedlogin.password.requestFocus()
                    } else {
                        model!!.getUser(binding!!.includedlogin.username.text!!.toString(), binding!!.includedlogin.password.text!!.toString())
                        val params = Bundle()
                        params.putString("user_email", binding!!.includedlogin.username.text!!.toString())
                        firebaseAnalytics.logEvent("android_email_log", params)
                    }
                }
            }
        }

        fun newsignup(view: View) {
            val signup_page = Intent(context, RegistrationActivity::class.java)
            startActivity(signup_page)
            Constant.activityTransition(context)
        }

        fun forgotPass(view: View) {
            var dialog = Dialog(this@LoginActivity, R.style.WideDialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            var mForgotbottomsheetBinding = DataBindingUtil.inflate<MForgotbottomsheetBinding>(layoutInflater, R.layout.m_forgotbottomsheet, null, false)
            dialog.setContentView(mForgotbottomsheetBinding.root)
            mForgotbottomsheetBinding.login.setOnClickListener {
                if (mForgotbottomsheetBinding!!.email.text!!.toString().isEmpty()) {
                    mForgotbottomsheetBinding.email.error = resources.getString(R.string.empty)
                    mForgotbottomsheetBinding.email.requestFocus()
                } else {
                    if (!model!!.isValidEmail(mForgotbottomsheetBinding.email.text!!.toString())) {
                        mForgotbottomsheetBinding.email.error = resources.getString(R.string.invalidemail)
                        mForgotbottomsheetBinding.email.requestFocus()
                    } else {
                        model!!.recoverCustomer(mForgotbottomsheetBinding.email.text!!.toString())
                        mForgotbottomsheetBinding.email.setText(" ")
                        dialog.dismiss()
                    }
                }
            }
            mForgotbottomsheetBinding.closeBut.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}
