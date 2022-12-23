package com.goazzi.skycore.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class Business(
    val alias: String,
    val categories: List<Category>,
    val coordinates: Center,

    @SerializedName("display_phone")
    val displayPhone: String,

    val distance: Double,
    val id: String,

    @SerializedName("image_url")
    val imageURL: String,

    @SerializedName("is_closed")
    val isClosed: Boolean,

    val location: Location,
    val name: String,
    val phone: String,
    val price: String,
    val rating: Long,

    @SerializedName("review_count")
    val reviewCount: Long,

    val transactions: List<String>,
    val url: String
)

data class Category(
    val alias: String,
    val title: String
)

data class Center(
    val latitude: Double,
    val longitude: Double
)

data class Location(
    val address1: String,
    val address2: String,
    val address3: String,
    val city: String,
    val country: String,

    @SerializedName("display_address")
    val displayAddress: List<String>,

    val state: String,

    @SerializedName("zip_code")
    val zipCode: String
)

data class Region(
    val center: Center
)
