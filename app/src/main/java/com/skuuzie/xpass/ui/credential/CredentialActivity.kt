package com.skuuzie.xpass.ui.credential

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skuuzie.xpass.R
import com.skuuzie.xpass.data.local.database.Credential
import com.skuuzie.xpass.databinding.ActivityCredentialBinding
import com.skuuzie.xpass.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import godroidguard.Godroidguard

@AndroidEntryPoint
class CredentialActivity : AppCompatActivity() {

    private val viewModel: CredentialViewModel by viewModels()
    private lateinit var binding: ActivityCredentialBinding

    private var credential: Credential? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityCredentialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // `credential` should never be null by normal usage
        credential = intent.getParcelableExtra(EXTRA_CREDENTIAL)

        setupEdgeToEdge()
        setupAppBarMenu()
        setupEditText()
        setupClickListener()
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

    private fun updateCredential() {
        val newCredential = Credential(
            uuid = credential?.uuid.toString(),
            platform = binding.textInputPlatform.editText?.text.toString(),
            username = binding.textInputUsername.editText?.text.toString(),
            email = binding.textInputEmail.editText?.text.toString(),
            password = binding.textInputPassword.editText?.text.toString()
        )
        viewModel.editCredential(newCredential).observe(this) {
            when (it) {
                CredentialUiState.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.topToolbar.setNavigationOnClickListener(null)
                }

                CredentialUiState.Success -> {
                    binding.btnSave.isEnabled = true
                    binding.topToolbar.setNavigationOnClickListener {
                        super.onBackPressedDispatcher.onBackPressed()
                    }
                    showToast("Credential updated.")
                }

                is CredentialUiState.Error -> {
                    showToast(it.message)
                }
            }
        }
    }

    private fun ensureDelete() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Credential")
            .setMessage("Remember that deleted credentials can't be restored in any way.")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteCredentialById(credential!!.uuid).observe(this) {
                    when (it) {
                        CredentialUiState.Loading -> {}
                        CredentialUiState.Success -> {
                            showToast("Credential deleted.")
                            finish()
                        }

                        is CredentialUiState.Error -> {
                            showToast(it.message)
                        }
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    private fun setupClickListener() {
        binding.btnSave.setOnClickListener {
            updateCredential()
        }
        binding.topToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupEditText() {
        (binding.textInputPlatform.editText as EditText).setText(credential?.platform)
        (binding.textInputEmail.editText as EditText).setText(credential?.email)
        (binding.textInputUsername.editText as EditText).setText(credential?.username)
        (binding.textInputPassword.editText as EditText).setText(credential?.password)
    }

    private fun setupAppBarMenu() {
        binding.topToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.credential_menu_delete -> {
                    ensureDelete()
                    true
                }

                else -> false
            }
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.credential_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_CREDENTIAL = "AA"
    }
}