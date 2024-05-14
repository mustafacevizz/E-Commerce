package com.mcvz.e_commerceapp.viewmodel

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
class OrderViewModal @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
):ViewModel() {

    private val pOrder= MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order=pOrder.asStateFlow()

    fun placeHolder(order:Order){
        viewModelScope.launch {
            pOrder.emit(Resource.Loading())
        }
        firestore.runBatch{batch->
            firestore.collection("user").document(auth.uid!!).collection("orders").document().set(order)
            firestore.collection("orders").document().set(order)
            firestore.collection("user").document(auth.uid!!).collection("cart").get().addOnSuccessListener {
                it.documents.forEach{
                    it.reference.delete()
                }
            }

        }.addOnSuccessListener {
            viewModelScope.launch {
                pOrder.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                pOrder.emit(Resource.Error(it.message.toString()))
            }
        }
    }

}
