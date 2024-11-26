package com.example.studentgo

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.studentgo.model.UserRepository
import com.example.studentgo.model.firestore.FirebaseUser
import com.example.studentgo.model.firestore.FirebaseUserDao
import com.example.studentgo.model.room.RoomUser
import com.example.studentgo.model.room.RoomUserDao
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserRepositoryTest {
    private lateinit var userRepository: UserRepository
    private lateinit var mockLocalDao: RoomUserDao
    private lateinit var mockRemoteDao: FirebaseUserDao

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        mockLocalDao = mockk(relaxed = true)
        mockRemoteDao = mockk(relaxed = true)
        userRepository = UserRepository(context, mockLocalDao, mockRemoteDao)
    }

    @Test
    fun testCreateUser() = runTest {
        // Arrange
        val email = "test@example.com"
        val expectedUser = RoomUser(email, 0, "")
        val mockFirebaseUser = FirebaseUser(email, "", 0)

        coEvery { mockLocalDao.insertUser(any()) } returns Unit
        coEvery { mockRemoteDao.convertToRemoteModel(any()) } returns mockFirebaseUser
        coEvery { mockRemoteDao.insertUser(any()) } returns Unit

        // Act
        val result = userRepository.createUser(email)

        // Assert
        assertEquals(expectedUser.email, result.email)
        assertEquals(expectedUser.score, result.score)
        assertEquals(expectedUser.name, result.name)
    }

    @Test
    fun testGetExistingUserFromLocalDatabase() = runTest {
        // Arrange
        val email = "test@example.com"
        val existingUser = RoomUser(email, 10, "Test User")
        coEvery { mockLocalDao.getUser(email) } returns existingUser

        // Act
        val result = userRepository.getUser(email)

        // Assert
        assertEquals(existingUser, result)
    }

    @Test
    fun testGetUserFromRemoteWhenNotInLocal() = runTest {
        // Arrange
        val email = "test@example.com"
        val remoteUser = FirebaseUser(email, "Test User", 10)
        val expectedLocalUser = RoomUser(email, 10, "Test User")

        coEvery { mockLocalDao.getUser(email) } returns null
        coEvery { mockRemoteDao.getUser(email) } returns remoteUser
        coEvery { mockRemoteDao.convertToLocalModel(remoteUser) } returns expectedLocalUser
        coEvery { mockLocalDao.insertUser(any()) } returns Unit

        // Act
        val result = userRepository.getUser(email)

        // Assert
        assertEquals(expectedLocalUser, result)
    }
}