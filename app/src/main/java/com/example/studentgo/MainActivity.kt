package com.example.studentgo

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.studentgo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.studentgo.ui.map.MapViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private lateinit var email: String
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add these lines before setContentView
        window.statusBarColor = ContextCompat.getColor(this, R.color.creme)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.creme)

        binding = ActivityMainBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth
        mapViewModel.setAuth(auth)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_leaderboard, R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)

        // Set up AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                // FirebaseUser is not signed in, redirect to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // Close MainActivity to prevent going back to it
            } else {
                // FirebaseUser is signed in
                currentUser.email?.let {
                    mapViewModel.setEmail(it)
                    mapViewModel.getUser(it)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener) // Add listener in onStart
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener) // Remove listener in onStop
    }
}