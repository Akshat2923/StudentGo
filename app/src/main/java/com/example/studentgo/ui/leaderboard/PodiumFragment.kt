package com.example.studentgo.ui.podium

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentgo.databinding.FragmentPodiumBinding
import com.example.studentgo.model.LeaderboardEntry
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PodiumFragment : Fragment() {
    private var _binding: FragmentPodiumBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaderboardRef: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodiumBinding.inflate(inflater, container, false)

        leaderboardRef = FirebaseFirestore.getInstance().collection("leaderboard")
        fetchTop3Users()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun fetchTop3Users() {
        leaderboardRef
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { documents ->
                val podiumContainer = binding.podiumContainer
                podiumContainer.removeAllViews()

                documents.forEachIndexed { index, document ->
                    val entry = document.toObject(LeaderboardEntry::class.java)
                    val userView = TextView(context).apply {
                        text = "${index + 1}. ${entry.userName}: ${entry.score}"
                        textSize = 20f
                        setPadding(0, 16, 0, 16)
                    }
                    podiumContainer.addView(userView)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PodiumFragment", "Error fetching top 3", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}