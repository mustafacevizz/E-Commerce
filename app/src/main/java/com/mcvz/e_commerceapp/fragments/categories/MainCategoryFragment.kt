package com.mcvz.e_commerceapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.adapters.BestDealsAdapter
import com.mcvz.e_commerceapp.adapters.BestProductAdapter
import com.mcvz.e_commerceapp.adapters.SpecialProductsAdapter
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.databinding.FragmentMainCategoryBinding
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.util.showBottomNavigationView
import com.mcvz.e_commerceapp.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG="MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment:Fragment(R.layout.fragment_main_category) {
    private lateinit var binding:FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter:BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpecialProductsRv()
        setupBestDealsRv()   //
        setupBestProductsRv()
        specialProductsAdapter= SpecialProductsAdapter()

        specialProductsAdapter.onClick={
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestDealsAdapter.onClick={
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestProductsAdapter.onClick={
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showLoading()
                    }
                    is Resource.Success->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error->{
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }else->Unit

                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.bestProductsProgressbar.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.bestProductsProgressbar.visibility=View.GONE
                    }
                    is Resource.Error->{
                        binding.bestProductsProgressbar.visibility=View.GONE
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }else->Unit


                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showLoading()
                    }
                    is Resource.Success->{
                        specialProductsAdapter.differ.submitList(it.data)   //Veriler adaptera eklenir ve güncellenir
                        hideLoading()
                    }
                    is Resource.Error->{
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }else->Unit

                }
            }
        }
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener {v,_,scrollY,_,_->
            if (v.getChildAt(0).bottom<=v.height+scrollY){
                viewModel.fetchBestProduct()
            }

        })


    }

    private fun setupBestProductsRv() {
        bestProductsAdapter= BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager=GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter=bestProductsAdapter
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter= BestDealsAdapter()    //
        binding.rvBestDealsProducts.apply {
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=bestDealsAdapter
        }
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility=View.GONE
    }

    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility=View.VISIBLE
    }

    fun setupSpecialProductsRv() {
        specialProductsAdapter= SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=specialProductsAdapter
        }
    }


    /*fun filterProducts(query: String) {
        Log.e("abx",query)
        //specialProductsAdapter= SpecialProductsAdapter(emptyList())
        val filteredSpecialProducts = specialProductsAdapter.differ.currentList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        Log.e("filtersSpclprdct",filteredSpecialProducts.toString())

        specialProductsAdapter.differ.submitList(filteredSpecialProducts)
        //bestDealsAdapter= BestDealsAdapter(emptyList())
        val filteredBestDealsProducts = bestDealsAdapter.differ.currentList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        bestDealsAdapter.differ.submitList(filteredBestDealsProducts)
        //bestProductsAdapter= BestProductAdapter(emptyList())
        val filteredBestProducts = bestProductsAdapter.differ.currentList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        bestProductsAdapter.differ.submitList(filteredBestProducts)
    }*/

    //fun setupFun(){
       // setupBestDealsRv()
        //setupBestProductsRv()
        //setupSpecialProductsRv()
   // }



    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}