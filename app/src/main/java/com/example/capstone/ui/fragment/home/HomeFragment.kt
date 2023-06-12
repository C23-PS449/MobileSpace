package com.example.capstone.ui.fragment.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.adapter.NewsAdapter
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.ArticlesItem
import com.example.capstone.databinding.FragmentHomeBinding
import com.example.capstone.viewmodel.NewsViewModel
import com.example.capstone.viewmodel.ViewModelFactory
import com.example.capstone.viewmodel.WeatherViewModel
import com.example.capstone.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeFragment : Fragment() {
    private var _binding:FragmentHomeBinding?=null
    private val binding get()=_binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentHomeBinding.inflate(inflater,container,false)

        val newsfactory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val newsviewModel: NewsViewModel by viewModels{
            newsfactory
        }

        val weatherfactory:WeatherViewModelFactory=WeatherViewModelFactory.getInstance(requireContext())
        val weatherviewmodel: WeatherViewModel by viewModels{
            weatherfactory
        }

        weatherviewmodel.getWeather(LOCATION_DATA).observe(viewLifecycleOwner){ result->
            if(result!=null){
                when(result){
                    is Result.Loading->{
                        binding.ProgressBarWeather.visibility=View.VISIBLE
                    }
                    is Result.Success->{
                        binding.apply {
                            ProgressBarWeather.visibility=View.GONE
                            weatherName.text=result.data.location.name
                            weatherDate.text=result.data.location.localtime
                            weatherDegree.text=result.data.current.tempC.toString()
                            weatherDegree.text=getString(R.string.degree_value,result.data.current.tempC)
                        }
                        Log.e("Another",result.data.current.condition.icon)
                        Glide.with(requireContext())
                            .load("https:${result.data.current.condition.icon}")
                            .into(binding.weatherImage)

                    }
                    is Result.Error->{
                        binding.ProgressBarWeather.visibility=View.GONE
                        Log.e("Error",result.error)
                        Toast.makeText(context,"Terdapat kesalahan +${result.error}",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }


        val rvNews=binding.rvNews
        rvNews.setHasFixedSize(true)
        val layoutManager=LinearLayoutManager(requireContext())
        binding.rvNews.layoutManager=layoutManager
        val itemDecoration=DividerItemDecoration(requireContext(),layoutManager.orientation)
        binding.rvNews.addItemDecoration(itemDecoration)

        newsviewModel.getAllNews().observe(viewLifecycleOwner){result->
            if(result!=null){
                when(result){
                    is Result.Loading->{
                        binding.progressBar.visibility=View.VISIBLE
                    }
                    is Result.Success->{
                        binding.progressBar.visibility=View.GONE
                        setNewsData(result.data)
                    }
                    is Result.Error->{
                        binding.progressBar.visibility=View.GONE
                        Toast.makeText(requireContext(),result.error,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }
    private fun setNewsData(news:List<ArticlesItem>){
        val adapter=NewsAdapter(news)
        binding.rvNews.adapter=adapter
        adapter.setOnItemClickCallback(object :NewsAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ArticlesItem) {
                val intent= Intent(Intent.ACTION_VIEW)
                intent.data= Uri.parse(data.url)
                startActivity(intent)

            }

        })
    }

    companion object{
        var LOCATION_DATA = "location_data"
    }



}