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
import androidx.recyclerview.widget.RecyclerView
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.adapters.AddressAdapter
import com.mcvz.e_commerceapp.adapters.BillingProductAdapter
import com.mcvz.e_commerceapp.data.CartProduct
import com.mcvz.e_commerceapp.databinding.FragmentBillingBinding
import com.mcvz.e_commerceapp.util.HorizontalItemDecoration
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.BillingViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class BillingFragment:Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val viewModel by viewModels<BillingViewModal>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products= emptyList<CartProduct>()
    private var totalPrice= 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        products=args.products.toList()
        totalPrice=args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBillingProductRv()
        setupAddressRv()

        binding.imageAddAddress.setOnClickListener{
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarAddress.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility=View.GONE

                    }
                    is Resource.Error->{
                        binding.progressbarAddress.visibility=View.GONE
                        Toast.makeText(requireContext(),"Error ${it.message}",Toast.LENGTH_SHORT).show()
                    }else->Unit
                }
            }
        }
        billingProductAdapter.differ.submitList(products)
        binding.tvTotalPrice.text="${totalPrice} TL"
    }

    private fun setupBillingProductRv() {
        binding.rvProducts.apply {
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter=billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter=addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}