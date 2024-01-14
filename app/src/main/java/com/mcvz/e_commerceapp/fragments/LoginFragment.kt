package com.mcvz.e_commerceapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.ShoppingActivity
import com.mcvz.e_commerceapp.databinding.FragmentLoginBinding
import com.mcvz.e_commerceapp.dialog.setupBottomSheetDialog
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment:Fragment(R.layout.fragment_login) {
    private lateinit var binding:FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.twLoginRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            btnLogIn.setOnClickListener {
                val email = etSignInEmail.text.toString().trim()    //yazılan yazının başındaki ve sonundaki boşlukları sile->trim
                val password = etSignInPassword.text.toString()
                viewModel.login(email, password)
                findNavController().navigate(R.id.action_loginFragment_to_selectCategoryFragment)
            }

        }

        binding.twForgotPassword.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Parola sıfırlama linki emailinize gönderildi",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Hata: ${it.message}", Snackbar.LENGTH_LONG)
                            .show()

                    }

                    else -> Unit
                }
            }

            lifecycleScope.launchWhenStarted {
                viewModel.login.collect {
                    when (it) {
                        is Resource.Loading -> {
                            binding.btnLogIn.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.btnLogIn.revertAnimation()
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)   //önceki aktiviteler stackten temizlenir. Yeni aktivite stackte yerini alır
                                startActivity(intent)

                            }
                        }

                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                            binding.btnLogIn.revertAnimation()
                        }

                        else -> Unit
                    }
                }
            }


        }
    }
}