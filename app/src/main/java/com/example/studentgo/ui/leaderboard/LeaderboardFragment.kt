////package com.example.studentgo.ui.leaderboard
////
////import android.content.Context
////import android.os.Bundle
////import android.os.Handler
////import android.os.Looper
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.TextView
////import androidx.fragment.app.Fragment
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.example.studentgo.R
////import com.example.studentgo.databinding.FragmentLeaderboardBinding
////import com.example.studentgo.models.LeaderboardEntry
////import com.google.firebase.database.*
////
////class LeaderboardFragment : Fragment() {
////
////    private var _binding: FragmentLeaderboardBinding? = null
////    private val binding get() = _binding!!
////
////    private var score = 0
////    private val leaderboardEntries = mutableListOf<LeaderboardEntry>()
////    private lateinit var leaderboardAdapter: LeaderboardAdapter
////    private val handler = Handler(Looper.getMainLooper())
////
////    private lateinit var leaderboardRef: DatabaseReference
////
////    private val resetRunnable = object : Runnable {
////        override fun run() {
////            resetScore()
////            handler.postDelayed(this, 10000) // Repeat every 10 seconds
////        }
////    }
////
////    override fun onCreateView(
////        inflater: LayoutInflater,
////        container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
////        val root: View = binding.root
////
////        // Initialize Firebase Database reference for leaderboard
////        val database = FirebaseDatabase.getInstance()
////        leaderboardRef = database.reference.child("leaderboard")
////
////        // Initialize RecyclerView and Adapter
////        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries)
////        binding.recyclerView.apply {
////            layoutManager = LinearLayoutManager(context)
////            adapter = leaderboardAdapter
////        }
////
////        // Set up listener to get leaderboard updates from Firebase
////        leaderboardRef.addValueEventListener(object : ValueEventListener {
////            override fun onDataChange(snapshot: DataSnapshot) {
////                leaderboardEntries.clear() // Clear the list to avoid duplicates
////                for (entrySnapshot in snapshot.children) {
////                    val entry = entrySnapshot.getValue(LeaderboardEntry::class.java)
////                    entry?.let { leaderboardEntries.add(it) }
////                }
////                leaderboardEntries.sortByDescending { it.score } // Sort by score
////                leaderboardAdapter.notifyDataSetChanged() // Notify adapter of data change
////            }
////
////            override fun onCancelled(error: DatabaseError) {
////                // Handle error (e.g., log the error message)
////            }
////        })
////
////        // Display the initial score
////        updateScoreDisplay()
////
////        // Add button increases the score
////        binding.bAdd.setOnClickListener {
////            score++
////            updateScoreDisplay()
////        }
////
////        // Leaderboard button adds score to leaderboard and resets score
////        binding.bLeaderboard.setOnClickListener {
////            addToLeaderboard()
////        }
////
////        // Start the periodic reset
////        handler.postDelayed(resetRunnable, 10000)
////
////        return root
////    }
////
////    private fun updateScoreDisplay() {
////        binding.tvScore.text = "SCORE: $score"
////    }
////
////    private fun resetScore() {
////        score = 0
////        updateScoreDisplay()
////    }
////
////    private fun addToLeaderboard() {
////        // Retrieve the user's email from SharedPreferences
////        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
////        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"
////
////        // Add a new entry to Firebase Database under "leaderboard" node
////        val userId = leaderboardRef.push().key ?: return // Unique key for each entry
////        val entry = LeaderboardEntry(userEmail, score)
////
////        leaderboardRef.child(userId).setValue(entry)
////            .addOnSuccessListener {
////                // Successfully added entry to Firebase; no further action needed as listener will update UI
////            }
////            .addOnFailureListener {
////                // Handle failure (e.g., show a Toast)
////            }
////
////        // Reset the score after adding to leaderboard
////        resetScore()
////    }
////
////    override fun onDestroyView() {
////        super.onDestroyView()
////        _binding = null
////        handler.removeCallbacks(resetRunnable) // Stop the periodic reset when fragment is destroyed
////    }
////}
////
////class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
////
////    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
////        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
////        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
////    }
////
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
////        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_entry, parent, false)
////        return ViewHolder(view)
////    }
////
////    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
////        val entry = entries[position]
////        holder.userNameTextView.text = entry.userName
////        holder.scoreTextView.text = "Score: ${entry.score}"
////    }
////
////    override fun getItemCount() = entries.size
////}
//
//
//package com.example.studentgo.ui.leaderboard
//
//import android.content.Context
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.studentgo.R
//import com.example.studentgo.databinding.FragmentLeaderboardBinding
//import com.example.studentgo.models.LeaderboardEntry
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//class LeaderboardFragment : Fragment() {
//
//    private var _binding: FragmentLeaderboardBinding? = null
//    private val binding get() = _binding!!
//
//    private var score = 0
//    private val leaderboardEntries = mutableListOf<LeaderboardEntry>()
//    private lateinit var leaderboardAdapter: LeaderboardAdapter
//    private val handler = Handler(Looper.getMainLooper())
//
//    private lateinit var leaderboardRef: DatabaseReference
//    private lateinit var auth: FirebaseAuth
//
//    private val resetRunnable = object : Runnable {
//        override fun run() {
//            resetScore()
//            handler.postDelayed(this, 10000) // Repeat every 10 seconds
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Initialize Firebase instances
//        auth = FirebaseAuth.getInstance()
//        leaderboardRef = FirebaseDatabase.getInstance().reference.child("leaderboard")
//
//        // Initialize RecyclerView and Adapter
//        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries)
//        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = leaderboardAdapter
//        }
//
//        // Set up listener to get leaderboard updates from Firebase
//        leaderboardRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                leaderboardEntries.clear()
//                for (entrySnapshot in snapshot.children) {
//                    val entry = entrySnapshot.getValue(LeaderboardEntry::class.java)
//                    entry?.let { leaderboardEntries.add(it) }
//                }
//                leaderboardEntries.sortByDescending { it.score }
//                leaderboardAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
//            }
//        })
//
//        // Display the initial score
//        updateScoreDisplay()
//
//        // Add button increases the score
//        binding.bAdd.setOnClickListener {
//            score++
//            updateScoreDisplay()
//        }
//
//        // Leaderboard button adds score to leaderboard and resets score
//        binding.bLeaderboard.setOnClickListener {
//            addToLeaderboard()
//        }
//
//        // Start the periodic reset
//        handler.postDelayed(resetRunnable, 10000)
//
//        return root
//    }
//
//    private fun updateScoreDisplay() {
//        binding.tvScore.text = "SCORE: $score"
//    }
//
//    private fun resetScore() {
//        score = 0
//        updateScoreDisplay()
//    }
//
//    private fun addToLeaderboard() {
//        val user = auth.currentUser ?: return
//        val userEmail = user.email ?: "Unknown User"
//        val userId = user.uid
//
//        val entry = LeaderboardEntry(userEmail, score)
//        leaderboardRef.child(userId).setValue(entry)
//            .addOnSuccessListener {
//                // Score updated successfully
//            }
//            .addOnFailureListener {
//                // Handle failure
//            }
//
//        resetScore()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//        handler.removeCallbacks(resetRunnable)
//    }
//}
//
//class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
//        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_entry, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val entry = entries[position]
//        holder.userNameTextView.text = entry.userName
//        holder.scoreTextView.text = "Score: ${entry.score}"
//    }
//
//    override fun getItemCount() = entries.size
//}


package com.example.studentgo.ui.leaderboard

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentgo.R
import com.example.studentgo.databinding.FragmentLeaderboardBinding
import com.example.studentgo.models.LeaderboardEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private var score = 0
    private val leaderboardEntries = mutableListOf<LeaderboardEntry>()
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var leaderboardRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val resetRunnable = object : Runnable {
        override fun run() {
            addOrUpdateLeaderboardScore() // Add or update the score in the leaderboard
            handler.postDelayed({ resetScore() }, 100) // Repeat every 10 seconds
            handler.postDelayed(this, 10000) // Repeat every 10 seconds
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

        // Initialize Firebase Database reference for leaderboard
        val database = FirebaseDatabase.getInstance()
        leaderboardRef = database.reference.child("leaderboard")

        // Initialize RecyclerView and Adapter
        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leaderboardAdapter
        }

        // Set up listener to get leaderboard updates from Firebase
        leaderboardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                leaderboardEntries.clear() // Clear the list to avoid duplicates
                for (entrySnapshot in snapshot.children) {
                    val entry = entrySnapshot.getValue(LeaderboardEntry::class.java)
                    entry?.let { leaderboardEntries.add(it) }
                }
                leaderboardEntries.sortByDescending { it.score } // Sort by score
                leaderboardAdapter.notifyDataSetChanged() // Notify adapter of data change
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error (e.g., log the error message)
            }
        })

        // Display the initial score
        updateScoreDisplay()

        // Add button increases the score
        binding.bAdd.setOnClickListener {
            score++
            updateScoreDisplay()
        }

        // Start the periodic reset
        handler.postDelayed(resetRunnable, 10000)

        return root
    }

    private fun updateScoreDisplay() {
        binding.tvScore.text = "SCORE: $score"
    }

    private fun resetScore() {
        score = 0
        updateScoreDisplay()
    }

    private fun addToLeaderboard() {
        // Retrieve the user's email from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        // Add or update user entry in Firebase Database under "leaderboard" node
        val userId = auth.currentUser?.uid ?: return // Unique key for each user
        val entry = LeaderboardEntry(userEmail, score)

        leaderboardRef.child(userId).setValue(entry)
            .addOnSuccessListener {
                // Successfully added or updated entry in Firebase
            }
            .addOnFailureListener {
                // Handle failure if needed
            }
    }

    private fun addOrUpdateLeaderboardScore() {
        // This function is called every 10 seconds to add or update the leaderboard score in Firebase
        val userId = auth.currentUser?.uid ?: return
        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        val entry = LeaderboardEntry(userEmail, score)

        leaderboardRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // If the entry doesn't exist, add it
                    leaderboardRef.child(userId).setValue(entry)
                } else {
                    // If the entry exists, update the score
                    leaderboardRef.child(userId).child("score").setValue(score)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
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
