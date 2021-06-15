package com.example.alpas.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PushNotification (
    var data: NotificationData,
    var to: String
):Parcelable {}