package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModal @Inject constructor(
    private val firestore: FirebaseFirestore
):ViewModel(){
    private val pAllProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val allProducts: StateFlow<Resource<List<Product>>> = pAllProducts


    init {
        getAllProducts()
    }

    fun getAllProducts() {
        viewModelScope.launch {
            pAllProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").get()
            .addOnSuccessListener { result ->
                val allproductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    pAllProducts.emit(Resource.Success(allproductsList))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    pAllProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}

