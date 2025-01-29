package com.skuuzie.xpass.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.skuuzie.xpass.R
import com.skuuzie.xpass.data.local.database.Credential
import com.skuuzie.xpass.databinding.ActivityHomeBinding
import com.skuuzie.xpass.ui.credential.CredentialActivity
import com.skuuzie.xpass.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import godroidguard.Godroidguard
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupRecyclerView()
        setupClickListener()
        setupGetCredentials()
    }

    override fun onResume() {
        super.onResume()

        if (Godroidguard.isInitialization()) {
            finish()
            Intent(this, LoginActivity::class.java).run {
                startActivity(this)
            }
        }
    }

    private fun setupGetCredentials() {
        lifecycleScope.launch {
            viewModel.getCredentials().collect {
                when (it) {
                    HomeUiState.Loading -> {}
                    is HomeUiState.Error -> {
                        showToast(it.message)
                    }

                    is HomeUiState.Success -> {
                        if (it.data.isEmpty()) {
                            animateCrossFade(binding.layoutEmptyMessage, binding.rvCredentials)
                        } else {
                            adapter.submitList(it.data)
                            binding.rvCredentials.adapter = adapter
                            animateCrossFade(binding.rvCredentials, binding.layoutEmptyMessage)
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListener() {
        binding.btnAddNewCredential.setOnClickListener {
            viewModel.addNewCredential().observe(this) {
                when (it) {
                    HomeUiState.Loading -> {}
                    is HomeUiState.Error -> {
                        showToast(it.message)
                    }

                    is HomeUiState.Success -> {
                        Intent(this@HomeActivity, CredentialActivity::class.java).apply {
                            putExtra(CredentialActivity.EXTRA_CREDENTIAL, it.data.first())
                        }.run {
                            startActivity(this)
                        }
                    }
                }
            }
        }

        adapter.setOnItemClickListener(
            object : HomeAdapter.OnItemClickListener {
                override fun onItemClick(credential: Credential) {
                    Intent(this@HomeActivity, CredentialActivity::class.java).apply {
                        putExtra(CredentialActivity.EXTRA_CREDENTIAL, credential)
                    }.run {
                        startActivity(this)
                    }
                }
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupRecyclerView() {
        adapter = HomeAdapter()

        with(binding) {
            rvCredentials.layoutManager = LinearLayoutManager(this@HomeActivity)
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun animateCrossFade(viewIn: View, viewOut: View) {
        viewIn.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration((500).toLong())
                .setListener(null)
        }

        viewOut.animate()
            .alpha(0f)
            .setDuration((500).toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    viewOut.visibility = View.GONE
                }
            })
    }
}