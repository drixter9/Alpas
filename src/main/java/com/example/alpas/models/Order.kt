package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order (
    val user_id: String = "",
    val items: ArrayList<CartItem> = ArrayList(),
    var address: Address = Address(),
    val title: String = "",
    val image: String = "",
    val sub_total_amount: String = "",
    var shipping_charge: String = "",
    var total_amount : String = "",
    val order_dateTime:Long = 0L,
    var id: String = "",
):Parcelable