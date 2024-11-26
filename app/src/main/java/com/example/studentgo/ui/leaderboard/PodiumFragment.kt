package com.example.studentgo.ui.podium

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentgo.databinding.FragmentPodiumBinding
import com.example.studentgo.model.LeaderboardEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PodiumFragment : Fragment() {
    private var _binding: FragmentPodiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodiumBinding.inflate(inflater, container, false)
        
        fetchTop3Users()
        fetchLeaderboardStats()
        
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun fetchTop3Users() {
        FirebaseFirestore.getInstance().collection("leaderboard")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { documents ->
                documents.forEachIndexed { index, document ->
                    val entry = document.toObject(LeaderboardEntry::class.java)
                    when (index) {
                        0 -> {
                            binding.firstPlaceUser.text = entry.userName
                            binding.firstPlaceScore.text = "${entry.score} GO Points"
                        }
                        1 -> {
                            binding.secondPlaceUser.text = entry.userName
                            binding.secondPlaceScore.text = "${entry.score} GO Points"
                        }
                        2 -> {
                            binding.thirdPlaceUser.text = entry.userName
                            binding.thirdPlaceScore.text = "${entry.score} GO Points"
                        }
                    }
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
    private fun fetchLeaderboardStats() {
    FirebaseFirestore.getInstance().collection("leaderboard")
        .get()
        .addOnSuccessListener { documents ->
            val totalParticipants = documents.size()
            val highestScore = documents.maxOf { it.toObject(LeaderboardEntry::class.java).score }
            
            binding.totalParticipants.text = totalParticipants.toString()
            binding.highestScore.text = highestScore.toString()
        }
}
}