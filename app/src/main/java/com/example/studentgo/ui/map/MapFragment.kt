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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.studentgo.R
import com.example.studentgo.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var permissionDenied = false
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var visitButton: Button

    private val transparentRed = Color.argb(120, 255, 139, 139)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        // Define OSU campus boundaries
        private val OSU_NORTH_EAST = LatLng(40.004679, -83.005945) // Roughly Lane Ave & High St
        private val OSU_SOUTH_WEST = LatLng(39.996112, -83.024656) // Roughly King Ave & Olentangy

        // Define min and max zoom levels
        private const val MIN_ZOOM = 14f  // Shows most of campus
        private const val MAX_ZOOM = 19f  // Close enough to see buildings clearly
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Event listener for the map reaching a ready state
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Create bounds for OSU campus
        val osuBounds = LatLngBounds(OSU_SOUTH_WEST, OSU_NORTH_EAST)

        // Set camera bounds
        map.setLatLngBoundsForCameraTarget(osuBounds)

        // Set zoom restrictions
        map.setMinZoomPreference(MIN_ZOOM)
        map.setMaxZoomPreference(MAX_ZOOM)

        // Your existing Baker Systems marker code
        val baker = LatLng(40.00163942803014, -83.01591779635797)

        // Move camera to center of campus with appropriate zoom
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(baker, 15.0f))

        // Add a marker to Baker Systems
        map.addMarker(
            MarkerOptions()
                .position(baker)
                .title("Baker Systems")
        )

        // Add a circle around Baker Systems
        map.addCircle(
            CircleOptions()
                .center(baker)
                .radius(50.0) // Meters
                .strokeWidth(0f)
                .fillColor(transparentRed)
                .clickable(true)
        )

        map.setOnCircleClickListener {
            handleCircleClick(it)
        }

        map.setOnMapClickListener { visitButton.visibility = View.GONE }

        // TODO: get coordinates for each location, store them in an iterable structure,
        // and call a function which adds a marker to each set of coordinates

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
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()
        // Add padding to bounds to ensure user can see slightly outside campus
        val padding = 0 // Or add some padding if desired
        map.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                // Keep camera within bounds when user tries to pan
                val cameraPosition = map.cameraPosition
                if (!osuBounds.contains(cameraPosition.target)) {
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(osuBounds, padding))
                }
            }
        }
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

//        This section found in the example code from Google
//        // 2. If if a permission rationale dialog should be shown
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                activity,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) || ActivityCompat.shouldShowRequestPermissionRationale(
//                activity,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        ) {
//            PermissionUtils.RationaleDialog.newInstance(
//                LOCATION_PERMISSION_REQUEST_CODE, true
//            ).show(childFragmentManager, "dialog")
//            return
//        }

        // 3. Otherwise, request permission
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
        val context = requireContext()
//        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
//        Above matches SDK example. The comments below are added in WhereAmIKotlin
//        if (hasLocationPermission()) {
//            findLocation()
//        }

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
                    Log.d("CIRCLE", "Within 50.0 meters.")
                    // Create VISIT button and give it a listener
                    visitButton.visibility = View.VISIBLE
                } else {
                    Log.d("CIRCLE", "Not within 50.0 meters.")
                    visitButton.visibility = View.GONE
                }
            }
        }
    }

//    companion object {
//        /**
//         * Request code for location permission request.
//         *
//         * @see .onRequestPermissionsResult
//         */
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//    }
}