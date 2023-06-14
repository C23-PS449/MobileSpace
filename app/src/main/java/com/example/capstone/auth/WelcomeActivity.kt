package com.example.capstone.auth

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.capstone.R
import com.example.capstone.databinding.ActivityWelcomeBinding
import com.example.capstone.ui.fragment.home.HomeFragment
import com.example.capstone.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        auth = Firebase.auth
        val user=auth.currentUser
        if (user !=null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() }
        supportActionBar?.hide()
        setContentView(binding.root)
        val calendar= Calendar.getInstance()
        val hour=calendar.get(Calendar.HOUR_OF_DAY)
        if(hour < 18){
            binding.root.setBackgroundResource(R.drawable.bg_welcome)
        }else{
            binding.root.setBackgroundResource(R.drawable.bg_welcome_night)
        }

        setupAction()
        setupView()
        playAnimation()
        checkLocation()
    }

    private fun checkLocation(){
        if (isLocationEnabled()){
            getMyLastLocation()
        }else{
            enableLocationSettings()
        }
    }

    private fun isLocationEnabled():Boolean{
        val locationManager=getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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

    private fun enableLocationSettings(){
        val alertDialogBuilder= AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.membutuhkan_lokasi))
        alertDialogBuilder.setMessage(getString(R.string.untuk_menggunakan_aplikasi_silahkan_aktifkan_layanan_lokasi))
        alertDialogBuilder.setPositiveButton(getString(R.string.aktifkan_lokasi)){ dialog, which ->
            val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            dialog.dismiss()
        }
        alertDialogBuilder.show()
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
                    Snackbar.make(binding.root,"Agar aplikasi dapat berjalan dengan baik, mohon berikan akses untuk aplikasi",Snackbar.LENGTH_SHORT).show()
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
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)&&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,CancellationTokenSource().token).addOnSuccessListener { location:Location? ->
                HomeFragment.LOCATION_DATA="${location?.latitude},${location?.longitude}"

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

    override fun onRestart() {
        super.onRestart()
        checkLocation()
    }

}