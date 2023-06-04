package com.example.capstone.auth

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

        auth = Firebase.auth

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
        if (binding.etRePassword.text.toString() == "") {
            binding.textInputLayoutRePassword.error = "This is required field"
            binding.textInputLayoutRePassword.errorIconDrawable = null
            return false
        }
        if (binding.etPassword.text.toString() != binding.etRePassword.text.toString()) {
            binding.textInputLayoutPassword.error = "Password do not match"
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