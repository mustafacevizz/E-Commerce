package com.mcvz.e_commerceapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcvz.e_commerceapp.adapters.AllOrderAdapter
import com.mcvz.e_commerceapp.databinding.FragmentAllOrdersBinding
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.AllOrderViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllOrderFragment:Fragment() {
    private lateinit var binding: FragmentAllOrdersBinding
    val viewModel by viewModels<AllOrderViewModal>()
    val allOrderAdapter by lazy { AllOrderAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAllOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAllOrderRv()

        lifecycleScope.launchWhenStarted {
            viewModel.allOrder.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        allOrderAdapter.differ.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarAllOrders.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
        allOrderAdapter.onClick={
            val action=AllOrderFragmentDirections.actionAllOrderFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun setupAllOrderRv() {
        binding.rvAllOrders.apply {
            adapter=allOrderAdapter
            layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        }
    }
}