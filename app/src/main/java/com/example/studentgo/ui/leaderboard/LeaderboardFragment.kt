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

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private var score = 0
    private val leaderboardEntries = mutableListOf<LeaderboardEntry>()
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val handler = Handler(Looper.getMainLooper())

    private val resetRunnable = object : Runnable {
        override fun run() {
            resetScore()
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

        // Initialize RecyclerView and Adapter
        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leaderboardAdapter
        }

        // Display the initial score
        updateScoreDisplay()

        // Add button increases the score
        binding.bAdd.setOnClickListener {
            score++
            updateScoreDisplay()
        }

        // Leaderboard button adds score to leaderboard and resets score
        binding.bLeaderboard.setOnClickListener {
            addToLeaderboard()
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

        // Add a new entry to the leaderboard with the current score and user's email
        val entry = LeaderboardEntry(userEmail, score)
        leaderboardEntries.add(entry)
        leaderboardEntries.sortByDescending { it.score }
        leaderboardAdapter.notifyDataSetChanged()

        // Reset the score after adding to leaderboard
        resetScore()
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

