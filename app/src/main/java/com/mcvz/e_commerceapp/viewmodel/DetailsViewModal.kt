package com.mcvz.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mcvz.e_commerceapp.data.CartProduct
import com.mcvz.e_commerceapp.firebase.FirebaseCommon
import com.mcvz.e_commerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailsViewModal @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
):ViewModel(){

    private val pAddToCart= MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart=pAddToCart.asStateFlow()

    fun UpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch { pAddToCart.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id",cartProduct.product.id).get().addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()){
                        addNewProduct(cartProduct)
                    }else{
                        val product=it.first().toObject(CartProduct::class.java)
                        if (product==cartProduct){
                            val documentId=it.first().id
                            increaseTheAmount(documentId, cartProduct)
                        }else{
                            addNewProduct(cartProduct)

                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { pAddToCart.emit(Resource.Error(it.message.toString())) }

            }
    }

    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){ addedProduct, e->
            viewModelScope.launch {
                if (e==null){
                    pAddToCart.emit(Resource.Success(addedProduct!!))
                }else{
                    pAddToCart.emit(Resource.Error(e.message.toString()))
                }
            }

        }
    }

    private fun increaseTheAmount(documentId: String,cartProduct: CartProduct){
        firebaseCommon.increaseTheAmount(documentId){ _,e->
            viewModelScope.launch {
                if (e==null){
                    pAddToCart.emit(Resource.Success(cartProduct))
                }else{
                    pAddToCart.emit(Resource.Error(e.message.toString()))

                }
            }

        }
    }



}