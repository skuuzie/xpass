package com.skuuzie.xpass.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.skuuzie.xpass.R
import com.skuuzie.xpass.databinding.ActivityLoginBinding
import com.skuuzie.xpass.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnGo.isVisible = false

        setupEdgeToEdge()
        setupClickListener()
        setupEditTextListener()

        viewModel.loginState.observe(this) {
            when (it) {
                LoginUiState.Error -> showErrorState()
                LoginUiState.Loading -> showLoadingState()
                LoginUiState.Success -> moveToHome()
            }
        }
    }

    private fun setupEditTextListener() {
        binding.textInputPassword.editText?.addTextChangedListener {
            if (it?.length == 0) {
                binding.btnGo.isVisible = false
            } else {
                password = it.toString()
                binding.btnGo.isVisible = true
            }
        }
    }

    private fun setupClickListener() {
        binding.btnGo.setOnClickListener {
            login(password)
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun moveToHome() {
        Intent(this, HomeActivity::class.java).run {
            startActivity(this)
            finish()
        }
    }

    private fun showErrorState() {
        binding.btnGo.isEnabled = true
        binding.tvIncorrectPassword.visibility = View.VISIBLE
    }

    private fun showLoadingState() {
        binding.btnGo.isEnabled = false
        binding.tvIncorrectPassword.visibility = View.GONE
    }

    private fun login(password: String) {
        viewModel.loadUserPassword(password)
    }
}