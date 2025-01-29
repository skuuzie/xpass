package com.skuuzie.xpass

import androidx.activity.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skuuzie.xpass.ui.register.RegisterActivity
import com.skuuzie.xpass.ui.register.RegisterViewModel
import com.skuuzie.xpass.util.IdlingResources
import com.skuuzie.xpass.util.TestData
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * User opens the app for the first time, register their password and arrive on homepage
 */
@RunWith(AndroidJUnit4::class)
class RegisterTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    private lateinit var registerViewModel: RegisterViewModel

    @Test
    fun successfulRegister_Then_Move_On_Homepage_With_Empty_Data() {
        activityRule.scenario.onActivity {
            registerViewModel = it.viewModels<RegisterViewModel>().value
        }

        val idlingResource = IdlingResources.RegisterIdlingResource(registerViewModel)

        IdlingRegistry.getInstance().register(idlingResource)

        // User input their password and click "next" button
        onView(withId(R.id.text_input_password))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ed_password))
            .perform(typeText(TestData.getTestLoginPassword()), closeSoftKeyboard())

        onView(withId(R.id.btn_go))
            .perform(click())
        //

        // Idling resource is registered and listening to register state

        // Check if the register is indeed completed and arrive at Homepage
        onView(withId(R.id.home_root))
            .check(matches(isDisplayed()))

        // Ensure database is empty by checking "empty message" display
        onView(withId(R.id.layout_empty_message))
            .check(matches(isDisplayed()))

        // Ensure recyclerview is not visible (meaning database is empty)
        onView(withId(R.id.rv_credentials))
            .check(matches(Matchers.not(isDisplayed())))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}