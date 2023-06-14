package com.example.capstone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone.BuildConfig
import com.example.capstone.R
import com.example.capstone.data.remote.response.ResultsItem
import com.example.capstone.databinding.LinearProductRowBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse

class ProductsAdapter(private val productList:List<ResultsItem>):RecyclerView.Adapter<ProductsAdapter.ViewHolder>(){
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback:OnItemClickCallback){
        this.onItemClickCallback=onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val layoutInflater= LayoutInflater.from(parent.context)
        Log.e("Testing",productList.toString())
        val binding=LinearProductRowBinding.inflate(layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val(title,location,rating,place_id,open)=productList[position]
        holder.title.text=title
        holder.location.text=location
        holder.rating.text=rating.toString()
        holder.open.text=open

        Places.initialize(holder.itemView.context, BuildConfig.Google_API_KEY)
        val placesClient=Places.createClient(holder.itemView.context)
        val field=listOf(Place.Field.PHOTO_METADATAS)
        val placeRequest=FetchPlaceRequest.newInstance(place_id,field)

        placesClient.fetchPlace(placeRequest).addOnSuccessListener { response:FetchPlaceResponse->
            val place=response.place
            val metada = place.photoMetadatas
            if (metada == null || metada.isEmpty()){
                Glide.with(holder.itemView.context)
                    .load(R.drawable.ic_broken_image_black)
                    .into(holder.image)
                return@addOnSuccessListener
            }
            val photoMetadata=metada.first()
            val photoRequest=FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(250)
                .setMaxHeight(250)
                .build()
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener {
                fetchPhotoResponse:FetchPhotoResponse ->
                val bitmap=fetchPhotoResponse.bitmap
                holder.image.setImageBitmap(bitmap)
            }.addOnFailureListener{exception:Exception ->
                if(exception is ApiException){
                    Log.e("test","Place not found"+exception.message)
                }

            }
        }
        holder.itemView.setOnClickListener{onItemClickCallback.onItemClicked(productList[position])}
    }

    override fun getItemCount()=productList.size

    interface OnItemClickCallback{
        fun onItemClicked(data:ResultsItem)
    }
    class ViewHolder(binding:LinearProductRowBinding):RecyclerView.ViewHolder(binding.root){
        val title=binding.productTitle
        val location=binding.productLocation
        val rating=binding.productPrice
        val image=binding.imgProduct
        val open=binding.productAvaibility
    }

}