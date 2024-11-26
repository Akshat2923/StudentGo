package com.example.studentgo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentgo.databinding.FragmentPopularLocationsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PopularLocationsFragment : Fragment() {
    private var _binding: FragmentPopularLocationsBinding? = null
    private val binding get() = _binding!!

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
        FirebaseFirestore.getInstance().collection("locations")
            .orderBy("visits", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { documents ->
                documents.forEachIndexed { index, document ->
                    val locationName = document.getString("name") ?: ""
                    val visits = document.getLong("visits")?.toInt() ?: 0

                    when (index) {
                        0 -> {
                            binding.firstPlaceLocation.text = locationName
                            binding.firstPlaceVisits.text = "$visits visits"
                        }
                        1 -> {
                            binding.secondPlaceLocation.text = locationName
                            binding.secondPlaceVisits.text = "$visits visits"
                        }
                        2 -> {
                            binding.thirdPlaceLocation.text = locationName
                            binding.thirdPlaceVisits.text = "$visits visits"
                        }
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}