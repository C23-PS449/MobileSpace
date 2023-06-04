package com.example.capstone.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.capstone.databinding.ActivitySignInBinding
import com.example.capstone.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //use to remove / hide action bar
        supportActionBar?.hide()

        auth = Firebase.auth

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            // if true
            if (checkAllField()) {
                showLoading(true)
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    showLoading(false)
                    if (task.isSuccessful) {
                        // if sign in is successful
                        Toast.makeText(this, "Successfully sign in", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.putExtra("fragment","home")
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in is not successful
                        when (task.exception) {
                            is FirebaseAuthInvalidUserException -> {
                                // Email is not registered or the account is not valid
                                Toast.makeText(this, "Email is not registered or the account is not valid", Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                // incorrect Password
                                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                // General error
                                Toast.makeText(this, "Failed to sign in. Please try again.", Toast.LENGTH_SHORT).show()
                                Log.e("Error : ", task.exception.toString())
                            }
                        }
                    }
                }

            }
        }
        binding.tvCreateAccount.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvForgotPassword.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkAllField() : Boolean {
        val email = binding.etEmail.text.toString()
        if (binding.etEmail.text.toString() == "") {
            binding.textInputLayoutEmail.error = "This is required field"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "Check email format"
            return false
        }
        if (binding.etPassword.text.toString() == "") {
            binding.textInputLayoutPassword.error = "This is required field"
            binding.textInputLayoutPassword.errorIconDrawable = null
            return false
        }
        if (binding.etPassword.length() <= 6) {
            binding.textInputLayoutPassword.error = "Password should at least 8 characters long"
            binding.textInputLayoutPassword.errorIconDrawable = null
            return false
        }
        return true
    }

    private fun showLoading(loading: Boolean){
        if(loading) {
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}