package com.mcvz.e_commerceapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.Category
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.CategoryViewModel
import com.mcvz.e_commerceapp.viewmodel.factory.BaseCategoryViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class ForYouFragment:BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore
    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Clothes )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showOfferLoading()
                    }
                    is Resource.Success->{
                        hideOfferLoading()
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideOfferLoading()
                    }
                    else->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showBestProductsLoading()
                    }
                    is Resource.Success->{
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideBestProductsLoading()
                    }
                    else->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showBestProductsLoading()
                    }
                    is Resource.Success->{
                        userCategoryAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideBestProductsLoading()
                    }
                    else->Unit
                }
            }
        }

    }

}