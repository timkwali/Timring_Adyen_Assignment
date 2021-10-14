package com.adyen.android.assignment.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.adapters.VenueItem
import com.adyen.android.assignment.adapters.VenuesRvAdapter
import com.adyen.android.assignment.api.repository.PlacesRepository
import com.adyen.android.assignment.api.viewmodel.PlacesViewmodel
import com.adyen.android.assignment.api.viewmodel.PlacesViewmodelFactory
import com.adyen.android.assignment.databinding.FragmentHomeBinding
import com.adyen.android.assignment.utils.ClickListener
import com.adyen.android.assignment.utils.DialogHelper
import com.adyen.android.assignment.utils.LocationService
import com.adyen.android.assignment.utils.Utils.gone
import com.adyen.android.assignment.utils.Utils.isNetworkAvailable
import com.adyen.android.assignment.utils.Utils.showAlertDialog
import com.adyen.android.assignment.utils.Utils.visible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), LocationListener, DialogHelper, ClickListener<VenueItem> {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var placesViewmodel: PlacesViewmodel
    private lateinit var venueRecommendations: MutableList<VenueItem>
    private lateinit var locationService: LocationService
    private lateinit var locationManager: LocationManager
    private lateinit var venuesRvAdapter: VenuesRvAdapter
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationService = LocationService(this@HomeFragment)
            locationService.setLocationPermission({ getLocation() }, { showPermissionMessage() })
            turnOnLocation()
            homeRequestPermissionBtn.setOnClickListener {
                turnOnLocation()
            }
            venuesRefreshTv.setOnClickListener {
                turnOnLocation()
            }
        }
    }

    fun turnOnLocation() {
        if(locationService.isLocationOn(locationManager)) {
            hidePermissionMessage()
            locationService.requestPermission()
        } else {
            showAlertDialog(
                requireActivity(),
                "GPS",
                "You need to turn on Location to continue.",
                this
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            1000,
            100f,
            this
        )
    }

    override fun onLocationChanged(p0: Location) {
        latitude = p0.latitude
        longitude = p0.longitude
        binding.venueProgressPb.visible()
        binding.homeNoVenuesTv.gone()
        locationManager.removeUpdates(this)
        getData()
    }

    private fun getData() {
        if(isNetworkAvailable(requireContext())) {
            hidePermissionMessage()
            val placesViewModelFactory = PlacesViewmodelFactory(PlacesRepository())
            placesViewmodel = ViewModelProvider(this, placesViewModelFactory)
                .get(PlacesViewmodel::class.java)
            placesViewmodel.getVenueRecommendations(latitude, longitude)
            placesViewmodel.venueRecommendations.observe(viewLifecycleOwner, {
                venueRecommendations = mutableListOf()
                if (placesViewmodel.errorMessage == null) {
                    for (item in it[0].items) {
                        val venue = item.venue
                        venueRecommendations.add(VenueItem(venue.id, venue.name, venue.location.distance, "",
                            "${venue.location.address ?: ""} ${venue.location.city ?: ""} ${venue.location.state ?: ""} ${venue.location.country ?: ""}",
                            venue.location.lat, venue.location.lng, "", venue.location.city ?: "", venue.location.country ?: "")
                        )
                    }
                    binding.venueProgressPb.gone()
                    binding.homeNoVenuesTv.gone()
                    binding.homeVenuesRv.visible()
                    setUpRecyclerView()
                } else {
                    binding.apply {
                        venueProgressPb.gone()
                        homeVenuesRv.gone()
                        homeNoVenuesTv.visible()
                    }
                    Toast.makeText(requireContext(), placesViewmodel.errorMessage, Toast.LENGTH_LONG).show()
                }
            })
        } else {
            binding.apply {
                venueProgressPb.gone()
                homeVenuesRv.gone()
                homeNoNetworkTv.visible()
            }
        }
    }

    private fun showPermissionMessage() {
        binding.apply {
            homeNoPermissionTv.visible()
            homeRequestPermissionBtn.visible()
            venueProgressPb.gone()
            homeNoVenuesTv.gone()
            homeVenuesRv.gone()
            homeNoNetworkTv.gone()
        }
    }

    private fun hidePermissionMessage() {
        binding.apply {
            homeNoPermissionTv.gone()
            homeRequestPermissionBtn.gone()
            homeNoNetworkTv.gone()
        }
    }

    private fun showNoData() {
        binding.apply {
            homeNoVenuesTv.visible()
            venueProgressPb.gone()
            homeVenuesRv.gone()
        }
    }

    override fun onOkClick() {
        requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun setUpRecyclerView() {
        venuesRvAdapter = VenuesRvAdapter(venueRecommendations, this)
        binding.apply {
            homeVenuesRv.adapter = venuesRvAdapter
            homeVenuesRv.layoutManager = LinearLayoutManager(requireContext())
        }
        setUpSearchView()
    }

    private fun setUpSearchView() {
        binding.homeSearchSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                venuesRvAdapter.filter.filter(p0)
                return false
            }
        })
    }

    override fun onItemClick(item: VenueItem, position: Int) {
        Toast.makeText(requireContext(), item.name.toUpperCase(), Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}