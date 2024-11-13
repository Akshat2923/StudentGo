package com.example.studentgo.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.rotationMatrix
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.studentgo.R
import com.example.studentgo.StudentGoApp
import com.example.studentgo.databinding.FragmentMapBinding
import com.example.studentgo.model.KnownLocationRepository
import com.example.studentgo.model.UserRepository
import com.example.studentgo.model.firestore.FirebaseKnownLocationDaoImplementation
import com.example.studentgo.model.firestore.FirebaseUserDao
import com.example.studentgo.model.firestore.FirebaseUserDaoImplementation
import com.example.studentgo.model.room.RoomKnownLocation
import com.example.studentgo.model.room.RoomUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var visitButton: Button

    private val transparentRed = Color.argb(120, 255, 139, 139)

    private val mapViewModel: MapViewModel by activityViewModels()
    private lateinit var knownLocations: List<RoomKnownLocation>

    private lateinit var userModel: RoomUser
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the button by ID
        visitButton = view.findViewById(R.id.visitButton)

        // Get the application instance to access the database
        val application = requireActivity().application as StudentGoApp
        val localLocationDao = application.database.roomKnownLocationDao()
        val remoteLocationDao = FirebaseKnownLocationDaoImplementation()
        val localUserDao = application.database.roomUserDao()
        val remoteUserDao = FirebaseUserDaoImplementation()

        // Create the repository and pass it to the ViewModel
        val locationRepository = KnownLocationRepository(application, localLocationDao, remoteLocationDao)
        val userRepository = UserRepository(application, localUserDao, remoteUserDao)
        mapViewModel.initialize(locationRepository, userRepository)

        // Action to be taken when the button is clicked
        visitButton.setOnClickListener {
            handleVisitButtonClick()
        }

        mapViewModel.user.observe(viewLifecycleOwner) { roomUser ->
            userModel = roomUser
        }

        mapViewModel.email.observe(viewLifecycleOwner) { emailObserved ->
            email = emailObserved
        }
    }

    private fun handleVisitButtonClick() {
        userModel.score += 1
        Log.d("TEST", "Score: ${userModel.score}")
        mapViewModel.updateUser(userModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Event listener for the map reaching a ready state
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val baker = LatLng(40.00163942803014, -83.01591779635797)
        val zoom = 15.0f // 'city-level' zoom

        // Move the camera to the desired location and set the zoom level
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(baker, zoom))

        map.setOnCircleClickListener {
            handleCircleClick(it)
        }

        map.setOnMapClickListener { visitButton.visibility = View.GONE }

        mapViewModel.getKnownLocations()
        mapViewModel.locations.observe(viewLifecycleOwner) { locations ->
            knownLocations = locations
            updateMapKnownLocations(knownLocations, map)
        }

        // Custom style to hide labels
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) {
                Log.e("MapFragment", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapFragment", "Can't find style. Error: ", e)
        }

        // Add settings for getting the user's location
        enableMyLocation()
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        val activity = requireActivity()

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            return
        }

        // 2. Otherwise, request permission
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    private fun handleCircleClick(circle: Circle) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val circleLocation = Location("").apply {
                    latitude = circle.center.latitude
                    longitude = circle.center.longitude
                }

                val distance = location.distanceTo(circleLocation)

                if (distance < 50.0) {
                    visitButton.visibility = View.VISIBLE
                } else {
                    visitButton.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun updateMapKnownLocations(locations: List<RoomKnownLocation>, map: GoogleMap) {
        for (location in locations) {
            val position = LatLng(location.latitude, location.longitude)
            val name = location.name

            map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(name)
            )

            map.addCircle(
                CircleOptions()
                    .center(position)
                    .radius(50.0) // Meters
                    .strokeWidth(0f)
                    .fillColor(transparentRed)
                    .clickable(true)
            )
        }
    }
}