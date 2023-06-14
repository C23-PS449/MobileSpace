package com.example.capstone.ui.fragment.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rvNews = binding.rvNews
        rvNews.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvNews.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvNews.addItemDecoration(itemDecoration)

        getWeather(LOCATION_DATA)
        getNews()


        return binding.root
    }

    private fun getWeather(location:String){
        val weatherfactory: WeatherViewModelFactory =
            WeatherViewModelFactory.getInstance(requireContext())
        val weatherviewmodel: WeatherViewModel by viewModels {
            weatherfactory
        }

        weatherviewmodel.getWeather(location).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.ProgressBarWeather.visibility = View.GONE
                        binding.refreshWeather.visibility=View.GONE
                    }
                    is Result.Success -> {
                        binding.refreshWeather.visibility=View.GONE
                        binding.ProgressBarWeather.visibility = View.GONE
                        binding.weatherName.text = result.data.location.name
                        binding.weatherDate.text = result.data.location.localtime
                        binding.weatherDegree.text = result.data.current.tempC.toString()
                        binding.weatherDegree.text =
                            getString(R.string.degree_value, result.data.current.tempC)
                        Glide.with(requireContext())
                            .load("https:${result.data.current.condition.icon}")
                            .into(binding.weatherImage)
                    }
                    is Result.Error -> {
                        Snackbar.make(binding.root,getString(R.string.weather_failure),Snackbar.LENGTH_SHORT)
                        binding.ProgressBarWeather.visibility = View.GONE
                        binding.refreshWeather.visibility=View.VISIBLE
                        binding.refreshWeather.setOnClickListener {
                            getWeather(LOCATION_DATA)
                        }

                    }
                }
            }

        }

    }

    private fun getNews(){
        val newsfactory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val newsviewModel: NewsViewModel by viewModels {
            newsfactory
        }
        newsviewModel.getAllNews().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.refreshNews.visibility=View.GONE
                        setNewsData(result.data)
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.refreshNews.visibility=View.VISIBLE
                        binding.refreshNews.setOnClickListener {
                            getNews()
                        }
                        Snackbar.make(binding.root,"Terjadi kesalahan, Silahkan muat ulang halaman untuk menampilkan daftar berita",Snackbar.LENGTH_SHORT).show()

                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }

            })
        }
    }

    private fun setNewsData(news: List<ArticlesItem>) {
        val adapter = NewsAdapter(news)
        binding.rvNews.adapter = adapter
        adapter.setOnItemClickCallback(object : NewsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ArticlesItem) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(data.url)
                startActivity(intent)

            }
        })
    }

    companion object {
        var LOCATION_DATA = "location_data"
    }


}