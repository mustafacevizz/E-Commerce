package com.mcvz.e_commerceapp.viewmodel

import android.icu.text.Collator.ReorderCodes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.order.Order
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrderViewModal @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
):ViewModel(){
    private val pAllOrder= MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrder=pAllOrder.asStateFlow()

    init {
        getAllOrder()
    }

    fun getAllOrder(){
        viewModelScope.launch {
            pAllOrder.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!).collection("orders").get().addOnSuccessListener {
            val orders=it.toObjects(Order::class.java)
            viewModelScope.launch {
                pAllOrder.emit(Resource.Success(orders))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                pAllOrder.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}