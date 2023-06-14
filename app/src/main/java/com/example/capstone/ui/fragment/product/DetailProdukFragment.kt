package com.example.capstone.ui.fragment.product

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.capstone.BuildConfig
import com.example.capstone.R
import com.example.capstone.data.remote.response.ResultsItem
import com.example.capstone.databinding.FragmentDetailProdukBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.material.snackbar.Snackbar

class DetailProdukFragment : Fragment() {

    private var _binding:FragmentDetailProdukBinding?=null
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private val binding get() =_binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailProdukBinding.inflate(inflater,container,false)
        mapView=binding.locationMaps
        mapView.onCreate(savedInstanceState)
        val latitude=EXTRA_DATA!!.geometry.location.lat
        val longitude=EXTRA_DATA!!.geometry.location.lng
        binding.productTitle.text= EXTRA_DATA?.name
        binding.productPrice.text= EXTRA_DATA?.businessStatus
        binding.productLocation.text=EXTRA_DATA?.vicinity
        binding.productAvaibility.text=EXTRA_DATA?.rating.toString()

        Places.initialize(requireContext(), BuildConfig.Google_API_KEY)
        val placesClient= Places.createClient(requireContext())
        val field=listOf(Place.Field.PHOTO_METADATAS)
        val placeRequest= FetchPlaceRequest.newInstance(EXTRA_DATA!!.placeId,field)

        placesClient.fetchPlace(placeRequest).addOnSuccessListener { response: FetchPlaceResponse ->
            val place=response.place
            val metada = place.photoMetadatas
            if (metada == null || metada.isEmpty()){

                return@addOnSuccessListener
            }
            val photoMetadata=metada.first()
            val photoRequest= FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(250)
                .setMaxHeight(250)
                .build()
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener {
                    fetchPhotoResponse: FetchPhotoResponse ->
                val bitmap=fetchPhotoResponse.bitmap
                binding.detailProductImage.setImageBitmap(bitmap)
            }.addOnFailureListener{exception:Exception ->
                if(exception is ApiException){
                   Snackbar.make(binding.root,getString(R.string.failed_to_fetch_detail),
                       Snackbar.LENGTH_SHORT).show()
                }

            }

            mapView.getMapAsync{googleMap ->
                this.googleMap=googleMap
                googleMap.uiSettings.isZoomControlsEnabled=true
                googleMap.uiSettings.isIndoorLevelPickerEnabled=true
                val latlng=LatLng(latitude, longitude)
                googleMap.addMarker(MarkerOptions().position(latlng).title("Lokasi Toko"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,20f))
                googleMap.setOnMarkerClickListener {
                    val gmnIntentUri= Uri.parse("google.navigation:q=$latitude,$longitude")
                    val mapIntent=Intent(Intent.ACTION_VIEW,gmnIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                    true
                }
            }

           binding.locationMaps.setOnClickListener {

            }



        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    companion object{
        var EXTRA_DATA:ResultsItem?=null
    }



}