package com.talhaatif.tickojet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.talhaatif.tickojet.databinding.ActivityLoginBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.AuthRepository
import com.talhaatif.tickojet.utils.LoadingDialog
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.AuthViewModel
import com.talhaatif.tickojet.viewmodel.factory.AuthViewModelFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager
    private val authRepository = AuthRepository()

    private val loadingDialog by lazy { LoadingDialog(this) }  // Initialize Custom Loader

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(authRepository, tokenManager, applicationContext)
    }

    // Helper function to navigate to MainActivity
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)


        setupUi()




    }
    private fun setupUi(){

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge display
        enableEdgeToEdge()



        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up signup link click listener
        binding.signupLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish() // Close LoginActivity
        }

        // Set up login button click listener
        binding.signInButton.setOnClickListener {
            val userName = binding.username.text.toString()
            val password = binding.password.text.toString()
            authViewModel.login(userName, password)
        }

        // Observe login state
        authViewModel.loginState.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show loading spinner
                    loadingDialog.show()
                }
                is Result.Success -> {
                    // Hide loading spinner
                    loadingDialog.dismiss()

                    // Navigate to MainActivity on successful login
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close LoginActivity
                }
                is Result.Error -> {
                    // Hide loading spinner
                    loadingDialog.dismiss()

                    // Show error message
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}