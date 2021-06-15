package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationData (
    val user_id:String = "",
    val user_image:String = "",
    val topic_id:String = "",
    val title: String = "",
    val message: String = "",
    val type: Int = 0,
    val to: String = "",
    val date:Long= 0L,
):Parcelable {}