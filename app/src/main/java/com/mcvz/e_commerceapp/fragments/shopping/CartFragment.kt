package com.mcvz.e_commerceapp.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.adapters.CartProductAdapter
import com.mcvz.e_commerceapp.databinding.FragmentCartBinding
import com.mcvz.e_commerceapp.firebase.FirebaseCommon
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.util.VerticalItemDecoration
import com.mcvz.e_commerceapp.viewmodel.CartViewModal
import kotlinx.coroutines.flow.collectLatest

class CartFragment: Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModal>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCartBinding.inflate(inflater)
        return binding.root
    }


    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility=View.GONE
            totalBoxContainer.visibility=View.GONE
            buttonCheckout.visibility=View.GONE
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility=View.VISIBLE
            totalBoxContainer.visibility=View.VISIBLE
            buttonCheckout.visibility=View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRv()

        var totalPrice = 0f

        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest {price->
                price?.let {
                    totalPrice=it
                    binding.tvTotalPrice.text="$price TL"
                }

            }
        }

        cartAdapter.onProductClick={
            val b=Bundle().apply { putParcelable("product",it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,b)
        }

        cartAdapter.onPlusClick={
            viewModel.changeAmount(it,FirebaseCommon.AmountChanging.INCREASE)
        }

        cartAdapter.onMinusClick={
            viewModel.changeAmount(it,FirebaseCommon.AmountChanging.DECREASE)
        }

        binding.buttonCheckout.setOnClickListener{
            val action=CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray())
            findNavController().navigate(action)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog=AlertDialog.Builder(requireContext()).apply {
                    setTitle("Sepetten ürünü silmek istiyor musunuz?")
                    setMessage("Sepetten bu ürünü silmek istiyor musunuz")
                    setNegativeButton("İptal") { dialog,_->
                        dialog.dismiss()
                    }
                    setPositiveButton("Evet"){dialog,_->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }

                }
                alertDialog.create()
                alertDialog.show()
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.cartProduct.collectLatest {
                when(it){
                    is Resource.Success->{
                        binding.progressbarCart.visibility=View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherViews()
                        }
                        else {
                            hideEmptyCart()
                            cartAdapter.differ.submitList(it.data)
                            showOtherViews()
                        }
                    }
                    is Resource.Loading->{
                        binding.progressbarCart.visibility=View.VISIBLE
                    }
                    is Resource.Error->{
                        binding.progressbarCart.visibility=View.INVISIBLE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }

    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility=View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility=View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            adapter=cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}