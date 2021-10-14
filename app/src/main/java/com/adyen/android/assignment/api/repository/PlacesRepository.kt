package com.adyen.android.assignment.api.repository

import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.model.ResponseWrapper
import com.adyen.android.assignment.api.model.VenueRecommendationsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlacesRepository: IPlacesRepository {
    override fun getVenueRecommendations(query: Map<String, String>): Call<ResponseWrapper<VenueRecommendationsResponse>> {
        return PlacesService.instance.getVenueRecommendations(query)
    }
}