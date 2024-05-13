package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.Address
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModal @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel(){
    private val pAddress= MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address=pAddress.asStateFlow()

    init {
        getUserAddresses()
    }


    fun getUserAddresses(){
        viewModelScope.launch {
            pAddress.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if (error!=null){
                    viewModelScope.launch { pAddress.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                val addresses=value?.toObjects(Address::class.java)
                viewModelScope.launch { pAddress.emit(Resource.Success(addresses!!)) }
            }
    }
}