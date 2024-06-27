package com.mcvz.e_commerceapp.fragments.shopping

import android.app.AlertDialog
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
import com.google.android.material.snackbar.Snackbar
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.adapters.AddressAdapter
import com.mcvz.e_commerceapp.adapters.BillingProductAdapter
import com.mcvz.e_commerceapp.data.Address
import com.mcvz.e_commerceapp.data.CartProduct
import com.mcvz.e_commerceapp.data.order.Order
import com.mcvz.e_commerceapp.data.order.OrderStatus
import com.mcvz.e_commerceapp.databinding.FragmentBillingBinding
import com.mcvz.e_commerceapp.util.HorizontalItemDecoration
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.BillingViewModal
import com.mcvz.e_commerceapp.viewmodel.OrderViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class BillingFragment:Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val billingViewModel by viewModels<BillingViewModal>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products= emptyList<CartProduct>()
    private var totalPrice= 0f
    private var selectedAddress:Address?=null
    private val orderViewModal by viewModels<OrderViewModal>()


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

        if (!args.payment){
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
        }

        binding.imageAddAddress.setOnClickListener{
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
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

        lifecycleScope.launchWhenStarted {
            orderViewModal.order.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success->{
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Sipariş verildi",Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Error->{
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(),"Error ${it.message}",Toast.LENGTH_SHORT).show()
                    }else->Unit
                }
            }
        }

        billingProductAdapter.differ.submitList(products)
        binding.tvTotalPrice.text="${totalPrice} TL"

        addressAdapter.onClick={
            selectedAddress=it
            if (!args.payment) {
                val b = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, b)
            }
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress==null){
                Toast.makeText(requireContext(),"Lütfen bir adres seçiniz",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }

    private fun showOrderConfirmationDialog() {
            val alertDialog= AlertDialog.Builder(requireContext()).apply {
                setTitle("Siparişi Tamamla")
                setMessage("Sepetteki ürünleri sipariş etmek edeceksiniz")
                setNegativeButton("İptal") { dialog,_->
                    dialog.dismiss()
                }
                setPositiveButton("Evet"){dialog,_->
                    val order= Order(
                        OrderStatus.Ordered.status,
                        totalPrice,
                        products,
                        selectedAddress!!
                    )
                    orderViewModal.placeHolder(order)
                    dialog.dismiss()
                }

            }
            alertDialog.create()
            alertDialog.show()
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