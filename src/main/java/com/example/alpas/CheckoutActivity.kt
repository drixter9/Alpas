package com.example.alpas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasCartItemListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList : ArrayList<Product>
    private lateinit var mCartItemsList : ArrayList<CartItem>
    private var mSubtotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionbar()

        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if(mAddressDetails != null){
            tv_checkout_address_type_checkout_activity.text = mAddressDetails?.address_type
            tv_checkout_full_name_checkout_activity.text = mAddressDetails?.name
            tv_checkout_address_checkout_activity.text = "${mAddressDetails?.address}, ${mAddressDetails?.zipCode}"
            tv_checkout_additional_note_checkout_activity.text = mAddressDetails?.additional_note

            if(mAddressDetails?.other_details!!.isNotEmpty()){
                tv_checkout_other_details_checkout_activity.text = mAddressDetails?.other_details
            }

            tv_checkout_mobile_number_checkout_activity.text = mAddressDetails?.mobile
        }

        getProductList()

        btn_place_order_checkout_activity.setOnClickListener {
            placeOrder()
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_checkout_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_checkout_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this@CheckoutActivity)
    }

    fun successProductListFromFirestore(productList : ArrayList<Product>){
        mProductList = productList
        getCartItemList()
    }

    private fun getCartItemList(){
        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    fun successCartListFromFirestore(cartList : ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList
        rv_cart_list_items_checkout_activity.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items_checkout_activity.setHasFixedSize(true)

        val cartListAdapter = AlpasCartItemListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items_checkout_activity.adapter = cartListAdapter

        for (item in mCartItemsList){
            val availableQuantity = item.stock_quantity.toInt()
            if(availableQuantity > 0){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubtotal += (price * quantity)
            }
        }

        tv_checkout_sub_total_checkout_activity.text = "Php $mSubtotal"
        tv_checkout_shipping_charge_checkout_activity.text = "Php 50"

        if (mSubtotal > 0 ){
            ll_checkout_place_order_checkout_activity.visibility = View.VISIBLE

            mTotalAmount = mSubtotal + 50.0
            tv_checkout_total_amount_checkout_activity.text = "Php $mTotalAmount"
        }
        else{
            ll_checkout_place_order_checkout_activity.visibility = View.GONE
        }

    }

    private fun placeOrder(){
        if (mAddressDetails !=null) {
            mOrderDetails = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My Order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubtotal.toString(),
                "Php 50",
                mTotalAmount.toString(),
                System.currentTimeMillis(),

                )

            FirestoreClass().placeOrder(this@CheckoutActivity,mOrderDetails)
        }

    }

    fun successPlaceOrder() {
        FirestoreClass().updateProductDetailsAfterOrder(this,mCartItemsList,mOrderDetails)


    }

    fun allDetailsUpdateSuccessfully(){

        val username = this.getSharedPreferences(Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME,"")!!
        val userImage = this.getSharedPreferences(Constants.ALPAS_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USER_IMAGE,"")!!

        for (i in mOrderDetails.items) {
            val topic = "/topics/${i.product_owner_id}"
            val message = "$username buy your product."
            val notification = NotificationData(FirestoreClass().getCurrentUserID(),userImage,i.product_id, i.title, message, Constants.NOTIFICATION_PRODUCTS,i.product_owner_id,System.currentTimeMillis())
            PushNotification(
                notification,
                topic
            ).also {
                FirebaseService().sendNotification(it)
            }
            val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            reference.child(Constants.NOTIFICATION).push().setValue(notification)
        }

        hideProgressDialog()

        Toast.makeText(
                this@CheckoutActivity,
                "Your order was placed successfully",
                Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

    }

}