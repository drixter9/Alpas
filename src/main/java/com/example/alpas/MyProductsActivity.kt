package com.example.alpas

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasProductListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Product
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_my_products.*


class MyProductsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_products)
        getProductListFromFireStore()
        setupActionbar()

        fb_add_product.setOnClickListener {
            val intent =(Intent(this, AddProductActivity::class.java))
            startActivityForResult(intent,Constants.ADD_PRODUCTS_REQUEST_CODE)
        }
    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_PRODUCTS_REQUEST_CODE) {
                getProductListFromFireStore()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }


    private fun setupActionbar() {
        setSupportActionBar(toolbar_my_products_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_my_products_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    fun successProductListFromFireStore(productList: ArrayList<Product>){
        hideProgressDialog()
        if (rv_product_items != null) {
            if (productList.size > 0) {
                rv_product_items.visibility = View.VISIBLE
                tv_no_products_found.visibility = View.GONE

                rv_product_items.layoutManager = LinearLayoutManager(this)

                rv_product_items.setHasFixedSize(true)

                val adapterProduct = AlpasProductListAdapter(this, productList, this)

                rv_product_items.adapter = adapterProduct
            } else {
                rv_product_items.visibility = View.GONE
                tv_no_products_found.visibility = View.VISIBLE
            }
        }
    }

    private fun getProductListFromFireStore(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductList(this)
    }

    fun deleteProduct (productID: String){
        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.product_delete_successful_msg),
            Toast.LENGTH_SHORT
        ).show()
        getProductListFromFireStore()
    }
    private fun showAlertDialogToDeleteProduct(productID: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(R.drawable.ic_baseline_clear)
        builder.setPositiveButton(resources.getString(R.string.delete_msg_yes)){ dialogInterface,_->
            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().deleteProduct(this,productID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.delete_msg_no)) { dialogInterface,_->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }
}