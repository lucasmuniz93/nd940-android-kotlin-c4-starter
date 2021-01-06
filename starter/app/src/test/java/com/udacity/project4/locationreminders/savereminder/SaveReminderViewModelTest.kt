package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.hamcrest.Matchers.`is`
import org.junit.*
import org.koin.core.context.stopKoin

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var context: Application
    private lateinit var reminder: ReminderDataItem
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    //TODO: provide testing to the SaveReminderView and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        fakeDataSource = FakeDataSource()
        context = ApplicationProvider.getApplicationContext()
        saveReminderViewModel = SaveReminderViewModel(context, fakeDataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun saveReminder_showToastSaved() {
        // Given a fresh ViewModel and a reminder
        reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "Location",
            latitude = 100.00,
            longitude = 50.00
        )

        // When adding a new reminder
        saveReminderViewModel.saveReminder(reminder)

        // Then the toast event is triggered
        val showToastValue = saveReminderViewModel.showToast.getOrAwaitValue()
        Assert.assertEquals(showToastValue, context.resources.getString(R.string.reminder_saved))
    }

    @Test
    fun saveReminder_navigationBackCommand() {
        // GIVE a fresh viewmodel and reminder
        reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "Location",
            latitude = 100.00,
            longitude = 50.00
        )

        // WHEN adding a new reminder
        saveReminderViewModel.saveReminder(reminder)
        val navigationCommand = saveReminderViewModel.navigationCommand.getOrAwaitValue()

        // Then the navigation back event is triggered
        Assert.assertEquals(navigationCommand, NavigationCommand.Back)
    }

    @Test
    fun validateEnteredData_ReturnsTrue() {
        // Given a fresh ViewModel and a reminder
        reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "Location",
            latitude = 100.00,
            longitude = 50.00
        )

        // When validate the reminder
        val value = saveReminderViewModel.validateEnteredData(reminder)

        // Then the return is true
        Assert.assertThat(value, `is`(true))
    }

    @Test
    fun validateEnteredData_EmptyTitleAndSnackbar() {
        // Given a fresh ViewModel and a reminder without a title
        reminder = ReminderDataItem(
            title = null,
            description = "Description",
            location = "Location",
            latitude = 100.00,
            longitude = 50.00
        )

        // When validate the reminder
        val value = saveReminderViewModel.validateEnteredData(reminder)
        val snackbarValue = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()

        // Then the return is true
        Assert.assertThat(value, `is`(false))
        Assert.assertEquals(
            context.getString(snackbarValue),
            context.resources.getString(R.string.err_enter_title)
        )
    }
}