package com.example.alpas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpas.adapters.AlpasOrderListAdapter
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.Order
import kotlinx.android.synthetic.main.activity_my_order.*


class MyOrderActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order)
        getOrderList()
        setupActionbar()
    }

    fun populateOrderListInUI(orderList : ArrayList<Order>){
        hideProgressDialog()
        orderList.sortByDescending { it.order_dateTime }
        if (orderList.size > 0){
            rv_my_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE

            rv_my_order_items.layoutManager = LinearLayoutManager(this)
            rv_my_order_items.setHasFixedSize(true)

            val orderAdapter = AlpasOrderListAdapter(this,orderList)
            rv_my_order_items.adapter = orderAdapter
        }
        else{
            rv_my_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }
    }
    private fun setupActionbar() {
        setSupportActionBar(toolbar_my_orders_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_my_orders_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getOrderList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getOrderList(this)
    }
}