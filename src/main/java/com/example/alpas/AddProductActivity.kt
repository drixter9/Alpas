package com.example.alpas

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Product
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri : Uri? = null
    private var mProductImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setupActionbar()

        iv_add_update_product.setOnClickListener(this)

        btn_add_product_submit.setOnClickListener (this)
    }
    private fun setupActionbar() {
        setSupportActionBar(toolbar_add_product_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_add_product_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    override fun onClick(v: View?) {
        if (v !=null){

            when (v.id){
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )== PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this@AddProductActivity)
                    }
                    else{
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_add_product_submit -> {
                    if(validateAddProductDetails()){
                        uploadProductImage()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this@AddProductActivity)
        }
        else{
            Toast.makeText(
                this,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE)    {
                if (data!=null){
                    try {
                        iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit))
                        try {
                            mSelectedImageFileUri = data.data!!
                            GlideLoader(this).loadUserImage(mSelectedImageFileUri!!,iv_product_image)
                        } catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddProductActivity,
                            resources.getString(R.string.image_selector_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun validateAddProductDetails(): Boolean {
        return when {
            mSelectedImageFileUri==null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(et_add_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }
            TextUtils.isEmpty(et_add_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }
            TextUtils.isEmpty(et_add_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description), true)
                false
            }
            TextUtils.isEmpty(et_add_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity), true)
                false
            }
            else -> {
                true
            }
        }
    }
    private fun uploadProductImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,Constants.PRODUCT_IMAGE)
    }
    fun imageUploadSuccess (imageURL:String){
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME,"")!!
        val userImage = this.getSharedPreferences(Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USER_IMAGE,"")!!

        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            userImage,
            et_add_product_title.text.toString().trim { it <= ' '},
            et_add_product_price.text.toString().trim { it <= ' '},
            et_add_product_description.text.toString().trim { it <= ' '},
            et_add_product_quantity.text.toString().trim { it <= ' '},
            mProductImageURL,
        )
        FirestoreClass().uploadProductDetails(this,product)
    }
    fun  productUploadSuccessful(){
        hideProgressDialog()
        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.msg_product_upload_success),
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }

}