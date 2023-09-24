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
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

interface MarkerClickListener {
    fun onMarkerClick(id: String)
}
class MapsFragment : Fragment(), OnMapReadyCallback, MarkerClickListener {
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mapFragment: SupportMapFragment
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val markerViewModel: MarkerViewModel by activityViewModels()
    private val markerViewViewModel: MarkerViewViewModel by activityViewModels()
    private val db =
        FirebaseDatabase.getInstance("https://cleantown-4d5da-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onMarkerClick(id: String) {
        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
        navController.navigate(R.id.action_mapsFragment_to_markerView)
    }
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
        googleMap.setOnMarkerClickListener {
            val markerId = it.tag as String
            markerViewViewModel.setMarkerId(markerId)
            onMarkerClick(markerId)
            true
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }
        //TODO Dodaj MarkerViewFragment
        //Filtriranje
        db.reference.child("markers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var markers = mutableListOf<Marker>()
                for (markerSnap in snapshot.children) {
                    val marker = markerSnap.getValue(Marker::class.java)!!
                    markers.add(marker)
                    val markerOptions = MarkerOptions().position(LatLng(marker.latitude, marker.longitude)).title(marker.title)
                    val added = googleMap.addMarker(markerOptions)
                    added!!.tag = marker.id
                }
                markerViewModel.setMarkers(markers)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}