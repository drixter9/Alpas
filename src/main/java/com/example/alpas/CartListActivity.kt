package com.example.alpas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasCartItemListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.CartItem
import com.example.alpas.models.Product
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_product_details.*

class CartListActivity : BaseActivity() {

    private lateinit var mProductList : ArrayList<Product>
    private lateinit var mCartListItems : ArrayList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionbar()

        btn_product_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, true)
            startActivity(intent)
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_cart_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_cart_list_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }
    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this@CartListActivity)
    }
    fun successProductListFromFireStore( productList: ArrayList<Product>){
        hideProgressDialog()
        mProductList = productList
        getCartItemList()
    }
    private fun getCartItemList(){
        FirestoreClass().getCartList(this@CartListActivity)
    }

    fun successCartItemList ( cartList: ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductList){
            for(cartItem in cartList){
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0) {
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }
        mCartListItems = cartList

        if(mCartListItems.size > 0){
            rv_my_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_items_found.visibility = View.GONE

            rv_my_cart_items_list.layoutManager = LinearLayoutManager (this@CartListActivity)
            rv_my_cart_items_list.setHasFixedSize(true)

            val  cartItemAdapter = AlpasCartItemListAdapter(this@CartListActivity,mCartListItems, true)

            rv_my_cart_items_list.adapter = cartItemAdapter
            var subTotal : Double = 0.0

            for (item in mCartListItems){
                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price*quantity)
                }
            }
            tv_sub_total.text = "Php $subTotal"
            tv_shipping_charge.text = "Php 50"

            if(subTotal > 0){
                ll_checkout.visibility = View.VISIBLE
                val total = subTotal + 50
                tv_total_amount.text = "Php $total"
            }
            else{
                ll_checkout.visibility = View.GONE
            }
        }
        else{
            rv_my_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_items_found.visibility = View.VISIBLE
        }

    }

    fun itemCartRemoveSuccess (){
        hideProgressDialog()
        Toast.makeText(
                this@CartListActivity,
                resources.getString(R.string.msg_cart_item_remove_success),
                Toast.LENGTH_SHORT
        ).show()

        getCartItemList()
    }

    fun updateCartSuccess(){
        hideProgressDialog()
        getCartItemList()
    }
}