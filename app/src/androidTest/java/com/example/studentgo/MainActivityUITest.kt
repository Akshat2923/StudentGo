package com.example.studentgo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityUITest {

    @Test
    fun test1_MapNavigation() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java)

        // Wait for map to load
        Thread.sleep(3000)

        // Click on the My Location button (Google Maps default button)
        onView(withContentDescription("My Location"))
            .perform(click())

        // Wait for location to be found
        Thread.sleep(3000)

        // Navigate to the map
        onView(withId(R.id.navigation_map))
            .perform(click())

        Thread.sleep(3000)

        onView(withId(R.id.action_popular))
            .perform(click())
        
        Thread.sleep(3000)
    }

    @Test
    fun test2_LeaderboardNavigation() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java)

        // Navigate to the leaderboard
        onView(withId(R.id.navigation_leaderboard))
            .perform(click())

        // Wait for leaderboard to load
        Thread.sleep(3000)

        onView(withId(R.id.action_podium))
            .perform(click())

        Thread.sleep(3000)
    }

    @Test
    fun test3_ProfileNavigationAndSignOut() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java)

        // Navigate to the profile
        onView(withId(R.id.navigation_profile))
            .perform(click())

        // Wait for profile to load
        Thread.sleep(3000)

        // Click the sign-out button
        onView(withId(R.id.logoutButton))
            .perform(click())
    }
}