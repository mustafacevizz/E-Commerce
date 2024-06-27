package com.mcvz.e_commerceapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mcvz.e_commerceapp.databinding.ActivityShoppingBinding
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.CartViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<CartViewModal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController=findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)  //Fragmentlar arası geçişi ayarlar

        lifecycleScope.launchWhenStarted {
            viewModel.cartProduct.collectLatest {
                when(it){
                    is Resource.Success->{
                        val count=it.data?.size?:0
                        val bottomNavigation=findViewById<BottomNavigationView>(R.id.bottomNavigation)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number=count
                            backgroundColor=resources.getColor(R.color.g_orange_yellow)
                        }
                    }
                    else-> Unit
                }
            }
        }


    }
}