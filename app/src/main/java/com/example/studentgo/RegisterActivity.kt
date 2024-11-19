//package com.example.studentgo
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Toast
//import androidx.lifecycle.lifecycleScope
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//import com.example.studentgo.databinding.ActivityRegisterActivtyBinding
//import com.example.studentgo.model.LeaderboardEntry
//import com.google.firebase.firestore.ktx.firestore
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import android.util.Log
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityRegisterActivtyBinding
//    private lateinit var auth: FirebaseAuth
//    private val database = Firebase.firestore
//    private val leaderboardRef = database.collection("leaderboard")
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRegisterActivtyBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        auth = Firebase.auth
//
//        binding.buttonRegister.setOnClickListener {
//            val email = binding.editTextEmail.text.toString()
//            val password = binding.editTextPassword.text.toString()
//            val confirmPassword = binding.editTextConfirmPassword.text.toString()
//
//            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
//                if (password == confirmPassword) {
//                    createAccount(email, password)
//                } else {
//                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        binding.textViewLogin.setOnClickListener {
//            finish() // This will close the RegisterActivity and return to LoginActivity
//        }
//    }
//
//    private fun createAccount(email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    Toast.makeText(baseContext, "Account created successfully.", Toast.LENGTH_SHORT).show()
//
//                    user?.let {
//                        // Launch coroutine to add the user to the leaderboard
//                        lifecycleScope.launch {
//                            addUserToFirestore(it.uid, it.email) // Add the user with the UID as the document ID
//                        }
//                    }
//
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                } else {
//                    Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private suspend fun addUserToFirestore(userId: String, userEmail: String?) {
//        val score = 0 // New users start with a score of 0
//        Log.d("Firestore", "Adding user with UID: $userId and email: $userEmail")
//
//        val userData = mapOf(
//            "email" to userEmail,
//            "score" to score
//        )
//
//        try {
//            // Add user data to Firestore with UID as the document ID
//            database.collection("users").document(userId).set(userData).await()
//            Log.d("Firestore", "User added successfully to Firestore")
//
//            // Add user to leaderboard
//            val entry = LeaderboardEntry(userEmail ?: "Unknown", score)
//            leaderboardRef.document(userId).set(entry)
//                .addOnSuccessListener {
//                    Log.d("Leaderboard", "User added to leaderboard successfully")
//                }
//                .addOnFailureListener {
//                    Log.e("Leaderboard", "Failed to add user to leaderboard", it)
//                }
//        } catch (e: Exception) {
//            Log.e("Firestore", "Error adding user to Firestore", e)
//        }
//    }
//
//    // Update the user's score
//    fun updateUserScore(userId: String, newScore: Int) {
//        val firestore = Firebase.firestore
//        firestore.collection("users").document(userId).update("score", newScore)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Score updated successfully in Firestore")
//            }
//            .addOnFailureListener {
//                Log.e("Firestore", "Failed to update score in Firestore", it)
//            }
//
//        val entry = LeaderboardEntry(auth.currentUser?.email ?: "Unknown", newScore)
//        leaderboardRef.document(userId).set(entry)
//            .addOnSuccessListener {
//                Log.d("Leaderboard", "Leaderboard score updated successfully")
//            }
//            .addOnFailureListener {
//                Log.e("Leaderboard", "Failed to update leaderboard score", it)
//            }
//    }
//}


package com.example.studentgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.studentgo.databinding.ActivityRegisterActivtyBinding
import com.example.studentgo.model.LeaderboardEntry
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterActivtyBinding
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore
    private val leaderboardRef = database.collection("leaderboard")

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
            finish() // Close RegisterActivity and return to LoginActivity
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        Toast.makeText(baseContext, "Account created successfully.", Toast.LENGTH_SHORT).show()

                        // Save user email to SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                        sharedPreferences.edit()
                            .putString("user_email", user.email) // Store the email of the newly registered user
                            .apply()

                        // Add user data to Firestore
                        lifecycleScope.launch {
                            addUserToFirestore(user.uid, user.email)
                        }

                        // Navigate to MainActivity
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private suspend fun addUserToFirestore(userId: String, userEmail: String?) {
        val score = 0 // New users start with a score of 0
        Log.d("Firestore", "Adding user with UID: $userId and email: $userEmail")

        val userData = mapOf(
            "email" to userEmail,
            "score" to score
        )

        try {
            // Add user data to Firestore with UID as the document ID
            database.collection("users").document(userId).set(userData).await()
            Log.d("Firestore", "User added successfully to Firestore")

            // Add user to leaderboard
            val entry = LeaderboardEntry(userEmail ?: "Unknown", score)
            leaderboardRef.document(userId).set(entry)
                .addOnSuccessListener {
                    Log.d("Leaderboard", "User added to leaderboard successfully")
                }
                .addOnFailureListener {
                    Log.e("Leaderboard", "Failed to add user to leaderboard", it)
                }
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding user to Firestore", e)
        }
    }
}
