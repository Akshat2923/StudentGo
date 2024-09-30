package com.example.studentgo

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.studentgo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val logTag = "MAIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(logTag,"onCreate() triggered")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onPause() {
        super.onPause()

        Log.d(logTag, "onPause() triggered")
    }

    override fun onResume() {
        super.onResume()

        Log.d(logTag, "onResume() triggered")
    }

    override fun onStart() {
        super.onStart()

        Log.d(logTag, "onStart() triggered")
    }

    override fun onRestart() {
        super.onRestart()

        Log.d(logTag, "onRestart() triggered")
    }

    override fun onStop() {
        super.onStop()

        Log.d(logTag, "onStop() triggered")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(logTag, "onDestroy() triggered")
    }
}