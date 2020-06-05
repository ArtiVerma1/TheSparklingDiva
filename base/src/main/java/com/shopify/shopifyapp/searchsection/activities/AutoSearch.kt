package com.shopify.shopifyapp.searchsection.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import com.google.zxing.integration.android.IntentIntegrator
import com.mindorks.paracamera.Camera
import com.shopify.buy3.Storefront
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.databinding.MAutosearchBinding
import com.shopify.shopifyapp.basesection.activities.BaseActivity
import com.shopify.shopifyapp.searchsection.adapters.SearchRecylerAdapter
import com.shopify.shopifyapp.searchsection.viewmodels.SearchListModel
import com.shopify.shopifyapp.utils.ViewModelFactory
import javax.inject.Inject

class AutoSearch : BaseActivity() {
    private var binding: MAutosearchBinding? = null
    @Inject
    lateinit var factory: ViewModelFactory
    private var model: SearchListModel? = null
    @Inject
    lateinit var adapter: SearchRecylerAdapter
    private var viewlist: RecyclerView? = null
    private lateinit var camera: Camera
    private val PERMISSION_REQUEST_CODE = 1
    var image: FirebaseVisionImage?=null
    lateinit var  labeler: FirebaseVisionImageLabeler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_autosearch, group, true)
        showBackButton()
        showTittle(resources.getString(R.string.search))
        val secondary=FirebaseApp.getInstance("MageNative")
        val localModel = FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("manifest.json")
                .build()
        val optionsBuilder= FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
        val options = optionsBuilder.setConfidenceThreshold(0.5f).build()
        labeler = FirebaseVision.getInstance(secondary).getOnDeviceAutoMLImageLabeler(options)
        camera = Camera.Builder()
                .resetToCorrectOrientation(true)//1
                .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)//2
                .setDirectory("pics")//3
                .setName("delicious_${System.currentTimeMillis()}")//4
                .setImageFormat(Camera.IMAGE_JPEG)//5
                .setCompression(75)//6
                .build(this)
        viewlist = setLayout(binding!!.searchlist, "vertical")
        (application as MyApplication).mageNativeAppComponent!!.doAutoSearchActivityInjection(this)
        model = ViewModelProviders.of(this, factory).get(SearchListModel::class.java)
        model!!.message.observe(this, Observer<String> { this.showToast(it) })
        model!!.setPresentmentCurrencyForModel()
        model!!.filteredproducts.observe(this, Observer<List<Storefront.ProductEdge>> { this.setRecylerData(it) })
        binding!!.searchtext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                model!!.setSearchData(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
    override fun onCreateOptionsMenu( menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scanner, menu)
        return true
    }
    private fun setRecylerData(products: List<Storefront.ProductEdge>) {
        try {
            if (products.size > 0) {
                if (adapter!!.products != null) {
                    adapter!!.products = products
                    adapter!!.notifyDataSetChanged()
                } else {
                    adapter!!.presentmentcurrency = model!!.presentmentcurrency
                    adapter!!.setData(products, this@AutoSearch)
                    viewlist!!.adapter = adapter
                }
            } else {
                showToast(resources.getString(R.string.noproducts))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.scanner -> {
                val integrator = IntentIntegrator(this)
                integrator.setPrompt("Scan a barcode")
                integrator.setCameraId(0) // Use a specific camera of the device
                integrator.setOrientationLocked(true)
                integrator.setBeepEnabled(true)
                integrator.captureActivity = SearchByScanner::class.java
                integrator.initiateScan()
                true
            }
            R.id.camera->{
                takePicture()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                val bitmap = camera.cameraBitmap
                if (bitmap != null) {
                    checkImage(bitmap)
                } else {
                    Toast.makeText(this.applicationContext, getString(R.string.picture_not_taken), Toast.LENGTH_SHORT).show()
                }
            }
            else{
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    if (result.contents == null) {
                        Toast.makeText(applicationContext, "" + resources.getString(R.string.noresultfound), Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        try {
                            adapter!!.products=null
                            Log.i("MageNative","Barcode"+result.contents)
                            model!!.searchResultforscanner(result.contents)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
    fun takePicture() {
        if (!hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                !hasPermission(android.Manifest.permission.CAMERA)) {
            // If do not have permissions then request it
            requestPermissions()
        } else {
            // else all permissions granted, go ahead and take a picture using camera
            try {
                camera.takePicture()
            } catch (e: Exception) {
                // Show a toast for exception
                Toast.makeText(this.applicationContext, getString(R.string.error_taking_picture),
                        Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun requestPermissions(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
            return
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        camera.takePicture()
                    } catch (e: Exception) {
                        Toast.makeText(this.applicationContext, getString(R.string.error_taking_picture),
                                Toast.LENGTH_SHORT).show()
                    }
                }
                return
            }
        }
    }
    private fun checkImage(bitmap: Bitmap) {
        image = FirebaseVisionImage.fromBitmap(bitmap)
        labeler.processImage(image!!)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        if(label.confidence>0.5){
                            model!!.getProductsByKeywords(text)
                            Log.i("MageNative","Label : "+text)
                            Log.i("MageNative","confidence : $confidence")
                        }else{
                            continue
                        }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
    }
    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this,
                permission) == PackageManager.PERMISSION_GRANTED
    }
}
