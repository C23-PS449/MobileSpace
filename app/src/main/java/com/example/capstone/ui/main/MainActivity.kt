package com.example.capstone.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.capstone.R
import com.example.capstone.auth.WelcomeActivity
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.menu.AboutActivity
import com.example.capstone.menu.ProfileFragment
import com.example.capstone.ui.fragment.camera.CameraFragment
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
    private lateinit var cameraFragment: CameraFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (auth.currentUser == null) {
            Intent(this, WelcomeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        // Inisialisasi fragment
        homeFragment = HomeFragment()
        guideFragment = GuideFragment()
        productFragment = ProdukFragment()
        forumsFragment = ForumFragment()
        cameraFragment = CameraFragment()

        // Tampilkan fragment Beranda saat pertama kali dibuka
        switchFragment(homeFragment)

        // Aksi klik pada BottomNavigationView
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.iBeranda -> switchFragment(homeFragment)
                R.id.iPanduan -> switchFragment(guideFragment)
                R.id.iLocation -> switchFragment(productFragment)
                R.id.iForum -> switchFragment(forumsFragment)
            }
            true
        }
        binding.fab.setOnClickListener {
            switchFragment(cameraFragment)
        }


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
        when (item.itemId) {
            R.id.profile -> {
                Intent(this, ProfileFragment::class.java).also {
                    startActivity(it)
                    Toast.makeText(this,"Profile", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.about -> {
                Intent(this, AboutActivity::class.java).also {
                    startActivity(it)
                    Toast.makeText(this,"About", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.signOut -> {
                auth.signOut()
                Intent(this, WelcomeActivity::class.java).also {
                    startActivity(it)
                    Toast.makeText(this,"Log Out Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
