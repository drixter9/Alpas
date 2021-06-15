package com.example.alpas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.CartItem
import com.example.alpas.models.Product
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.item_cart_layout.view.*

class ProductDetailsActivity : BaseActivity() {

    private var mProductID : String = ""
    private lateinit var mProductDetails : Product
    private var mProductOwnerId : String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionbar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            mProductOwnerId  = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == mProductOwnerId){
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        }else{
            btn_add_to_cart.visibility = View.VISIBLE

        }
        getProductDetails()

        btn_add_to_cart.setOnClickListener {
            addToCart()
        }

        btn_go_to_cart.setOnClickListener {
            startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
        }

        iv_message_seller.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS_CHAT,mProductDetails.user_id)
            startActivity(intent)
        }

    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_product_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_product_details_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getProductDetails (){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this,mProductID)
    }


    fun productDetailsSuccess(product: Product){
        mProductDetails = product
        GlideLoader(this@ProductDetailsActivity).loadProductImage(
            product.image,
            iv_product_details_image
        )
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "Php ${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity
        GlideLoader(this).loadUserImage(
            product.user_image,
            cv_product_details_seller_photo
        )

        tv_product_details_seller_name.text = product.user_name

        if(product.stock_quantity.toInt() == 0){
            hideProgressDialog()

            btn_add_to_cart.visibility = View.GONE
            tv_product_details_stock_quantity.text = resources.getString(R.string.lbl_out_of_stock)

            tv_product_details_stock_quantity.setTextColor(
                    ContextCompat.getColor(
                            this@ProductDetailsActivity,
                            R.color.black
                    )
            )
        }
        else{
            if(FirestoreClass().getCurrentUserID() == product.user_id){
                hideProgressDialog()
            }
            else{
                FirestoreClass().checkIfItemExistInCart(this, mProductID)
            }
        }
    }

    private fun addToCart(){
        val yourCartItems = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductID,
            mProductOwnerId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
                mProductDetails.stock_quantity,
            Constants.DEFAULT_CART_QUANTITY,
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addcartitems(this, yourCartItems)
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.msg_add_to_cart_success),
            Toast.LENGTH_SHORT
        ).show()

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun productExistInCart(){
        hideProgressDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }
}