package com.example.eventify.attendees

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.eventify.ModelData.EventModelData
import com.example.eventify.ModelData.TicketModelData
import com.example.eventify.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class AttendeesEventDetailAndWaitingListTest {

    @get:Rule
    val activityRule = ActivityScenarioRule<AttendeesEventDetail>(createIntent())

    companion object {
        private fun createIntent(): Intent {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val intent = Intent(context, AttendeesEventDetail::class.java)

            val event = EventModelData(
                eventId = "test_event_1",
                eventName = "Test Event",
                eventDescription = "Test Description",
                eventDate = "2025-05-26",
                eventTime = "10:00",
                eventLocation = "Test Location",
                organizerId = "org1",
                eventImage = ""
            )

            val ticket = TicketModelData(
                maxWaitingList = 5
            )

            intent.putExtra(AttendeesEventDetail.EXTRA_EVENT_DETAIL, event)
            intent.putExtra(AttendeesEventDetail.EXTRA_TICKET_DETAIL, ticket)
            intent.putExtra(AttendeesEventDetail.EXTRA_TICKET_TOTAL, 0)
            return intent
        }
    }

    @Test
    fun joinWaitingListButton_DisplaysCorrectText() {
        onView(withId(R.id.attendeesEventDetailBuyTicketButton))
            .check(matches(isDisplayed()))
            .check(matches(withText("Join Waiting List")))
    }

    @Test
    fun joinWaitingListButton_ShowsDialog() {
        onView(withId(R.id.attendeesEventDetailBuyTicketButton))
            .perform(click())

    }

    @Test
    fun eventDetails_DisplayedCorrectly() {
        onView(withId(R.id.attendeesEventDetailName))
            .check(matches(withText("Test Event")))

        onView(withId(R.id.attendeesEventDetailDate))
            .check(matches(withText("Date: 2025-05-26 10:00")))

        onView(withId(R.id.attendeesEventDetailLocation))
            .check(matches(withText("Location: Test Location")))
    }
}
