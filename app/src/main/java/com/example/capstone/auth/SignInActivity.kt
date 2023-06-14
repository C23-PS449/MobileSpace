package com.example.capstone.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.R
import com.example.capstone.databinding.ActivitySignInBinding
import com.example.capstone.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
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
        supportActionBar?.hide()

        auth = Firebase.auth

        setupAction()
        playAnimation()

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
                        Snackbar.make(binding.root,getString(R.string.berhasil_masuk), Snackbar.LENGTH_SHORT).show()
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.putExtra("fragment","home")
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in is not successful
                        when (task.exception) {
                            is FirebaseAuthInvalidUserException -> {
                                // Email is not registered or the account is not valid
                                Snackbar.make(binding.root,getString(R.string.email_yang_digunakan_tidak_valid),Snackbar.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                // incorrect Password
                                Snackbar.make(binding.root,getString(R.string.password_yang_anda_masukkan_salah),Snackbar.LENGTH_SHORT).show()
                            }
                            else -> {
                                // General error
                                Toast.makeText(this, "Failed to sign in. Please try again.", Toast.LENGTH_SHORT).show()
                                Snackbar.make(binding.root,getString(R.string.terdapat_kesalahan_saat_akses_server_mohon_coba_kembali),Snackbar.LENGTH_SHORT).show()
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

    private fun setupAction() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(500)
        val inputEmail = ObjectAnimator.ofFloat(binding.textInputLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val inputPassword = ObjectAnimator.ofFloat(binding.textInputLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val forgotPass = ObjectAnimator.ofFloat(binding.tvForgotPassword, View.ALPHA, 1f).setDuration(500)
        val createAccount = ObjectAnimator.ofFloat(binding.tvCreateAccount, View.ALPHA, 1f).setDuration(500)
        val btnSignIn = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, inputEmail, inputPassword, forgotPass, btnSignIn, createAccount)
            start()
        }

    }

    private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        return when {
            binding.etEmail.text.toString().isEmpty() -> {
                binding.textInputLayoutEmail.error = getString(R.string.kolom_ini_harus_diisi)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.textInputLayoutEmail.error = getString(R.string.email_yang_digunakan_tidak_valid)
                false
            }
            binding.etPassword.text.toString().isEmpty() -> {
                binding.textInputLayoutPassword.error = getString(R.string.kolom_ini_harus_diisi)
                binding.textInputLayoutPassword.errorIconDrawable = null
                false
            }
            binding.etPassword.length() <= 6 -> {
                binding.textInputLayoutPassword.error = getString(R.string.jumlah_karakter_password_harus_lebih_dari_8_karakter)
                binding.textInputLayoutPassword.errorIconDrawable = null
                false
            }
            else -> true
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