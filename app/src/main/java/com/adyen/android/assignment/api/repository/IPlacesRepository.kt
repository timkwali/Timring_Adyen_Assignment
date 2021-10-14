package com.adyen.android.assignment.api.repository

import com.adyen.android.assignment.api.model.ResponseWrapper
import com.adyen.android.assignment.api.model.VenueRecommendationsResponse
import retrofit2.Call

interface IPlacesRepository {
    fun getVenueRecommendations(query: Map<String, String>): Call<ResponseWrapper<VenueRecommendationsResponse>>
}