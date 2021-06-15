package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class Comment(
    var thread_id: String = "",
    var sender_id: String = "",
    var sender_name: String = "",
    var sender_image: String = "",
    var comment: String = "",
    val comment_like:Long = 0L,
    var date: Long = 0L,
    var comment_id:String = "",
):Parcelable