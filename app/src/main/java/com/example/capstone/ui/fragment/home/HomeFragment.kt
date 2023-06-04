package com.example.capstone.ui.fragment.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.NewsAdapter
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.ArticlesItem
import com.example.capstone.databinding.FragmentHomeBinding
import com.example.capstone.viewmodel.NewsViewModel
import com.example.capstone.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding:FragmentHomeBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentHomeBinding.inflate(inflater,container,false)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel: NewsViewModel by viewModels{
            factory
        }
        val rvNews=binding.rvNews
        rvNews.setHasFixedSize(true)
        val layoutManager=LinearLayoutManager(requireContext())
        binding.rvNews.layoutManager=layoutManager
        val itemDecoration=DividerItemDecoration(requireContext(),layoutManager.orientation)
        binding.rvNews.addItemDecoration(itemDecoration)

        viewModel.getAllNews().observe(viewLifecycleOwner){result->
            if(result!=null){
                when(result){
                    is Result.Loading->{
                    }
                    is Result.Success->{
                        setNewsData(result.data)
                    }
                    is Result.Error->{
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

}