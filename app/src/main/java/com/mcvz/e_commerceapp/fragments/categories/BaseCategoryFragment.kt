package com.mcvz.e_commerceapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.adapters.BestProductAdapter
import com.mcvz.e_commerceapp.databinding.FragmentBaseCategoryBinding
import com.mcvz.e_commerceapp.util.showBottomNavigationView

open class BaseCategoryFragment:Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductAdapter by lazy { BestProductAdapter() }
    protected val bestProductsAdapter: BestProductAdapter by lazy { BestProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOfferRv()
        setupBestProductsRv()

        bestProductsAdapter.onClick={
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }
        offerAdapter.onClick={
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        binding.rvOfferProducts.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)&& dx !=0){
                    onOfferPagingRequests()
                }
            }
        })
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _->
            if (v.getChildAt(0).bottom<=v.height+scrollY){
                onBestProductsPagingRequests()
            }
        })
    }

    fun showOfferLoading(){
        binding.offerProductsProgressBar.visibility=View.VISIBLE
    }
    fun hideOfferLoading(){
        binding.offerProductsProgressBar.visibility=View.GONE

    }


    fun showBestProductsLoading(){
        binding.bestProductsProgressBar.visibility=View.VISIBLE

    }
    fun hideBestProductsLoading(){
        binding.bestProductsProgressBar.visibility=View.GONE

    }

    open fun onOfferPagingRequests(){

    }

    open fun onBestProductsPagingRequests(){

    }

    private fun setupOfferRv() {
        binding.rvOfferProducts.apply {
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=offerAdapter
        }
    }

    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager= GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false)
            adapter=bestProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}