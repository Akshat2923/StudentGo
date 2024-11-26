package com.example.studentgo

import com.example.studentgo.model.room.RoomUser
import com.example.studentgo.model.room.RoomKnownLocation
import org.junit.Test
import org.junit.Assert.*

class StudentGoUnitTests {

    // Test Case 1: User Score Management
    @Test
    fun testUserScoreManagement() {
        val user = RoomUser("test@example.com", 0, "Test User")

        // Check initial score
        assertEquals(0, user.score)

        // Increment score and verify
        user.score += 10
        assertEquals(10, user.score)

        // Add more points and verify total
        user.score += 5
        assertEquals(15, user.score)
    }

    // Test Case 2: Location Coordinate Validation
    @Test
    fun testLocationValidation() {
        // Test valid location
        val validLocation = RoomKnownLocation(
            "Valid Location",
            10,
            40.7128, // Valid latitude
            -74.0060 // Valid longitude
        )
        assertTrue(isValidLatitude(validLocation.latitude))
        assertTrue(isValidLongitude(validLocation.longitude))

        // Test invalid location
        val invalidLocation = RoomKnownLocation(
            "Invalid Location",
            10,
            91.0, // Invalid latitude (>90)
            -181.0 // Invalid longitude (<-180)
        )
        assertFalse(isValidLatitude(invalidLocation.latitude))
        assertFalse(isValidLongitude(invalidLocation.longitude))
    }

    // Test Case 3: Email Validation
    @Test
    fun testEmailValidation() {
        // Test valid emails
        assertTrue(isValidEmail("user@example.com"))
        assertTrue(isValidEmail("student@university.edu"))

        // Test invalid emails
        assertFalse(isValidEmail("invalid.email"))
        assertFalse(isValidEmail(""))
        assertFalse(isValidEmail("@invalid.com"))
        assertFalse(isValidEmail("user@.com"))
    }

    // Helper functions
    private fun isValidLatitude(latitude: Double): Boolean {
        return latitude in -90.0..90.0
    }

    private fun isValidLongitude(longitude: Double): Boolean {
        return longitude in -180.0..180.0
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}