package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.GojoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GojoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: GojoRepository
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = androidx.room.Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = GojoRepository(db.gojoDao())
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `admin password verification accepts Gojo and rejects wrong password`() {
        assertTrue(repository.verifyAdminPassword("Gojo"))
        assertTrue(repository.verifyAdminPassword("gojo"))
        assertTrue(repository.verifyAdminPassword(" Gojo "))
        assertFalse(repository.verifyAdminPassword("Sukuna"))
        assertFalse(repository.verifyAdminPassword(""))
    }

    @Test
    fun `initial wallet contains 30 Gojo Coins and seeds default tasks`() = runTest {
        repository.initializeSeedData()

        val wallet = repository.walletFlow.first()
        assertNotNull(wallet)
        assertEquals(30, wallet?.coinsBalance)

        val tasks = repository.tasksFlow.first()
        assertTrue(tasks.isNotEmpty())
        assertTrue(tasks.any { it.id == "task_first_video" })
        assertTrue(tasks.any { it.id == "task_post_video" })
    }

    @Test
    fun `admin granting coins credits user wallet`() = runTest {
        repository.initializeSeedData()
        val initialWallet = repository.walletFlow.first()!!
        val initialBalance = initialWallet.coinsBalance

        val grantResult = repository.adminGrantCoins(100, "Community Winner")
        assertTrue(grantResult.isSuccess)
        assertEquals(100, grantResult.getOrNull())

        val updatedWallet = repository.walletFlow.first()!!
        assertEquals(initialBalance + 100, updatedWallet.coinsBalance)
    }

    @Test
    fun `admin can create and host live community events`() = runTest {
        repository.initializeSeedData()

        val eventResult = repository.adminCreateEvent(
            title = "Infinity Void 48H Sprint",
            description = "Double Gojo Coins on all video generations",
            bonusCoins = 50,
            gradientHex = "#00E5FF",
            tag = "FLASH EVENT"
        )
        assertTrue(eventResult.isSuccess)
        val event = eventResult.getOrNull()!!
        assertEquals("Infinity Void 48H Sprint", event.title)

        val allEvents = repository.allEventsFlow.first()
        assertTrue(allEvents.any { it.title == "Infinity Void 48H Sprint" })
    }

    @Test
    fun `generating video deducts 10 Gojo Coins and completes first video task`() = runTest {
        repository.initializeSeedData()
        val initialWallet = repository.walletFlow.first()!!
        val initialBalance = initialWallet.coinsBalance

        val genResult = repository.generateVideo(
            title = "Test Satoru Void",
            prompt = "Gojo domain expansion dynamic zoom",
            imageUrl = "",
            style = "Anime",
            cameraMotion = "Dynamic Zoom",
            aspectRatio = "9:16",
            durationSec = 5
        )
        assertTrue(genResult.isSuccess)

        val updatedWallet = repository.walletFlow.first()!!
        assertEquals(initialBalance - 10, updatedWallet.coinsBalance)

        val tasks = repository.tasksFlow.first()
        val firstVideoTask = tasks.first { it.id == "task_first_video" }
        assertTrue(firstVideoTask.isCompleted)
    }
}
