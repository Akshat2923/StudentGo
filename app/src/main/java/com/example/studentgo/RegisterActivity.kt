package com.example.studentgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.example.studentgo.databinding.ActivityRegisterActivtyBinding
import com.example.studentgo.models.LeaderboardEntry
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

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
                        // Launch coroutine to add the user to the leaderboard
                        lifecycleScope.launch {
                            addUserToLeaderboard(it.uid, email) // Add the user with the current score
                        }
                    }

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private suspend fun addUserToLeaderboard(userId: String, userEmail: String) {
        val score = getUserScoreFromFirestore(userEmail) // Get the current score from Firestore
        Log.d("Leaderboard", "Score retrieved: $score")
        val entry = LeaderboardEntry(userEmail, score)

        leaderboardRef.child(userId).setValue(entry)
            .addOnSuccessListener {
                Log.d("Leaderboard", "User added to leaderboard successfully")
            }
            .addOnFailureListener {
                Log.e("Leaderboard", "Failed to add user to leaderboard", it)
            }
    }

    private suspend fun getUserScoreFromFirestore(email: String): Int {
        val firestore = Firebase.firestore
        val userDoc = firestore.collection("users").document(email).get().await()
        val score = userDoc.getLong("score")?.toInt() ?: 0
        Log.d("Firestore", "User score: $score")
        return score
    }

    // Update the user's score
    fun updateUserScore(userId: String, userEmail: String, newScore: Int) {
        val firestore = Firebase.firestore
        firestore.collection("users").document(userEmail).update("score", newScore)
            .addOnSuccessListener {
                Log.d("Firestore", "Score updated successfully in Firestore")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to update score in Firestore", it)
            }

        val entry = LeaderboardEntry(userEmail, newScore)
        leaderboardRef.child(userId).setValue(entry)
            .addOnSuccessListener {
                Log.d("Leaderboard", "Leaderboard score updated successfully")
            }
            .addOnFailureListener {
                Log.e("Leaderboard", "Failed to update leaderboard score", it)
            }
    }
}
