package com.example.studentgo.ui.leaderboard

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var leaderboardRef: CollectionReference
    private lateinit var auth: FirebaseAuth

    private val resetRunnable = object : Runnable {
        override fun run() {
            resetAllLeaderboardScores() // Add or update the score in Firestore
            fetchLeaderboardScore()
            updateScoreDisplay()
            addOrUpdateUsersScore()
            handler.postDelayed(this, 10000) // Repeat every 10 seconds

        }
    }

    // Function to update all scores to 0 in Firestore
    private fun resetAllLeaderboardScores() {
        leaderboardRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Update the score to 0 for each leaderboard entry
                    leaderboardRef.document(document.id).update("score", 0)
                        .addOnSuccessListener {
                            Log.d("Leaderboard", "Score reset for user: ${document.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Leaderboard", "Error resetting score for user: ${document.id}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Leaderboard", "Error fetching leaderboard documents", e)
            }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            // Handle publish to leaderboard
            fetchUsersScore()
            addOrUpdateLeaderboardScore()
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

        // Initialize RecyclerView and Adapter
        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leaderboardAdapter
        }

        // Fetch initial score from Firestore
        fetchUsersScore()

        // Set up Firestore snapshot listener for leaderboard
        leaderboardRef.orderBy("score", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
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

        // Display initial score
        updateScoreDisplay()



        // Periodic update of score in Firestore
        handler.postDelayed(resetRunnable, 10000)

        return root
    }

    private fun fetchLeaderboardScore() {
        val currentUser = auth.currentUser ?: return
        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        // Query Firestore for the current user's document by email
        leaderboardRef.whereEqualTo("userName", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0] // Assume each user has one document
                    val firestoreScore = document.getLong("score")?.toInt() ?: 0 // Default to 0 if null
                    score = firestoreScore // Initialize local score
                    updateScoreDisplay() // Update the displayed score
                } else {
                    // No entry found; default score to 0
                    score = 0 // Default to 0 if there's an error
                    updateScoreDisplay()

                }
            }
            .addOnFailureListener { e ->
                // Handle error (e.g., network issue)
                score = 0 // Default to 0 if there's an error
                updateScoreDisplay()
            }
    }

    private fun fetchUsersScore() {
        val currentUser = auth.currentUser ?: return
        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        // Log the email for debugging
        Log.d("fetchUsersScore", "Fetching score for email: $userEmail")

        // Get the document directly using the email as the document ID
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firestoreScore = document.getLong("score")?.toInt() ?: 0
                    score = firestoreScore
                    updateScoreDisplay()
                    Log.d("fetchUsersScore", "Score fetched successfully: $firestoreScore")
                } else {
                    score = 0
                    updateScoreDisplay()
                    Log.d("fetchUsersScore", "No document found for email: $userEmail")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error fetching user score", e)
                score = 0
                updateScoreDisplay()
            }
    }

    private fun updateUserId(newUserId: String) {
        // Update any cached references to the user ID
        // Optionally log the new user for debugging purposes
    }

    private fun updateScoreDisplay() {
        binding.tvScore.text = "SCORE: $score"
    }


    private fun addOrUpdateUsersScore() {
        val currentUser = auth.currentUser ?: return
        val userEmail = currentUser.email ?: return

        // Create a map of fields to update
        val updates = hashMapOf<String, Any>(
            "score" to 0,
            "lastResetTime" to System.currentTimeMillis() // Add a timestamp for the reset
        )

        // Update the score directly using the email as document ID
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.document(userEmail)
            .update(updates)
            .addOnSuccessListener {
                Log.d("Leaderboard", "Score successfully reset to 0 for user: $userEmail")
                // Force a refresh of the score in memory
                score = 0
                updateScoreDisplay()
            }
            .addOnFailureListener { e ->
                Log.e("Leaderboard", "Error resetting score for user: $userEmail", e)
            }
    }

    private fun addOrUpdateLeaderboardScore() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // No user is signed in
            return
        }

        val userId = currentUser.uid
        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        val entry = LeaderboardEntry(userEmail, score)

        // Add or update Firestore document for the current user
        leaderboardRef.document(userId).set(entry)
            .addOnSuccessListener {
                // Successfully added or updated entry
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(resetRunnable) // Stop the periodic reset when fragment is destroyed
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
        holder.scoreTextView.text = "Score: ${entry.score}"
    }

    override fun getItemCount() = entries.size
}
