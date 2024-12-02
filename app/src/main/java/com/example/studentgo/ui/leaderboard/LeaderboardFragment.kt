package com.example.studentgo.ui.leaderboard

import PublishScoreBottomSheet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentgo.R
import com.example.studentgo.databinding.FragmentLeaderboardBinding
import com.example.studentgo.model.LeaderboardEntry
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardFragment : Fragment() {

    companion object {
        private const val RESET_INTERVAL_MS = 604800000L //for ui timer
    }

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private var score = 0
    private val leaderboardEntries = mutableListOf<LeaderboardEntry>()
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var leaderboardRef: CollectionReference
    private lateinit var auth: FirebaseAuth
    private var animator: ValueAnimator? = null
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
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

        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries, userEmail)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leaderboardAdapter
        }

        fetchUsersScore()
        startResetProgressAnimation()

        // Set up leaderboard listener
        leaderboardRef.orderBy("score", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    leaderboardEntries.clear()
                    for (doc in snapshot.documents) {
                        val entry = doc.toObject(LeaderboardEntry::class.java)
                        entry?.let { leaderboardEntries.add(it) }
                    }
                    leaderboardAdapter.notifyDataSetChanged()
                }
            }

        return root
    }

    private fun formatTimeRemaining(timeInMillis: Long): String {
        val seconds = (timeInMillis / 1000) % 60
        val minutes = (timeInMillis / (1000 * 60)) % 60
        val hours = (timeInMillis / (1000 * 60 * 60)) % 24
        val days = timeInMillis / (1000 * 60 * 60 * 24)

        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

    private fun startResetProgressAnimation() {
        if (_binding == null) return

        val progressIndicator = binding.resetProgressIndicator
        progressIndicator.max = 100

        // Calculate initial progress based on current time
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime % RESET_INTERVAL_MS
        val remainingTime = RESET_INTERVAL_MS - elapsedTime
        val initialProgress = ((remainingTime.toFloat() / RESET_INTERVAL_MS.toFloat()) * 100).toInt()

        animator?.cancel()
        animator = ValueAnimator.ofInt(initialProgress, 0).apply {
            duration = remainingTime  // Set duration to actual remaining time
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                if (_binding == null) {
                    cancel()
                    return@addUpdateListener
                }
                val progress = animation.animatedValue as Int
                progressIndicator.progress = progress

                // Calculate remaining time directly from current time
                val currentTimeInAnimation = System.currentTimeMillis()
                val elapsedTimeInAnimation = currentTimeInAnimation % RESET_INTERVAL_MS
                val remainingTimeInAnimation = RESET_INTERVAL_MS - elapsedTimeInAnimation

                binding.timerTextView.text = formatTimeRemaining(remainingTimeInAnimation)
            }

            addListener(onEnd = {
                if (_binding != null) {
                    startResetProgressAnimation()
                }
            })

            start()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            val publishScoreBottomSheet = PublishScoreBottomSheet().apply {
                onPublishClick = {
                    fetchUsersScore()
                    addOrUpdateLeaderboardScore()
                    Toast.makeText(
                        requireContext(),
                        "GO Points published to leaderboard!",
                        Toast.LENGTH_SHORT
                    ).show()
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
        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
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
        binding.tvScore.text = "$score"
    }

    private fun addOrUpdateLeaderboardScore() {
        val currentUser = auth.currentUser ?: return
        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "Unknown User") ?: "Unknown User"

        val entry = LeaderboardEntry(userEmail, score)
        leaderboardRef.document(currentUser.uid).set(entry)
    }

    override fun onResume() {
        super.onResume()
        startResetProgressAnimation()
    }

    override fun onPause() {
        super.onPause()
        animator?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animator?.cancel()
        _binding = null
    }
}

class LeaderboardAdapter(
    private val entries: List<LeaderboardEntry>,
    private val currentUserEmail: String
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
        val container: LinearLayout = view.findViewById(R.id.leaderboard_item_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]  // Use entries list directly instead of getItem
        holder.userNameTextView.text = entry.userName
        holder.scoreTextView.text = "GO Points: ${entry.score}"

        // Handle current user highlighting
        if (entry.userName == currentUserEmail) {
            // Current user - use accent color
            holder.userNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.sage_green))
            holder.container.findViewById<ImageView>(R.id.profileIcon)?.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.sage_green)
            )
            holder.scoreTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.sage_green))

        } else {
            // Other users - use default colors
            holder.userNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            holder.container.findViewById<ImageView>(R.id.profileIcon)?.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.black)
            )
        }
    }

    override fun getItemCount() = entries.size
}