package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SavedThread (
    val user_id: String = "",
    val owner_user_id: String = "",
    val owner_user_name : String = "",
    val owner_user_image: String = "",
    val thread_title : String = "",
    val thread_field_of_study : String = "",
    val thread_topic: String = "",
    val thread_description: String = "",
    val thread_image: String = "",
    val thread_like:Long = 0L,
    val thread_comments:Long = 0L,
    val thread_date:Long = 0L,
    var thread_id: String = "",
    var saved_thread_id: String = "",
): Parcelable
