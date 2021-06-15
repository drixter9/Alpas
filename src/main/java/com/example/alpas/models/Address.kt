package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address  (
    val user_id: String = "",
    val name : String = "",
    val mobile: String = "",

    val address: String = "",
    val zipCode: String = "",
    val additional_note: String = "",

    val address_type: String = "",
    val other_details: String = "",
    var id: String = "",
): Parcelable