package com.example.studentgo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.studentgo.ui.map.MapViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.studentgo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var email: String
    private val mapViewModel: MapViewModel by viewModels()

    private val resetRunnable = object : Runnable {
        override fun run() {
            resetAllScores()
            handler.postDelayed(this, 3600000) // Reset every 10 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.creme)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.creme)

        binding = ActivityMainBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)

        auth = Firebase.auth
        mapViewModel.setAuth(auth)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_leaderboard, R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                currentUser.email?.let {
                    mapViewModel.setEmail(it)
                    mapViewModel.getUser(it)
                }
            }
        }

        // Start the reset timer
        handler.post(resetRunnable)
    }

    private fun resetAllScores() {
        val firestore = FirebaseFirestore.getInstance()
        val leaderboardRef = firestore.collection("leaderboard")
        val usersRef = firestore.collection("users")

        // Reset leaderboard scores
        leaderboardRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                leaderboardRef.document(document.id).update("score", 0)
                    .addOnSuccessListener {
                        Log.d("Leaderboard", "Score reset for user: ${document.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Leaderboard", "Error resetting score for user: ${document.id}", e)
                    }
            }
        }

        // Reset user scores
        usersRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                usersRef.document(document.id).update("score", 0)
                    .addOnSuccessListener {
                        Log.d("Users", "Score reset for user: ${document.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Users", "Error resetting score for user: ${document.id}", e)
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(resetRunnable)
    }
}