package com.example.eventify


import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.eventify.Admin.AdminAnalyticsReportsEventHosted
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminAnalyticsReportsEventHostedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AdminAnalyticsReportsEventHosted::class.java)

    @Test
    fun testSpinnerWeeklySelectionLoadsData() {

        onView(withId(R.id.spinner_time_period_event_hosted)).perform(click())

        onView(withText("Weekly")).perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.analyticReportBarChartEventHosted))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCustomRangeDatePickerButtonsExist() {
        onView(withId(R.id.spinner_time_period_event_hosted)).perform(click())

        onView(withText("Custom range")).perform(click())

        onView(withId(R.id.startDateButtonEventHosted)).check(matches(isDisplayed()))
        onView(withId(R.id.endDateButtonEventHosted)).check(matches(isDisplayed()))
        onView(withId(R.id.generateDataButtonEventHosted)).check(matches(isDisplayed()))
    }

    @Test
    fun testClickDownloadPdfButton() {
        onView(withId(R.id.PDFButton)).perform(scrollTo(), click())
    }
}
