package com.skuuzie.xpass.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.skuuzie.xpass.R
import com.skuuzie.xpass.databinding.ActivityRegisterBinding
import com.skuuzie.xpass.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupClickListener()
        setupEditTextListener()

        binding.btnGo.isVisible = false

        viewModel.registerState.observe(this) {
            when (it) {
                RegisterUiState.Error -> {}
                RegisterUiState.Loading -> {}
                RegisterUiState.Success -> moveToHome()
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
            register(password)
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

    private fun register(password: String) {
        viewModel.registerUser("root", password)
    }
}