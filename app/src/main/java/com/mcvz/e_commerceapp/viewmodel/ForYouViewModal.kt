package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.mcvz.e_commerceapp.data.Category
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.util.RegisterValidation
import com.mcvz.e_commerceapp.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForYouViewModal constructor(
    private val firestore:FirebaseFirestore,
    private val category: Category)
    :ViewModel() {
    private val pOfferProducts=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts=pOfferProducts.asStateFlow()

    private val pBestProducts=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts=pBestProducts.asStateFlow()

    private val pUser=MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user=pUser.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }
    fun fetchOfferProducts(){
        viewModelScope.launch {
            pOfferProducts.emit(Resource.Loading())
        }
        firestore.collection("user").document(userId!!)
            .get().addOnSuccessListener {
                val user=it.toObject(User::class.java)
                viewModelScope.launch {
                    pUser.emit(Resource.Success(user))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    pUser.emit(Resource.Error(it.message.toString()))
                }

            }
    }
    fun fetchBestProducts(){
        viewModelScope.launch {
            pBestProducts.emit(Resource.Loading())
        }
        firestore.collection("user").document(userId!!)
            .get().addOnSuccessListener {
                val user=it.toObject(User::class.java)
                viewModelScope.launch {
                    pUser.emit(Resource.Success(user))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    pBestProducts.emit(Resource.Error(it.message.toString()))
                }

            }
    }
}

