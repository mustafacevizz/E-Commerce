package com.mcvz.e_commerceapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.ShoppingActivity
import com.mcvz.e_commerceapp.adapters.ColorsAdapter
import com.mcvz.e_commerceapp.adapters.SizesAdapter
import com.mcvz.e_commerceapp.adapters.ViewPagerImages
import com.mcvz.e_commerceapp.data.CartProduct
import com.mcvz.e_commerceapp.databinding.FragmentProductDetailsBinding
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.util.hideBottomNavigationView
import com.mcvz.e_commerceapp.viewmodel.DetailsViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment:Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding:FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPagerImages() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor:Int?=null
    private var selectedSize:String?=null
    private val viewModel by viewModels<DetailsViewModal>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView()
        binding=FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val products=args.product

        setupSizesRv()
        setupColorsRv()
        setupViewpager()

        binding.apply {
            tvProductName.text=products.name
            tvProductPrice.text="${products.price} TL"
            tvProductDescription.text=products.description

            if (products.colors.isNullOrEmpty())
                tvProductColors.visibility=View.INVISIBLE

            if (products.sizes.isNullOrEmpty())
                tvProductSize.visibility=View.INVISIBLE
        }

        viewPagerAdapter.differ.submitList(products.images)

        sizesAdapter.onItemClick={
            selectedSize=it
        }
        colorsAdapter.onItemClick={
            selectedColor=it
        }
        binding.buttonAddToCart.setOnClickListener {
            viewModel.UpdateProductInCart(CartProduct(products,1,selectedColor,selectedSize))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.buttonAddToCart.startAnimation()
                    }

                    is Resource.Success->{
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }

                    is Resource.Error->{
                        binding.buttonAddToCart.stopAnimation()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }


        products.colors?.let {
            colorsAdapter.differ.submitList(it)
        }
        products.sizes?.let {
            sizesAdapter.differ.submitList(it)
        }


    }

    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter=viewPagerAdapter
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter=colorsAdapter
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter=sizesAdapter
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}