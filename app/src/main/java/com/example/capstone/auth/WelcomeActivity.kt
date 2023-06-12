package com.example.capstone.auth

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.capstone.databinding.ActivityWelcomeBinding
import com.example.capstone.ui.fragment.home.HomeFragment
import com.example.capstone.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        supportActionBar?.hide()
        setupAction()
        setupView()
        playAnimation()
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()

        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun playAnimation() {
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, login, signup)
            start()
        }
    }

    private fun setupView() {
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private val requestPermissionLauncher=
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){
            when{
                it[Manifest.permission.ACCESS_FINE_LOCATION]?:false ->{
                    getMyLastLocation()
                }
                it[Manifest.permission.ACCESS_COARSE_LOCATION]?:false ->{
                    getMyLastLocation()
                }
                else ->{
                    Toast.makeText(this,"No Permssion Granted", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun checkPermission(permission:String):Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            permission
        )== PackageManager.PERMISSION_GRANTED
    }
    private fun getMyLastLocation(){
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)&&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient.lastLocation.addOnSuccessListener{location: Location ->
                HomeFragment.LOCATION_DATA="${location.latitude},${location.longitude}"

            }

        }else{
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }


    }
}