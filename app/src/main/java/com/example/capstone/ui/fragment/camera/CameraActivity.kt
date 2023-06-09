package com.example.capstone.ui.fragment.camera

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.capstone.R
import com.example.capstone.databinding.ActivityCameraBinding
import com.example.capstone.ui.fragment.guide.GuideFragment
import com.example.capstone.ui.main.MainActivity

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture:ImageCapture?=null
    private var cameraSelector:CameraSelector=CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.captureImage.setOnClickListener { takePhoto() }
        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == (CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()

        }
    }
    public override fun onResume(){
        super.onResume()
        hideSystemUI()
        startCamera()
    }
    private fun startCamera(){
        val cameraProviderFuture= ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider =cameraProviderFuture.get()
            val preview= Preview.Builder().build().also{
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture= ImageCapture.Builder().build()
            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,cameraSelector,preview,imageCapture
                )
            }catch (exc:Exception){
                Toast.makeText(this,"Gagal memuat camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        val imageCapture=imageCapture?:return
        val photoFile=com.example.capstone.utils.createFile(application)
        val outputOptions= ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent= Intent()
                    intent.putExtra("picture",photoFile)
                    intent.putExtra(
                        "isBackCamera", cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(GuideFragment.CAMERA_X_RESULT,intent)
                    finish()

                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,"Gagal Memuat Camera", Toast.LENGTH_SHORT
                    ).show()
                }

            })

    }

    private fun hideSystemUI(){
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


}