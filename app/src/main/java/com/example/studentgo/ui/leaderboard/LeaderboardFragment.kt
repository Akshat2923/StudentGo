package com.example.studentgo.ui.leaderboard

import PublishScoreBottomSheet
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentgo.R
import com.example.studentgo.databinding.FragmentLeaderboardBinding
import com.example.studentgo.model.LeaderboardEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private var score = 0
    private val leaderboardEntries = mutableListOf<LeaderboardEntry>()
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var leaderboardRef: CollectionReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        leaderboardRef = firestore.collection("leaderboard")

        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leaderboardAdapter
        }

        fetchUsersScore()

        leaderboardRef.orderBy("score", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    leaderboardEntries.clear()
                    for (doc in snapshot.documents) {
                        val entry = doc.toObject(LeaderboardEntry::class.java)
                        entry?.let { leaderboardEntries.add(it) }
                    }
                    leaderboardAdapter.notifyDataSetChanged()
                }
            }

        updateScoreDisplay()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            val publishScoreBottomSheet = PublishScoreBottomSheet().apply {
                onPublishClick = {
                    fetchUsersScore()
                    addOrUpdateLeaderboardScore()
                    Toast.makeText(requireContext(), "GO Points published to leaderboard!", Toast.LENGTH_SHORT).show()
                }
            }
            publishScoreBottomSheet.show(childFragmentManager, PublishScoreBottomSheet.TAG)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_podium -> {
                    findNavController().navigate(
                        R.id.navigation_podium,
                        null,
                        navOptions {
                            anim {
                                enter = androidx.navigation.ui.R.anim.nav_default_enter_anim
                                exit = androidx.navigation.ui.R.anim.nav_default_exit_anim
                                popEnter = androidx.navigation.ui.R.anim.nav_default_pop_enter_anim
                                popExit = androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
                            }
                        }
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchUsersScore() {
        val currentUser = auth.currentUser ?: return
        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    score = document.getLong("score")?.toInt() ?: 0
                    updateScoreDisplay()
                } else {
                    score = 0
                    updateScoreDisplay()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error fetching user score", e)
                score = 0
                updateScoreDisplay()
            }
    }

    private fun updateScoreDisplay() {
        binding.tvScore.text = "GO Points: $score"
    }

    private fun addOrUpdateLeaderboardScore() {
        val currentUser = auth.currentUser ?: return
        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        val entry = LeaderboardEntry(userEmail, score)
        leaderboardRef.document(currentUser.uid).set(entry)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.userNameTextView.text = entry.userName
        holder.scoreTextView.text = "GO Points: ${entry.score}"
    }

    override fun getItemCount() = entries.size
}