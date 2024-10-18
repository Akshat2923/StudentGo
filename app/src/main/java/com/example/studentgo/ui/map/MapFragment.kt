package com.example.studentgo.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        mapViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        // Adding? Google Maps fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Event listener for the map reaching a ready state
    override fun onMapReady(googleMap: GoogleMap) {
        val baker = LatLng(40.00163942803014, -83.01591779635797)
        val zoom = 15.0f // 'city-level' zoom

        // Move the camera to the desired location and set the zoom level
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baker, zoom))

        // Add a marker to Baker Systems
        googleMap.addMarker(
            MarkerOptions()
                .position(baker)
                .title("Baker Systems")
        )

        // TODO: get coordinates for each location, store them in an iterable structure,
        // and call a function which adds a marker to each set of coordinates

        // Custom style to hide labels
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) {
                Log.e("MapFragment", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapFragment", "Can't find style. Error: ", e)
        }
    }
}