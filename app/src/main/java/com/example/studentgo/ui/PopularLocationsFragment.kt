package com.example.studentgo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentgo.databinding.FragmentPopularLocationsBinding
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PopularLocationsFragment : Fragment() {
    private var _binding: FragmentPopularLocationsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        fetchPopularLocations()
    }

    private fun fetchPopularLocations() {
        val db = FirebaseFirestore.getInstance()

        // Fetch top 3 locations
        db.collection("locations")
            .orderBy("visits", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { documents ->
                documents.forEachIndexed { index, document ->
                    val locationName = document.getString("name") ?: ""
                    val visits = document.getLong("visits")?.toInt() ?: 0
                    // Calculate points based on rank
                    val points = when (index) {
                        0 -> 3
                        1 -> 2
                        2 -> 1
                        else -> 0
                    }

                    when (index) {
                        0 -> {
                            binding.firstPlaceLocation.text = locationName
                            binding.firstPlaceVisits.text = "$visits visits"
                            binding.firstPlacePoints.text = "$points pts"
                        }

                        1 -> {
                            binding.secondPlaceLocation.text = locationName
                            binding.secondPlaceVisits.text = "$visits visits"
                            binding.secondPlacePoints.text = "$points pts"
                        }

                        2 -> {
                            binding.thirdPlaceLocation.text = locationName
                            binding.thirdPlaceVisits.text = "$visits visits"
                            binding.thirdPlacePoints.text = "$points pts"
                        }
                    }
                }

                // Calculate total visits
                var totalVisitsCount = 0
                documents.forEach { doc ->
                    totalVisitsCount += doc.getLong("visits")?.toInt() ?: 0
                }
                binding.totalVisits.text = totalVisitsCount.toString()
            }

        // Get total number of locations
        db.collection("locations")
            .get()
            .addOnSuccessListener { documents ->
                binding.totalLocations.text = documents.size().toString()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}