package com.mcvz.e_commerceapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.data.User
import com.mcvz.e_commerceapp.databinding.FragmentRegisterBinding
import com.mcvz.e_commerceapp.util.RegisterValidation
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

val TAG="RegisterFragment"
@AndroidEntryPoint
class RegisterFragment:Fragment(R.layout.fragment_register) {
    private lateinit var binding : FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.twRegisterText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment) //Register fragmenttan logine geçiş
        }
        binding.apply {
            btnRegister.setOnClickListener{
                val user=User(
                    etRegisterName.text.toString().trim(),
                    etRegisterLastName.text.toString().trim(),
                    etRegisterEmail.text.toString().trim(),
                )
                val password=etRegisterPassword.text.toString()
                viewModel.createAccountWithEmailAndPassword(user,password)
            }


        }
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading->{
                        binding.btnRegister.startAnimation()
                    }
                    is Resource.Success->{
                        Log.d("test",it.data.toString())
                        binding.btnRegister.revertAnimation()
                    }
                    is Resource.Error->{
                        Log.e(TAG,it.message.toString())
                        binding.btnRegister.revertAnimation()
                    }
                    else -> Unit
                }

            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect{validation->
                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etRegisterEmail.apply {
                            requestFocus()
                            error= validation.email.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etRegisterPassword.apply {
                            requestFocus()
                            error=validation.password.message
                        }
                    }
                }
            }
        }
    }
}