package com.example.studentgo.ui.leaderboard
import android.os.Bundle
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
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up RecyclerView
        val leaderboardEntries = listOf(
            LeaderboardEntry("Alice", 100),
            LeaderboardEntry("Bob", 95),
            LeaderboardEntry("Charlie", 85)
        )
        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leaderboardAdapter
        }

        // Set up score and buttons
        val tvScore = binding.tvScore
        tvScore.text = "SCORE: $score"

        binding.bAdd.setOnClickListener {
            score++
            tvScore.text = "SCORE: $score"
        }

        binding.bEnd.setOnClickListener {
            score = 0
            tvScore.text = "SCORE: $score"
        }

        return root
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
        holder.scoreTextView.text = "Score: ${entry.score}"
    }

    override fun getItemCount() = entries.size
}
