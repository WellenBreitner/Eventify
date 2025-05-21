package com.example.eventify.Admin

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.eventify.R
import org.junit.Before
import org.junit.Test

class SignUpTesting {

    @Before
    fun setup() {
        ActivityScenario.launch(AdminSignup::class.java)
    }

    @Test
    fun testActivityLaunches() {
        onView(withId(R.id.signup_username)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyUsernameField() {
        onView(withId(R.id.signup_email)).perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.signup_password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.signup_confirm)).perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.adminSignUpButton)).perform(click())

        onView(withId(R.id.signup_username))
            .check(matches(hasErrorText("Username cannot be empty")))
    }

    @Test
    fun testEmailValidation() {
        onView(withId(R.id.signup_username)).perform(typeText("testuser"), closeSoftKeyboard())
        onView(withId(R.id.signup_email)).perform(typeText("invalidemail"), closeSoftKeyboard())
        onView(withId(R.id.signup_password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.signup_confirm)).perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.adminSignUpButton)).perform(click())

        onView(withId(R.id.signup_email))
            .check(matches(hasErrorText("Please enter a valid email")))
    }

    @Test
    fun testPasswordMismatch() {
        onView(withId(R.id.signup_username)).perform(typeText("testuser"), closeSoftKeyboard())
        onView(withId(R.id.signup_email)).perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.signup_password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.signup_confirm)).perform(typeText("password456"), closeSoftKeyboard())

        onView(withId(R.id.adminSignUpButton)).perform(click())

        onView(withId(R.id.signup_confirm))
            .check(matches(hasErrorText("Passwords do not match")))
    }
    @Test
    fun testMoveToLoginPage() {
        onView(withId(R.id.adminMoveToLoginButton)).perform(click())


        onView(withId(R.id.adminLoginButton))
            .check(matches(isDisplayed()))
    }
}
