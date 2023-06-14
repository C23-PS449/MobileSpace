package com.example.capstone.ui.fragment.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.R
import com.example.capstone.adapter.ProductsAdapter
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.ResultsItem
import com.example.capstone.databinding.FragmentProdukBinding
import com.example.capstone.ui.fragment.home.HomeFragment
import com.example.capstone.viewmodel.LocationViewModel
import com.example.capstone.viewmodel.LocationViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ProdukFragment : Fragment() {

    private var _binding: FragmentProdukBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProdukBinding.inflate(inflater, container, false)


        val rvProduct = binding.rvRecyclerview
        rvProduct.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecyclerview.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvRecyclerview.addItemDecoration(itemDecoration)

        getProductList(HomeFragment.LOCATION_DATA)

        return binding.root
    }

    private fun getProductList(location:String){
        val locationfactory: LocationViewModelFactory =
            LocationViewModelFactory.getInstance(requireContext())
        val locationViewModel: LocationViewModel by viewModels {
            locationfactory
        }
        locationViewModel.getLocation(location)
            .observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.refreshShop.visibility=View.GONE
                            binding.progressBarProduct.visibility=View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.refreshShop.visibility=View.GONE
                            binding.progressBarProduct.visibility=View.GONE
                            setProductData(result.data)
                        }

                        is Result.Error -> {
                            binding.refreshShop.visibility=View.VISIBLE
                            binding.progressBarProduct.visibility=View.GONE
                            Snackbar.make(binding.root,getString(R.string.failed_shop_list),
                                Snackbar.LENGTH_SHORT).show()
                            binding.refreshShop.setOnClickListener {
                                getProductList(HomeFragment.LOCATION_DATA)
                            }
                        }
                    }
                }
            }
    }

    private fun setProductData(product: List<ResultsItem>) {
        val adapter = ProductsAdapter(product)
        binding.rvRecyclerview.adapter = adapter
        adapter.setOnItemClickCallback(object : ProductsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ResultsItem) {
                val fragmentManager=parentFragmentManager
                val detailProdukFragment=DetailProdukFragment()
                fragmentManager.beginTransaction().apply{
                    replace(R.id.containerFragment,detailProdukFragment,DetailProdukFragment::class.java.simpleName)
                    DetailProdukFragment.EXTRA_DATA=data
                    addToBackStack(null)
                        .commit()
                }
            }

        })
    }

}