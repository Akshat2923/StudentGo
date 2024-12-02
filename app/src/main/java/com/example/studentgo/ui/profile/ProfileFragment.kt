package com.example.studentgo.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.studentgo.LoginActivity
import com.example.studentgo.MainActivity
import com.example.studentgo.R
import com.example.studentgo.StudentGoApp
import com.example.studentgo.databinding.FragmentProfileBinding
import com.example.studentgo.model.UserRepository
import com.example.studentgo.model.firestore.FirebaseUserDaoImplementation
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    private lateinit var profileViewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Observe user data and update the UI
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.emailTextView.text = "Logged in as: ${user.email}"
            } else {
                redirectToLogin()
            }
        }

        // Sign out button click listener
        binding.logoutButton.setOnClickListener {
            profileViewModel.signOut()
        }

        // Delete account button click listener
        binding.deleteButton.setOnClickListener {
            profileViewModel.deleteUser { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                    redirectToLogin()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Update account button
        binding.updateButton.setOnClickListener {
            val newEmail = binding.emailEditText.text.toString()
            profileViewModel.updateUser(newEmail) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Please check your email to confirm", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Invalid email", Toast.LENGTH_SHORT).show()
                }
            }
        }
        auth = FirebaseAuth.getInstance()
        fetchAndDisplayScore()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireActivity().application as StudentGoApp
        val localUserDao = application.database.roomUserDao()
        val remoteUserDao = FirebaseUserDaoImplementation()

        val userRepository = UserRepository(application, localUserDao, remoteUserDao)
        profileViewModel.setUserRepository(userRepository)
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun fetchAndDisplayScore() {
        val currentUser = auth.currentUser ?: return
        val userEmail = currentUser.email ?: return

        FirebaseFirestore.getInstance().collection("users")
            .document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                val score = document.getLong("score")?.toInt() ?: 0
                updateScoreDisplay(score)
            }
    }

    private fun updateScoreDisplay(score: Int) {
        val scoreMenuItem = binding.topAppBar.menu.findItem(R.id.action_score)
        scoreMenuItem.title = "GO Points: $score"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}