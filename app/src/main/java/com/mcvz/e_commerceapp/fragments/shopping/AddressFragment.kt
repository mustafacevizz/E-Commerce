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
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.data.Address
import com.mcvz.e_commerceapp.databinding.FragmentAddressBinding
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.AddressViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment:Fragment() {
    private lateinit var binding: FragmentAddressBinding
    val viewModal by viewModels<AddressViewModal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModal.addNewAddress.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility=View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility=View.INVISIBLE
                        findNavController().navigateUp()

                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()


                    }else->Unit
            }
        }
    }

        lifecycleScope.launchWhenStarted {
            viewModal.error.collectLatest {
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            }
        }
}


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSave.setOnClickListener {
            val addressTitle=edAddressTitle.text.toString()
            val fullName=edFullName.text.toString()
            val street=edStreet.text.toString()
            val phone=edPhone.text.toString()
            val city=edCity.text.toString()
            val state=edState.text.toString()
            val address=Address(addressTitle, fullName, street, phone, city, state)

            viewModal.addAddress(address)
        }


        }
    }
}