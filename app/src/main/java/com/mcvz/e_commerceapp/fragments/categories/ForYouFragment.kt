package com.mcvz.e_commerceapp.fragments.categories

import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.Category
import com.mcvz.e_commerceapp.viewmodel.CategoryViewModel
import com.mcvz.e_commerceapp.viewmodel.factory.BaseCategoryViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForYouFragment:BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore
    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.ForYou )
    }
}