package com.example.eventify

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.eventify.attendees.AttendeesPurchaseTicket
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AttendeesPurchaseTicketTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(AttendeesPurchaseTicket::class.java)

    @Test
    fun testTicketTypeDialogOpens() {
        onView(withId(R.id.attendeesSelectTicketType)).perform(click())

        onView(withText("Select Ticket Type"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBookingButtonExistsAndClickable() {
        onView(withId(R.id.attendeesBookingButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }
}
