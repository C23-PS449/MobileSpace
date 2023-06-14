package com.example.capstone.ui.fragment.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.databinding.FragmentHasilDeteksiBinding
import com.example.capstone.ui.fragment.home.HomeFragment
import com.example.capstone.ui.fragment.product.ProdukFragment
import com.google.android.material.snackbar.Snackbar

class HasilDeteksiFragment : Fragment() {
    private var _binding:FragmentHasilDeteksiBinding?=null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentHasilDeteksiBinding.inflate(inflater,container,false)

        binding.BackToHome.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.containerFragment,
                    HomeFragment(),
                    HomeFragment::class.java.simpleName
                )
                     addToBackStack(null)
                    .commit()
            }
        }
        if (HASIL_DETEKSI.isBlank()){
            binding.refreshDetail.visibility=View.VISIBLE
            Snackbar.make(binding.root,getString(R.string.terdapat_kesalahan_saat_akses_server_mohon_coba_kembali),Snackbar.LENGTH_SHORT).show()
            binding.refreshDetail.setOnClickListener {
                hasildeteksi(HASIL_DETEKSI)
            }
        }else{
            binding.refreshDetail.visibility=View.GONE
            hasildeteksi(HASIL_DETEKSI)
        }

        return binding.root
    }

    private fun hasildeteksi(hasil:String){
        val diseaseResult:Array<String>? = when (hasil){
            "LeafBlast" -> resources.getStringArray(R.array.Leaf_Blast)
            "Hispa" -> resources.getStringArray(R.array.Hispa)
            "BrownSpot" -> resources.getStringArray(R.array.brown_spot)
            "Healthy" -> resources.getStringArray(R.array.Healthy)
            else -> null
        }

        Glide.with(requireContext())
            .load(diseaseResult!![0])
            .into(binding.imageHasilDeteksi)
        binding.namaPenyakit.text = diseaseResult[1]
        binding.namaAlternatifPenyakit.text=diseaseResult[2]
        binding.detailPenyakit.text=diseaseResult[3]
        binding.penyembuhanPenyakit.text=diseaseResult[4]

        binding.goToShop.setOnClickListener{
            parentFragmentManager.beginTransaction().apply{
                replace(
                    R.id.containerFragment,
                    ProdukFragment(),
                    ProdukFragment::class.java.simpleName
                )
                addToBackStack(null)
                    .commit()
            }
        }
    }
    companion object{
        var HASIL_DETEKSI = "HASIL_DETEKSI"
    }

}