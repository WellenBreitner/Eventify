package com.example.eventify

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.core.app.ActivityScenario
import com.example.eventify.eventOrganizer.EventEditPage
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventEditPageTest {

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun testImagePickerIntentLaunched() {
        ActivityScenario.launch(EventEditPage::class.java).use {
            val resultData = Intent()
            val testImageUri = Uri.parse("content://test/image.jpg")
            resultData.data = testImageUri
            val result = ActivityResult(Activity.RESULT_OK, resultData)

            Intents.intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

            onView(withId(R.id.editEventPosterSelector)).perform(click())

            Intents.intended(allOf(
                hasAction(Intent.ACTION_GET_CONTENT),
                hasType("image/*")
            ))
        }
    }

    @Test
    fun testCancelButtonFinishesActivity() {
        ActivityScenario.launch(EventEditPage::class.java).use {
            onView(withId(R.id.cancelEditEventButton)).perform(click())

            assert(it.state.isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED))
        }
    }
}
