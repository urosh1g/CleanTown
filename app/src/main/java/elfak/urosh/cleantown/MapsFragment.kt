package elfak.urosh.cleantown

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mapFragment: SupportMapFragment
    private val locationViewModel: LocationViewModel by activityViewModels()

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                locationViewModel.setLocation(LatLng(location.latitude, location.longitude))
                val userLocation = LatLng(location.latitude, location.longitude)
                googleMap.clear()
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val btnUserList = view.findViewById<Button>(R.id.btnUserList)
        val btnProfile = view.findViewById<Button>(R.id.btnProfile)
        val btnAddMarker = view.findViewById<Button>(R.id.btnAddMarker)
        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)

        btnUserList.setOnClickListener {
            navController.navigate(R.id.action_fragmentMaps_to_fragmentUsers)
        }
        btnProfile.setOnClickListener {
            navController.navigate(R.id.action_mapsFragment_to_userProfile)
        }
        btnAddMarker.setOnClickListener {
            navController.navigate(R.id.action_mapsFragment_to_addMarker)
        }

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}