package com.skuuzie.xpass

import androidx.activity.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.skuuzie.xpass.ui.register.RegisterActivity
import com.skuuzie.xpass.ui.register.RegisterViewModel
import com.skuuzie.xpass.util.EspressoIdlingResource
import com.skuuzie.xpass.util.IdlingResources
import com.skuuzie.xpass.util.TestData
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Case:
 * Starts at register
 * 1. User add new credential -> ensure data updated on homepage and the credential page itself
 * 2. User add 2 credential then update 1 -> ensure data updated on homepage and the credential page itself
 * 2. User add 2 credential then delete 1 -> ensure homepage only have the undeleted one
 */
@RunWith(AndroidJUnit4::class)
class CredentialOperationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    private lateinit var registerViewModel: RegisterViewModel

    @Test
    fun addNewCredential_Then_Ensure_Update_On_Homepage() {

        // Register

        activityRule.scenario.onActivity {
            registerViewModel = it.viewModels<RegisterViewModel>().value
        }

        val idlingResource = IdlingResources.RegisterIdlingResource(registerViewModel)

        IdlingRegistry.getInstance().register(idlingResource)

        onView(withId(R.id.text_input_password))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ed_password))
            .perform(typeText(TestData.getTestLoginPassword()), closeSoftKeyboard())

        onView(withId(R.id.btn_go))
            .perform(click())

        IdlingRegistry.getInstance().unregister(idlingResource)

        /* --- */

        // Homepage

        onView(withId(R.id.layout_empty_message))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv_credentials))
            .check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.btn_add_new_credential))
            .perform(click())

        /* --- */

        // Add new credential

        onView(withId(R.id.credential_root))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ed_credential_platform))
            .perform(clearText(), typeText(TestData.getTestPlatform(1)), closeSoftKeyboard())
        onView(withId(R.id.ed_credential_email))
            .perform(clearText(), typeText(TestData.getTestEmail(1)), closeSoftKeyboard())
        onView(withId(R.id.ed_credential_username))
            .perform(clearText(), typeText(TestData.getTestUsername(1)), closeSoftKeyboard())
        onView(withId(R.id.ed_credential_password))
            .perform(clearText(), typeText(TestData.getTestPassword(1)), closeSoftKeyboard())
        onView(withId(R.id.btn_save))
            .perform(click())

        pressBack()

        /* --- */

        // Ensure it's updated

        onView(withId(R.id.layout_empty_message))
            .check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.rv_credentials))
            .check(matches(isDisplayed()))

        onView(withText(TestData.getTestPlatform(1)))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.credential_root))
            .check(matches(isDisplayed()))

        onView(withText(TestData.getTestPlatform(1)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addTwoCredential_Then_Update_One() {

        // Register

        activityRule.scenario.onActivity {
            registerViewModel = it.viewModels<RegisterViewModel>().value
        }

        val idlingResource = IdlingResources.RegisterIdlingResource(registerViewModel)

        IdlingRegistry.getInstance().register(idlingResource)

        onView(withId(R.id.text_input_password))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ed_password))
            .perform(typeText(TestData.getTestLoginPassword()), closeSoftKeyboard())

        onView(withId(R.id.btn_go))
            .perform(click())

        IdlingRegistry.getInstance().unregister(idlingResource)

        /* --- */

        // Homepage

        onView(withId(R.id.layout_empty_message))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv_credentials))
            .check(matches(Matchers.not(isDisplayed())))

        /* --- */

        // Add 2 new credential

        for (i in 1..2) {
            onView(withId(R.id.btn_add_new_credential))
                .perform(click())

            onView(withId(R.id.ed_credential_platform))
                .perform(clearText(), typeText(TestData.getTestPlatform(i)), closeSoftKeyboard())
            onView(withId(R.id.ed_credential_email))
                .perform(clearText(), typeText(TestData.getTestEmail(i)), closeSoftKeyboard())
            onView(withId(R.id.ed_credential_username))
                .perform(clearText(), typeText(TestData.getTestUsername(i)), closeSoftKeyboard())
            onView(withId(R.id.ed_credential_password))
                .perform(clearText(), typeText(TestData.getTestPassword(i)), closeSoftKeyboard())
            onView(withId(R.id.btn_save))
                .perform(click())

            pressBack()
        }

        /* --- */

        // Arrive at homepage, click existing credential

        onView(withId(R.id.layout_empty_message))
            .check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.rv_credentials))
            .check(matches(isDisplayed()))

        onView(withText(TestData.getTestPlatform(1)))
            .check(matches(isDisplayed()))
            .perform(click())

        /* --- */

        // Update/edit it

        onView(withId(R.id.ed_credential_platform))
            .perform(clearText(), typeText(TestData.getTestPlatform(7)), closeSoftKeyboard())
        onView(withId(R.id.btn_save))
            .perform(click())
        pressBack()

        /* --- */

        // Verify the change

        onView(withId(R.id.layout_empty_message))
            .check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.rv_credentials))
            .check(matches(isDisplayed()))

        onView(withText(TestData.getTestPlatform(7)))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.credential_root))
            .check(matches(isDisplayed()))

        onView(withText(TestData.getTestPlatform(7)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addTwoCredential_Then_Delete_One() {

        // Register

        activityRule.scenario.onActivity {
            registerViewModel = it.viewModels<RegisterViewModel>().value
        }

        val idlingResource = IdlingResources.RegisterIdlingResource(registerViewModel)

        IdlingRegistry.getInstance().register(idlingResource)

        onView(withId(R.id.text_input_password))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ed_password))
            .perform(typeText(TestData.getTestLoginPassword()), closeSoftKeyboard())

        onView(withId(R.id.btn_go))
            .perform(click())

        IdlingRegistry.getInstance().unregister(idlingResource)

        /* --- */

        // Homepage

        onView(withId(R.id.layout_empty_message))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv_credentials))
            .check(matches(Matchers.not(isDisplayed())))

        /* --- */

        // Add 2 new credential

        for (i in 1..2) {
            onView(withId(R.id.btn_add_new_credential))
                .perform(click())

            onView(withId(R.id.ed_credential_platform))
                .perform(clearText(), typeText(TestData.getTestPlatform(i)), closeSoftKeyboard())
            onView(withId(R.id.ed_credential_email))
                .perform(clearText(), typeText(TestData.getTestEmail(i)), closeSoftKeyboard())
            onView(withId(R.id.ed_credential_username))
                .perform(clearText(), typeText(TestData.getTestUsername(i)), closeSoftKeyboard())
            onView(withId(R.id.ed_credential_password))
                .perform(clearText(), typeText(TestData.getTestPassword(i)), closeSoftKeyboard())
            onView(withId(R.id.btn_save))
                .perform(click())

            pressBack()
        }

        /* --- */

        // Arrive at homepage, click existing credential

        onView(withId(R.id.layout_empty_message))
            .check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.rv_credentials))
            .check(matches(isDisplayed()))

        onView(withText(TestData.getTestPlatform(1)))
            .check(matches(isDisplayed()))
            .perform(click())

        /* --- */

        // Delete it

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText("Delete"))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withText("Delete"))
            .check(matches(isDisplayed()))
            .perform(click())

        /* --- */

        // Verify the change

        onView(withId(R.id.layout_empty_message))
            .check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.rv_credentials))
            .check(matches(isDisplayed()))

        onView(withText(TestData.getTestPlatform(1)))
            .check(doesNotExist())

        onView(withText(TestData.getTestPlatform(2)))
            .check(matches(isDisplayed()))
    }
}