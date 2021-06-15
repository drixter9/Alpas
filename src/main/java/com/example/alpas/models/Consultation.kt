package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Consultation  (
    val user_id: String = "",
    val user_name : String = "",
    val user_image: String = "",
    val consultation_title : String = "",
    val consultation_degree : String = "",
    val consultation_program_of_study : String = "",
    val consultation_price: String = "",
    val consultation_description: String = "",
    val image: String = "",
    var consultation_id: String = ""
): Parcelable