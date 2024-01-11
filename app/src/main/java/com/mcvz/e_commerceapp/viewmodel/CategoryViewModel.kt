package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.Category
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore:FirebaseFirestore,
    private val category: Category
):ViewModel() {
    private val pOfferProducts=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts=pOfferProducts.asStateFlow()

    private val pBestProducts=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts=pBestProducts.asStateFlow()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts(){
        viewModelScope.launch {
            pOfferProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category",category.category).whereNotEqualTo("offerPercentage",null)
            .get().addOnSuccessListener {
                val products=it.toObjects(Product::class.java)
                viewModelScope.launch {
                    pOfferProducts.emit(Resource.Success(products))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    pOfferProducts.emit(Resource.Error(it.message.toString()))
                }

            }
    }
    fun fetchBestProducts(){
        viewModelScope.launch {
            pBestProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category",category.category).whereEqualTo("offerPercentage",null)
            .get().addOnSuccessListener {
                val products=it.toObjects(Product::class.java)
                viewModelScope.launch {
                    pBestProducts.emit(Resource.Success(products))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    pBestProducts.emit(Resource.Error(it.message.toString()))
                }

            }
    }


}