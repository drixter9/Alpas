package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Like (
    var thread_id: String = "",
    var user_id: String = "",
    var like_id:String = "",
): Parcelable