package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Thread (
    val user_id: String = "",
    val user_name : String = "",
    val user_image: String = "",
    val thread_title : String = "",
    val thread_field_of_study : String = "",
    val thread_topic: String = "",
    val thread_description: String = "",
    val thread_image: String = "",
    val thread_like:Long = 0L,
    val thread_comments:Long = 0L,
    val thread_date:Long = 0L,
    var thread_id: String = "",
): Parcelable