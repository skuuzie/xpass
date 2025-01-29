package com.skuuzie.xpass.util

import androidx.test.espresso.IdlingResource
import com.skuuzie.xpass.ui.login.LoginUiState
import com.skuuzie.xpass.ui.login.LoginViewModel
import com.skuuzie.xpass.ui.register.RegisterUiState
import com.skuuzie.xpass.ui.register.RegisterViewModel

object IdlingResources {
    class RegisterIdlingResource(private val viewModel: RegisterViewModel) : IdlingResource {
        @Volatile
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName(): String = javaClass.name

        override fun isIdleNow(): Boolean {
            val isIdle = viewModel.registerState.value != RegisterUiState.Loading

            if (isIdle) {
                callback?.onTransitionToIdle()
            }

            return isIdle
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }
    }

    class LoginIdlingResource(private val viewModel: LoginViewModel) : IdlingResource {
        @Volatile
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName(): String = javaClass.name

        override fun isIdleNow(): Boolean {
            val isIdle = viewModel.loginState.value != LoginUiState.Loading

            if (isIdle) {
                callback?.onTransitionToIdle()
            }

            return isIdle
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }
    }
}