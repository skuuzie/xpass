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
    private lateinit var _intent: Intent
    private var initState: LaunchActivityInitState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        splashScreen.setKeepOnScreenCondition {
            when (initState) {
                null -> true
                LaunchActivityInitState.Loading -> true
                is LaunchActivityInitState.Finished -> false
            }
        }

        lifecycleScope.launch {
            viewModel.initializeDatastore().collect {
                initState = it
                when (initState) {
                    is LaunchActivityInitState.Finished -> {
                        if ((initState as LaunchActivityInitState.Finished).isRegistered) {
                            _intent = Intent(this@LaunchActivity, LoginActivity::class.java)
                        } else {
                            _intent = Intent(this@LaunchActivity, RegisterActivity::class.java)
                        }
                        startActivity(_intent)
                        finish()
                    }

                    LaunchActivityInitState.Loading -> {}
                    null -> {}
                }
            }
        }
    }
}