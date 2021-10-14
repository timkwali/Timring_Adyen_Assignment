package com.adyen.android.assignment.api.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.ResponseWrapper
import com.adyen.android.assignment.api.model.VenueRecommendationGroup
import com.adyen.android.assignment.api.model.VenueRecommendationsResponse
import com.adyen.android.assignment.api.repository.PlacesRepository
import com.adyen.android.assignment.utils.Utils.showAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlacesViewmodel(private val placesRepository: PlacesRepository) : ViewModel() {
//    private val context = Application().applicationContext
    private val _venueRecommendations: MutableLiveData<List<VenueRecommendationGroup>> = MutableLiveData()
    val venueRecommendations: LiveData<List<VenueRecommendationGroup>> get() = _venueRecommendations
    var errorMessage: String? = null

    fun getVenueRecommendations(latitude: Double, longitude: Double) {
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(latitude, longitude)
            .build()

        placesRepository.getVenueRecommendations(query).enqueue(object :
            Callback<ResponseWrapper<VenueRecommendationsResponse>> {
            override fun onResponse(
                call: Call<ResponseWrapper<VenueRecommendationsResponse>>,
                response: Response<ResponseWrapper<VenueRecommendationsResponse>>
            ) {
                _venueRecommendations.value = response.body()?.response?.groups!!
                errorMessage = null
            }

            override fun onFailure(
                call: Call<ResponseWrapper<VenueRecommendationsResponse>>,
                t: Throwable
            ) {
                val errorBody = call.execute().errorBody()
                val errorCode = call.execute().body()?.meta?.code!!
                errorMessage = t.message
                Log.d("errorOccurred", "${t.message}\n\n${errorBody}")
            }
        })
    }
}