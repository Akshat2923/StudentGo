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
import com.example.studentgo.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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

        return binding.root
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}