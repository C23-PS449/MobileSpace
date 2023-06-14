package com.example.capstone.menu

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.R
import com.example.capstone.databinding.ActivityProfileBinding
import com.example.capstone.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var imageUri: Uri
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        user?.let {
            showLoading(true)
            if (it.photoUrl != null) {
                Picasso.get()
                    .load(it.photoUrl)
                    .into(binding.ivProfile, object : Callback {
                        override fun onSuccess() {
                            showLoading(false)
                        }

                        override fun onError(e: Exception?) {
                            showLoading(false)
                        }
                    })
            } else {
                Picasso.get()
                    .load("https://picsum.photos/200/300")
                    .into(binding.ivProfile, object : Callback {
                        override fun onSuccess() {
                            showLoading(false)
                        }

                        override fun onError(e: Exception?) {
                            showLoading(false)
                        }
                    })
            }
            with(binding) {
                etName.setText(it.displayName)
                etEmail.setText(it.email)

                if (it.isEmailVerified) {
                    idVerified.visibility = View.VISIBLE
                } else {
                    idUnverified.visibility = View.VISIBLE
                }

            }
        }

        binding.ivProfile.setOnClickListener {
            startCameraIntent()
        }

        binding.idUnverified.setOnClickListener {
            showLoading(true)
            user?.sendEmailVerification()?.addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Snackbar.make(binding.root,getString(R.string.success_verification),
                        Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root,getString(R.string.Something_happened),Snackbar.LENGTH_SHORT).show()
                }
            }
        }


        binding.btnUpdate.setOnClickListener {
            val image = if (::imageUri.isInitialized) imageUri
            else user?.photoUrl ?: Uri.parse("https://picsum.photos/200/300")

            val name = binding.etName.text.toString().trim()
            if (name.isEmpty()) {
                binding.etName.error = getString(R.string.kolom_ini_harus_diisi)
                binding.etName.requestFocus()
                return@setOnClickListener
            }

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build()

            user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(binding.root,getString(R.string.profil_berhasil_diubah),Snackbar.LENGTH_SHORT).show()
                    val intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(binding.root,getString(R.string.Something_happened),Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun startCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            packageManager?.let {
                intent.resolveActivity(it)?.also {
                    startActivityForResult(intent, REQ_CAM)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAM && resultCode == Activity.RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            uploadImage(imgBitmap)
        }
    }

    private fun uploadImage(imgBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")

        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        ref.putBytes(image).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        binding.ivProfile.setImageBitmap(imgBitmap)
                    }
                }
            }
        }
    }

    companion object {
        private const val REQ_CAM = 100
    }

    private fun showLoading(loading: Boolean){
        if(loading) {
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}

