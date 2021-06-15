package com.example.alpas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.alpas.models.SoldProduct
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_sold_product_details.*
import java.text.SimpleDateFormat
import java.util.*

class SoldProductDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_product_details)

        var soldProductDetails: SoldProduct = SoldProduct()

        if(intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)){
            soldProductDetails= intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }

        setupActionbar()
        setupUI(soldProductDetails)
    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_sold_product_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_sold_product_details_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun setupUI(soldProductDetails: SoldProduct){
        tv_sold_product_details_id.text = soldProductDetails.order_id

        val dateFormat = "dd/MM/YYYY HH:mm"

        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = soldProductDetails.order_date
        tv_sold_product_details_date.text = formatter.format(calendar.time)

        GlideLoader(this@SoldProductDetailsActivity).loadProductImage(soldProductDetails.image,iv_sold_product_item_image)

        tv_sold_product_item_name.text = soldProductDetails.title
        tv_sold_product_item_price.text = "Php ${soldProductDetails.price}"
        tv_sold_product_quantity.text = soldProductDetails.sold_quantity

        tv_sold_details_address_type.text = soldProductDetails.address.address_type
        tv_sold_details_full_name.text = soldProductDetails.address.name
        tv_sold_details_address.text =
            "${soldProductDetails.address.address}, ${soldProductDetails.address.zipCode}"
        tv_sold_details_additional_note.text = soldProductDetails.address.additional_note

        if (soldProductDetails.address.other_details.isNotEmpty()) {
            tv_sold_details_other_details.visibility = View.VISIBLE
            tv_sold_details_other_details.text = soldProductDetails.address.other_details
        } else {
            tv_sold_details_other_details.visibility = View.GONE
        }
        tv_sold_details_mobile_number.text = soldProductDetails.address.mobile

        tv_sold_product_sub_total.text = soldProductDetails.sub_total_amount
        tv_sold_product_shipping_charge.text = soldProductDetails.shipping_charge
        tv_sold_product_total_amount.text = soldProductDetails.total_amount

    }
}