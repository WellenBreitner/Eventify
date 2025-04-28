package com.example.eventify

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.eventify.eventOrganizer.EOAddEvent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddEventTesting {
    @Before
    fun setup() {
        ActivityScenario.launch(EOAddEvent::class.java)
    }

    @Test
    fun testActivityLaunch() {
        Espresso.onView(withId(R.id.addEventNameEditText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEventNameEmpty() {
        Espresso.onView(withId(R.id.addEventButton))
            .perform(click())

        Espresso.onView(withId(R.id.addEventNameEditText))
            .check(matches(hasErrorText("Event name can't be empty")))
    }

    @Test
    fun testEventDescEmpty() {
        Espresso.onView(withId(R.id.addEventNameEditText))
            .perform(typeText("Concert"), closeSoftKeyboard())

        Espresso.onView(withId(R.id.addEventButton))
            .perform(click())

        Espresso.onView(withId(R.id.addEventDescEditText))
            .check(matches(hasErrorText("Event description can't be empty")))
    }

    @Test
    fun testEventLocationEmpty() {
        Espresso.onView(withId(R.id.addEventNameEditText))
            .perform(typeText("Concert"), closeSoftKeyboard())
        Espresso.onView(withId(R.id.addEventDescEditText))
            .perform(typeText("A big concert event."), closeSoftKeyboard())

        Espresso.onView(withId(R.id.addEventButton))
            .perform(click())

        Espresso.onView(withId(R.id.addEventLocationEditText))
            .check(matches(hasErrorText("Event location can't be empty")))
    }

}
