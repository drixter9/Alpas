package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserAlpas(
    var uid: String = "",
    val firstName : String = "",
    val lastName : String = "",
    val email : String = "",
    val birthday : String = "",
    val image : String= "",
    val mobile : Long = 0,
    val gender: String = "",
    val profileCompleted : Int = 0
):Parcelable