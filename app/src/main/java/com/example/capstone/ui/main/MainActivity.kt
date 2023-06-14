package com.example.capstone.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capstone.R
import com.example.capstone.auth.WelcomeActivity
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.menu.ProfileActivity
import com.example.capstone.ui.fragment.forums.ForumFragment
import com.example.capstone.ui.fragment.home.HomeFragment
import com.example.capstone.ui.fragment.guide.GuideFragment
import com.example.capstone.ui.fragment.product.ProdukFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var homeFragment: HomeFragment
    private lateinit var guideFragment: GuideFragment
    private lateinit var productFragment: ProdukFragment
    private lateinit var forumsFragment: ForumFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = Firebase.auth
        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }

        homeFragment = HomeFragment()
        guideFragment = GuideFragment()
        productFragment = ProdukFragment()
        forumsFragment = ForumFragment()

        switchFragment(homeFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.iBeranda -> switchFragment(homeFragment)
                R.id.iPanduan -> switchFragment(guideFragment)
                R.id.iLocation -> switchFragment(productFragment)
                R.id.iForum -> switchFragment(forumsFragment)
            }
            true
        }

        supportActionBar?.setTitle(getString(R.string.ricebuddy))
        binding.fab.setOnClickListener {
            switchFragment(guideFragment)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== REQUEST_CODE_PERMISSION){
            if(!allPermissionGranted()){
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionGranted()= REQUIRED_PERMISSION.all{
        ContextCompat.checkSelfPermission(this,it) == PackageManager.PERMISSION_GRANTED
    }


    private fun switchFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.profile -> {
                showLoading(true)
                Handler(Looper.getMainLooper()).postDelayed({
                    Intent(this, ProfileActivity::class.java).also {
                        startActivity(it)
                        Toast.makeText(this,"Profil", Toast.LENGTH_SHORT).show()
                        showLoading(false)
                    }
                }, 500)
            }
            R.id.signOut -> {
                showLoading(true)
                Handler(Looper.getMainLooper()).postDelayed({
                    auth.signOut()
                    Intent(this, WelcomeActivity::class.java).also {
                        startActivity(it)
                        Toast.makeText(this,"Berhasil Keluar", Toast.LENGTH_SHORT).show()
                        finish()
                        showLoading(false)
                    }
                }, 500)
            }
        }
        return super.onOptionsItemSelected(item)
    }



    companion object{
        private const val REQUEST_CODE_PERMISSION =10
        private val REQUIRED_PERMISSION=arrayOf(
            Manifest.permission.CAMERA,
        )
    }

    private fun showLoading(loading: Boolean){
        if(loading) {
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

}
