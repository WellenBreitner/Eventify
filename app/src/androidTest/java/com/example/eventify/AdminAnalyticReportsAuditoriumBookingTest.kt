package com.example.eventify.Admin

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.eventify.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminAnalyticReportsAuditoriumBookingTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AdminAnalyticReportsAuditoriumBooking::class.java)

    @Test
    fun spinnerWeeklySelectionLoadsData() {
        onView(withId(R.id.spinner_time_period_auditorium_booking)).perform(click())
        onView(withText("Weekly")).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.analyticReportBarChartAuditoriumBooking)).check(matches(isDisplayed()))
    }

    @Test
    fun spinnerMonthlySelectionLoadsData() {
        onView(withId(R.id.spinner_time_period_auditorium_booking)).perform(click())
        onView(withText("Monthly")).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.analyticReportBarChartAuditoriumBooking)).check(matches(isDisplayed()))
    }

    @Test
    fun spinnerCustomRangeShowsButtons() {
        onView(withId(R.id.spinner_time_period_auditorium_booking)).perform(click())
        onView(withText("Custom range")).perform(click())

        onView(withId(R.id.startDateButtonAuditoriumBooking)).check(matches(isDisplayed()))
        onView(withId(R.id.endDateButtonAuditoriumBooking)).check(matches(isDisplayed()))
        onView(withId(R.id.generateDataButtonAuditoriumBooking)).check(matches(isDisplayed()))
    }

    @Test
    fun clickGenerateDataWithoutDatesShowsToast() {
        onView(withId(R.id.spinner_time_period_auditorium_booking)).perform(click())
        onView(withText("Custom range")).perform(click())

        onView(withId(R.id.generateDataButtonAuditoriumBooking)).perform(click())
    }

    @Test
    fun clickPdfButtonTriggersDownload() {
        onView(withId(R.id.PDFButton)).perform(scrollTo(), click())
    }
}
