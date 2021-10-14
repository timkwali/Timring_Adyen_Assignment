package com.adyen.android.assignment.adapters

data class VenueItem(
    val id: String,
    val name: String,
    val distance: Int,
    val image: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    val summary: String,
    val city: String,
    val country: String,
)
