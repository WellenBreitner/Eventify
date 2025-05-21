package eventify

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.eventify.Admin.AdminLoginPage
import com.example.eventify.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginTesting {

    @Before
    fun setup() {
        ActivityScenario.launch(AdminLoginPage::class.java)
    }

    @Test
    fun testLoginEmptyEmail() {
        Espresso.onView(ViewMatchers.withId(R.id.login_password))
            .perform(ViewActions.typeText("somepassword"))

        Espresso.onView(ViewMatchers.withId(R.id.adminLoginButton))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.login_email))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Email cannot be empty")))
    }

    @Test
    fun testLoginEmptyPassword() {
        Espresso.onView(ViewMatchers.withId(R.id.login_email))
            .perform(ViewActions.typeText("admin@example.com"))

        Espresso.onView(ViewMatchers.withId(R.id.adminLoginButton))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.login_password))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Password cannot be empty")))
    }

    @Test
    fun testValidLogin() {
        Espresso.onView(ViewMatchers.withId(R.id.login_email))
            .perform(ViewActions.typeText("admin123@gmail.com"))

        Espresso.onView(ViewMatchers.withId(R.id.login_password))
            .perform(ViewActions.typeText("admin123"))

        Espresso.onView(ViewMatchers.withId(R.id.adminLoginButton))
            .perform(ViewActions.click())

        Thread.sleep(5000)
    }

    @Test
    fun testMoveToSignUp() {
        Espresso.onView(ViewMatchers.withId(R.id.adminMoveToSignUpButton))
            .perform(ViewActions.click())

        Thread.sleep(3000)

        Espresso.onView(ViewMatchers.withId(R.id.signup_username))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @After
    fun teardown() {
    }
}
