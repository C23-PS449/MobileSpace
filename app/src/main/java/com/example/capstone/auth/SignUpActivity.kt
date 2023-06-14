package com.example.capstone.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.R
import com.example.capstone.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
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
                                    Snackbar.make(binding.root,getString(R.string.berhasil_membuat_akun_silahkan_login_menggunakan_akun_yang_telah_dibuat),Snackbar.LENGTH_SHORT).show()
                                    val intentLogin = Intent(this, SignInActivity::class.java)
                                    startActivity(intentLogin)
                                    finish()
                                } else {
                                    Snackbar.make(binding.root,getString(R.string.fail_account),Snackbar.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Snackbar.make(binding.root,getString(R.string.fail_account),Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        return when {
            binding.etUsername.text.toString().isEmpty() -> {
                binding.textInputLayoutUsername.error = getString(R.string.kolom_ini_harus_diisi)
                false
            }
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
            binding.etPassword.length() < 8 -> {
                binding.textInputLayoutPassword.error = getString(R.string.jumlah_karakter_password_harus_lebih_dari_8_karakter)
                binding.textInputLayoutPassword.errorIconDrawable = null
                false
            }
            binding.etRePassword.text.toString().isEmpty() -> {
                binding.textInputLayoutRePassword.error = getString(R.string.kolom_ini_harus_diisi)
                binding.textInputLayoutRePassword.errorIconDrawable = null
                false
            }
            binding.etPassword.text.toString() != binding.etRePassword.text.toString() -> {
                binding.textInputLayoutPassword.error = getString(R.string.password_yang_dimasukkan_tidak_sama)
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
