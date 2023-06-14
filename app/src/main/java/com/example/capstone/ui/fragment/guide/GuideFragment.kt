package com.example.capstone.ui.fragment.guide

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.capstone.R
import com.example.capstone.data.Result
import com.example.capstone.databinding.FragmentGuideBinding
import com.example.capstone.ui.fragment.camera.CameraActivity
import com.example.capstone.utils.rotateFile
import com.example.capstone.utils.uriToFile
import com.example.capstone.viewmodel.DiseaseViewModel
import com.example.capstone.viewmodel.DiseaseViewModelFactory
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class GuideFragment : Fragment() {
    private var _binding:FragmentGuideBinding?=null
    private val binding get()=_binding!!
    private var getFile:File?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (getFile != null) getFile=null
        _binding= FragmentGuideBinding.inflate(inflater,container,false)
        binding.progressBar.visibility=View.GONE
        binding.ambilKamera.setOnClickListener { startCamera() }
        binding.ambilGaleri.setOnClickListener { startGallery() }
        binding.uploadGambar.setOnClickListener { upload() }

        return binding.root
    }

    private fun upload() {
        val diseasefactory: DiseaseViewModelFactory =
            DiseaseViewModelFactory.getInstance(requireContext())
        val diseaseviewmodel: DiseaseViewModel by viewModels {
            diseasefactory
        }

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestImageFile
            )
            diseaseviewmodel.getDisease(imageMultipart).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            Snackbar.make(
                                binding.root,
                                getString(R.string.model_loading),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            parentFragmentManager.beginTransaction().apply {
                                replace(
                                    R.id.containerFragment,
                                    HasilDeteksiFragment(),
                                    HasilDeteksiFragment::class.java.simpleName
                                )
                                HasilDeteksiFragment.HASIL_DETEKSI = result.data.prediction
                                addToBackStack(null)
                                    .commit()
                            }
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(
                                binding.root,
                                getString(R.string.something_wrong),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(context,"Silahkan Masukkan Berkas Gambar Terlebih Dahulu!",Toast.LENGTH_SHORT).show()
        }
    }



    private fun startCamera(){
        val intent= Intent(context, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }
    private fun startGallery(){
        val intent=Intent()
        intent.action=Intent.ACTION_GET_CONTENT
        intent.type="image/*"
        val chooser=Intent.createChooser(intent, "Pilih sebuah gambar")
        launcherIntentGallery.launch(chooser)
    }


    private val launcherIntentCameraX=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode==CAMERA_X_RESULT){
            val myFile=if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.TIRAMISU){
                it.data?.getSerializableExtra("picture",File::class.java)

            }else{
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            }as?File
            val isBackCamera=it.data?.getBooleanExtra("isBackCamera",true) as Boolean

            myFile?.let{
                file->
                rotateFile(file,isBackCamera)
                getFile=file
                binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(file.path))

            }
        }
    }
    private val launcherIntentGallery=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode==AppCompatActivity.RESULT_OK){
            val selectedImg=it.data?.data as Uri
            selectedImg.let{uri ->
                val myFile=uriToFile(uri,requireContext())
                getFile=myFile
                binding.imagePreview.setImageURI(uri)
            }
        }
    }


    private fun reduceFileImage(file:File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality=100
        var streamLength:Int
        do{
            val bmpStream= ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,compressQuality, bmpStream)
            val bmpPictByteArray=bmpStream.toByteArray()
            streamLength=bmpPictByteArray.size
            compressQuality -= 5
        }while(streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG,compressQuality,FileOutputStream(file))
        return file
    }

    companion object{
        const val CAMERA_X_RESULT=200
        private val REQUIRED_PERMISSION=arrayOf(
            Manifest.permission.CAMERA,
        )
        private const val REQUEST_CODE_PERMISSION =10
    }

}