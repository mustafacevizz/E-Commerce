package com.mcvz.e_commerceapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mcvz.e_commerceapp.data.Category
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.data.User
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

    private val pUser=MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user=pUser.asStateFlow()

    init {
        fetchUser()
        fetchOfferProducts()
        fetchBestProducts()
    }



    fun fetchUser(){
        viewModelScope.launch {
            pUser.emit(Resource.Loading())
        }
        firestore.collection("user").document("qwqwe")
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

    fun fetchOfferProducts(){
        viewModelScope.launch {
            pOfferProducts.emit(Resource.Loading())
        }

        firestore.collection("user").document("2313")
            .get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                if (task.isSuccessful && task.result != null) {
                    val firstName = task.result!!.getString("firstName")
                    val email = task.result!!.getString("email")
                    val categories = task.result!!.get("categories")
                    /*Log.d("firstName", firstName.toString())
                    Log.d("email", email.toString())
                    Log.d("categories", categories.toString())*/
                    //other stuff
                } else {
                    //deal with error
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