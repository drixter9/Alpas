package com.example.alpas

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasSoldProductListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.SoldProduct
import kotlinx.android.synthetic.main.activity_my_products.*
import kotlinx.android.synthetic.main.activity_my_sold_products.*


class MySoldProductsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sold_products)
        getSoldProductList()
        setupActionbar()
    }

    private fun getSoldProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getSoldProductList(this)
    }
    private fun setupActionbar() {
        setSupportActionBar(toolbar_my_sold_product_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_my_sold_product_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    fun successGetSoldProductsList(soldProductList: ArrayList<SoldProduct>){
        hideProgressDialog()
        if (soldProductList.size > 0){
            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_products_found.visibility = View.GONE

            rv_sold_product_items.layoutManager = LinearLayoutManager(this)
            rv_sold_product_items.setHasFixedSize(true)

            val soldProductAdapter = AlpasSoldProductListAdapter(this,soldProductList)
            rv_sold_product_items.adapter = soldProductAdapter
        }
        else{
            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_products_found.visibility = View.VISIBLE

        }
    }
}