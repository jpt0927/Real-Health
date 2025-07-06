package com.example.realhealth.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Gym(
    val name: String,
    val lat: Double,
    val lng: Double,
    val placeId: String,
    val address: String,
    val phoneNumber: String,
    val openingHours: String,
    val rating: Double?,
    val photoReference: String? = null,
    var distance: String?

) : Parcelable