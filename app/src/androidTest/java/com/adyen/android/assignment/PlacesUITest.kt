package com.adyen.android.assignment

import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.adyen.android.assignment.api.repository.PlacesRepository
import com.adyen.android.assignment.api.viewmodel.PlacesViewmodel
import com.adyen.android.assignment.ui.MainActivity
import com.adyen.android.assignment.utils.LocationService
import com.adyen.android.assignment.utils.Utils.isNetworkAvailable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock


@RunWith(AndroidJUnit4ClassRunner::class)
class PlacesUITest {

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_goToHomeFragment_whenActivityIsLaunched() {
        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.home_search_sv)).check(matches(isDisplayed()))
        onView(withId(R.id.home_venue_tv)).check(matches(isDisplayed()))
        onView(withId(R.id.home_venues_rv)).check(matches(isDisplayed()))
    }

    @Test
    fun test_getVenues_fromViewmodel() {
        val context = InstrumentationRegistry.getContext()
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val isNetworkAvailable = isNetworkAvailable(context)

        if(isLocationOn && isNetworkAvailable) {
            Thread.sleep(6000)
            val repo = PlacesRepository()
            val placesViewmodel = PlacesViewmodel(repo)
            if(placesViewmodel.errorMessage != null) {
                placesViewmodel.venueRecommendations.value?.isNotEmpty()?.let { assert(it) }
            }
        }
    }
}