package com.skuuzie.xpass.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.skuuzie.xpass.R
import com.skuuzie.xpass.ui.login.LoginActivity
import com.skuuzie.xpass.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {

    private val viewModel: LaunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        var initState: MainActivityInitState = MainActivityInitState.Loading

        lifecycleScope.launch {
            viewModel.initState.collect {
                initState = it
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (initState) {
                MainActivityInitState.Loading -> true
                is MainActivityInitState.Finished -> {
                    if ((initState as MainActivityInitState.Finished).isRegistered) {
                        Intent(this, LoginActivity::class.java).run {
                            startActivity(this)
                            finish()
                        }
                    } else {
                        Intent(this, RegisterActivity::class.java).run {
                            startActivity(this)
                            finish()
                        }
                    }
                    false
                }
            }
        }
    }
}