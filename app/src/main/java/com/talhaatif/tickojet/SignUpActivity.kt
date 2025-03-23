package com.talhaatif.tickojet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.talhaatif.tickojet.databinding.ActivitySignUpBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.AuthRepository
import com.talhaatif.tickojet.utils.LoadingDialog
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.AuthViewModel
import com.talhaatif.tickojet.viewmodel.factory.AuthViewModelFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var tokenManager: TokenManager
    private val authRepository = AuthRepository()

    private val loadingDialog by lazy { LoadingDialog(this) }

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(authRepository, tokenManager, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Initialize TokenManager
        tokenManager = TokenManager(this)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up signup button click listener
        binding.signUpButton.setOnClickListener {
            val userName = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            authViewModel.signup(userName, email, password, confirmPassword)
        }

        // Observe signup state
        authViewModel.signupState.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show loading spinner
                    loadingDialog.show()
                }
                is Result.Success -> {
                    // Hide loading spinner
                    loadingDialog.show()

                    // Navigate to LoginActivity on successful signup
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Close SignUpActivity
                }
                is Result.Error -> {
                    // Hide loading spinner
                    loadingDialog.show()

                    // Show error message
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set up login link click listener
        binding.loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close SignUpActivity
        }
    }
}