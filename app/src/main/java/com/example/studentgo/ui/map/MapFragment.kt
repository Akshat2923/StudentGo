package com.example.studentgo.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.studentgo.R
import com.example.studentgo.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Adding? Google Maps fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        return root
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

        // Add a marker to Baker Systems
        map.addMarker(
            MarkerOptions()
                .position(baker)
                .title("Baker Systems")
        )

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
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
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

// The below code is found in the Google example code
//    // [START maps_check_location_permission_result]
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            super.onRequestPermissionsResult(
//                requestCode,
//                permissions,
//                grantResults
//            )
//            return
//        }
//
//        if (isPermissionGranted(
//                permissions,
//                grantResults,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) || isPermissionGranted(
//                permissions,
//                grantResults,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        ) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation()
//        } else {
//            // Permission was denied. Display an error message
//            // [START_EXCLUDE]
//            // Display the missing permission error dialog when the fragments resume.
//            permissionDenied = true
//            // [END_EXCLUDE]
//        }
//    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}