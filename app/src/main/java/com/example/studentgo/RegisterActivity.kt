package com.example.studentgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.example.studentgo.databinding.ActivityRegisterActivtyBinding
import com.example.studentgo.models.LeaderboardEntry

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterActivtyBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private val leaderboardRef = database.reference.child("leaderboard")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    createAccount(email, password)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewLogin.setOnClickListener {
            finish() // This will close the RegisterActivity and return to LoginActivity
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Account created successfully.", Toast.LENGTH_SHORT).show()

                    user?.let {
                        addUserToLeaderboard(it.uid, email)
                    }

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Helper function to add a user entry to the leaderboard with an initial score of 0
    private fun addUserToLeaderboard(userId: String, userEmail: String) {
        val initialScore = 0
        val entry = LeaderboardEntry(userEmail, initialScore)
        leaderboardRef.child(userId).setValue(entry)
            .addOnSuccessListener {
                // Successfully added initial score
            }
            .addOnFailureListener {
                // Handle failure if needed
            }
    }
}