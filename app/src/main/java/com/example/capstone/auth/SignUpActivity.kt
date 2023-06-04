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
import com.example.capstone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
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
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (checkAllField()) {
                showLoading(true)
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    showLoading(false)
                    if (it.isSuccessful) {
                        auth.signOut()
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        val intentLogin = Intent(this, SignInActivity::class.java)
                        startActivity(intentLogin)
                        finish()
                    }
                    else {
                        Log.e("Error : ", it.exception.toString())
                    }
                }
            }
        }
    }


    private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        return when {
            binding.etEmail.text.toString() == "" -> {
                binding.textInputLayoutEmail.error = "This is required field"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.textInputLayoutEmail.error = "Check email format"
                false
            }
            binding.etPassword.text.toString() == "" -> {
                binding.textInputLayoutPassword.error = "This is required field"
                binding.textInputLayoutPassword.errorIconDrawable = null
                false
            }
            binding.etPassword.length() <= 6 -> {
                binding.textInputLayoutPassword.error = "Password should be at least 8 characters long"
                binding.textInputLayoutPassword.errorIconDrawable = null
                false
            }
            binding.etRePassword.text.toString() == "" -> {
                binding.textInputLayoutRePassword.error = "This is required field"
                binding.textInputLayoutRePassword.errorIconDrawable = null
                false
            }
            binding.etPassword.text.toString() != binding.etRePassword.text.toString() -> {
                binding.textInputLayoutPassword.error = "Password do not match"
                false
            }
            else -> true
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(500)
        val inputEmail = ObjectAnimator.ofFloat(binding.textInputLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val inputPassword = ObjectAnimator.ofFloat(binding.textInputLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val rePassword = ObjectAnimator.ofFloat(binding.textInputLayoutRePassword, View.ALPHA, 1f).setDuration(500)
        val buttonSignUp = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, inputEmail, inputPassword, rePassword, buttonSignUp)
            start()
        }

    }

    private fun showLoading(loading: Boolean){
        if(loading) {
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}