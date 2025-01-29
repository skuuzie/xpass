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
import com.skuuzie.xpass.ui.login.LoginActivity
import com.skuuzie.xpass.ui.login.LoginViewModel
import com.skuuzie.xpass.ui.register.RegisterViewModel
import com.skuuzie.xpass.util.IdlingResources
import com.skuuzie.xpass.util.TestData
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Case:
 * 1. User login with correct password -> arrive on homepage
 * 2. User login with incorrect password -> error message shown, not moving to homepage
 */
@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var loginViewModel: LoginViewModel

    @Test
    fun successfulLogin_Then_Move_To_Homepage() {
        activityRule.scenario.onActivity {
            loginViewModel = it.viewModels<LoginViewModel>().value
            registerViewModel = it.viewModels<RegisterViewModel>().value
        }

        val loginIdlingResource = IdlingResources.LoginIdlingResource(loginViewModel)
        val registerIdlingResource = IdlingResources.RegisterIdlingResource(registerViewModel)

        IdlingRegistry.getInstance().register(loginIdlingResource, registerIdlingResource)

        registerViewModel.registerUser(
            TestData.getTestLoginUsername(),
            TestData.getTestLoginPassword()
        )

        // User input their password and click "Go" button
        onView(withId(R.id.text_input_password))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ed_password))
            .perform(typeText(TestData.getTestLoginPassword()), closeSoftKeyboard())

        onView(withId(R.id.btn_go))
            .perform(click())
        //

        // Check if the register is indeed completed and arrive at Homepage
        onView(withId(R.id.home_root))
            .check(matches(isDisplayed()))

        // Ensure database is empty by checking "empty message" display
        onView(withId(R.id.layout_empty_message))
            .check(matches(isDisplayed()))

        // Ensure recyclerview is not visible (meaning database is empty)
        onView(withId(R.id.rv_credentials))
            .check(matches(Matchers.not(isDisplayed())))

        IdlingRegistry.getInstance().unregister(loginIdlingResource, registerIdlingResource)
    }

    @Test
    fun failedLogin_Then_Display_Error_Message() {
        activityRule.scenario.onActivity {
            loginViewModel = it.viewModels<LoginViewModel>().value
            registerViewModel = it.viewModels<RegisterViewModel>().value
        }

        val loginIdlingResource = IdlingResources.LoginIdlingResource(loginViewModel)
        val registerIdlingResource = IdlingResources.RegisterIdlingResource(registerViewModel)

        IdlingRegistry.getInstance().register(loginIdlingResource, registerIdlingResource)

        // Register credential first
        registerViewModel.registerUser(
            TestData.getTestLoginUsername(),
            TestData.getTestLoginPassword()
        )

        // User input their password and click "Go" button
        onView(withId(R.id.text_input_password))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ed_password))
            .perform(typeText("Wrong Password"), closeSoftKeyboard())

        onView(withId(R.id.btn_go))
            .perform(click())

        // Ensure error message is shown
        onView(withId(R.id.tv_incorrect_password))
            .check(matches(isDisplayed()))

        // Ensure still on the login page
        onView(withId(R.id.text_input_password))
            .check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(loginIdlingResource, registerIdlingResource)
    }
}