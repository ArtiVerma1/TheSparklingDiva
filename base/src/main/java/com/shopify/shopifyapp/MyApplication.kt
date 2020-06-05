package com.shopify.shopifyapp
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.shopify.shopifyapp.dependecyinjection.MageNativeAppComponent
import com.shopify.shopifyapp.dependecyinjection.DaggerMageNativeAppComponent
import com.shopify.shopifyapp.dependecyinjection.UtilsModule
import com.shopify.shopifyapp.utils.Urls
import net.danlew.android.joda.JodaTimeAndroid
class MyApplication : MultiDexApplication() {
    var mageNativeAppComponent: MageNativeAppComponent? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
        context = this
        mageNativeAppComponent = DaggerMageNativeAppComponent.builder().utilsModule(UtilsModule(this)).build()
        FirebaseApp.initializeApp(this)
        val options = FirebaseOptions.Builder()
                .setProjectId("shopify-dev-project-2f51e")
                .setApplicationId("1:445702503308:android:f8dee0b320adfdaa68b4a9") // Required for Analytics.
                .setApiKey("AIzaSyD0GhHgrwqVQC7m3LBOkoxVzVefP6EQAZw") // Required for Auth.
                .setDatabaseUrl("https://shopify-dev-project-2f51e.firebaseio.com/") // Required for RTDB.
                .build()

        val firebaseapp = FirebaseApp.initializeApp(this /* Context */, options, "MageNative")
        auth = Firebase.auth(firebaseapp!!)
        auth.signInWithEmailAndPassword("sudhanshshah@magenative.com", "asdcxzasd")
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        /*Toast.makeText(baseContext, "Authentication success",
                                Toast.LENGTH_SHORT).show()*/
                    } else {

                        /*Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()*/
                    }

                }
    }
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
    }

    companion object {
        lateinit var context: MyApplication
        private var mFirebaseInstance: FirebaseDatabase? = null
        private var mFirebaseSecondanyInstance: FirebaseDatabase? = null
        private var database: DatabaseReference? = null

        fun getmFirebaseInstance(): FirebaseDatabase {
            if (mFirebaseInstance == null) {
                mFirebaseInstance = FirebaseDatabase.getInstance()
            }
            return mFirebaseInstance as FirebaseDatabase
        }

        fun getmFirebaseSecondanyInstance(): FirebaseDatabase {
            if (mFirebaseSecondanyInstance == null) {
                val secondary = FirebaseApp.getInstance("MageNative")
                mFirebaseSecondanyInstance = FirebaseDatabase.getInstance(secondary)
            }
            return mFirebaseSecondanyInstance as FirebaseDatabase
        }

        val dataBaseReference: DatabaseReference
            get() {
                return getmFirebaseSecondanyInstance().getReference(Urls(context).shopdomain.replace(".myshopify.com",""))
            }
    }
}
