package com.talhaatif.tickojet


import com.talhaatif.tickojet.requestModel.UpdateRequest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.talhaatif.tickojet.databinding.ActivityUpdateProfileBinding
import com.talhaatif.tickojet.local.TokenManager
import com.talhaatif.tickojet.repository.ProfileRepository
import com.talhaatif.tickojet.utils.Result
import com.talhaatif.tickojet.viewmodel.ProfileViewModel
import com.talhaatif.tickojet.viewmodel.factory.ProfileViewModelFactory
import com.google.android.material.snackbar.Snackbar

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            ProfileRepository(TokenManager(this)),
            TokenManager(this),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeUserData()
        viewModel.getUserDetails()
    }

    private fun setupViews() {
        binding.saveButton.setOnClickListener {
            updateProfile()
        }

//        binding.backButton.setOnClickListener {
//            finish()
//        }
    }

    private fun observeUserData() {
        viewModel.userDetails.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    binding.nameEditText.setText(user.userName ?: "")
                    binding.emailEditText.setText(user.email ?: "")
                    binding.phoneEditText.setText(user.phoneNumber ?: "")
                }
                is Result.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
                Result.Loading -> {
                    // Show loading if needed
                }
            }
        }

        viewModel.profileUpdateResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Snackbar.make(binding.root, "Profile updated successfully", Snackbar.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
                Result.Loading -> {
                    // Show loading if needed
                }
            }
        }
    }

    private fun updateProfile() {
        val userName = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()

        if (userName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_LONG).show()
            return
        }

        val updateRequest = UpdateRequest(
            userName = userName,
            email = email,
            phoneNumber = phone
        )

        viewModel.updateProfile(updateRequest)
    }
}