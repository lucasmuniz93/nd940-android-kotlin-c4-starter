package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase


    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminder_AndVerifyFromDB() = runBlockingTest {
        // Giver a new reminder
        val reminder = ReminderDTO(
            "Title",
            "Description",
            "Location",
            100.0,
            100.0
        )

        // When the reminder is saved
        database.reminderDao().saveReminder(reminder)
        val dbReminder = database.reminderDao().getReminderById(reminder.id)

        // Then the value is the same that we create
        assertThat(dbReminder, not(nullValue()))
        assertThat(dbReminder?.title, `is`(reminder.title))
        assertThat(dbReminder?.description, `is`(reminder.description))
        assertThat(dbReminder?.location, `is`(reminder.location))
        assertThat(dbReminder?.latitude, `is`(reminder.latitude))
        assertThat(dbReminder?.longitude, `is`(reminder.longitude))
    }

    @Test
    fun insertReminder_AndVerifyTheList() = runBlockingTest {
        // Giver a new reminder
        val reminder = ReminderDTO(
            "Title",
            "Description",
            "Location",
            100.0,
            100.0
        )
        val reminder2 = ReminderDTO(
            "Title-2",
            "Description-2",
            "Location-2",
            50.0,
            50.0
        )
        val reminder3 = ReminderDTO(
            "Title-3",
            "Description-3",
            "Location-3",
            0.0,
            0.0
        )

        // When the reminders is saved
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // Then the value in the list is the same that the created
        val remindersList = database.reminderDao().getReminders()

        assertThat(remindersList, not(nullValue()))
        assertThat(remindersList.size, `is`(3))
        assertThat(remindersList[0], `is`(reminder))
        assertThat(remindersList[1], `is`(reminder2))
        assertThat(remindersList[2], `is`(reminder3))
    }
}