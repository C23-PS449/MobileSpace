package com.example.capstone.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.capstone.auth.SignInActivity
import com.example.capstone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = Firebase.auth

        playAnimation()

        binding.btnSignUp.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (checkAllField()) {
                showLoading(true)
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    showLoading(false)
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.updateProfile(
                            UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build())
                            ?.addOnCompleteListener { updateProfileTask ->
                                if (updateProfileTask.isSuccessful) {
                                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                    val intentLogin = Intent(this, SignInActivity::class.java)
                                    startActivity(intentLogin)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Log.e("Error: ", task.exception.toString())
                        Toast.makeText(this, "Failed to create account.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        return when {
            binding.etUsername.text.toString().isEmpty() -> {
                binding.textInputLayoutUsername.error = "This is a required field"
                false
            }
            binding.etEmail.text.toString().isEmpty() -> {
                binding.textInputLayoutEmail.error = "This is a required field"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.textInputLayoutEmail.error = "Invalid email format"
                false
            }
            binding.etPassword.text.toString().isEmpty() -> {
                binding.textInputLayoutPassword.error = "This is a required field"
                binding.textInputLayoutPassword.errorIconDrawable = null
                false
            }
            binding.etPassword.length() < 8 -> {
                binding.textInputLayoutPassword.error = "Password should be at least 8 characters long"
                binding.textInputLayoutPassword.errorIconDrawable = null
                false
            }
            binding.etRePassword.text.toString().isEmpty() -> {
                binding.textInputLayoutRePassword.error = "This is a required field"
                binding.textInputLayoutRePassword.errorIconDrawable = null
                false
            }
            binding.etPassword.text.toString() != binding.etRePassword.text.toString() -> {
                binding.textInputLayoutPassword.error = "Passwords do not match"
                false
            }
            else -> true
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(500)
        val inputUser = ObjectAnimator.ofFloat(binding.textInputLayoutUsername, View.ALPHA, 1f).setDuration(500)
        val inputEmail = ObjectAnimator.ofFloat(binding.textInputLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val inputPassword = ObjectAnimator.ofFloat(binding.textInputLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val rePassword = ObjectAnimator.ofFloat(binding.textInputLayoutRePassword, View.ALPHA, 1f).setDuration(500)
        val buttonSignUp = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, inputUser, inputEmail, inputPassword, rePassword, buttonSignUp)
            start()
        }
    }

    private fun showLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
