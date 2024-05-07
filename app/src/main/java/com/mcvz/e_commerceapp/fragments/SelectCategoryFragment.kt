package com.mcvz.e_commerceapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.ShoppingActivity
import com.mcvz.e_commerceapp.data.Category
import com.mcvz.e_commerceapp.data.CategoryItem
import com.mcvz.e_commerceapp.data.User
import com.mcvz.e_commerceapp.data.allCategoryIds
import com.mcvz.e_commerceapp.databinding.FragmentLoginBinding
import com.mcvz.e_commerceapp.databinding.FragmentSelectCategoryBinding
import com.mcvz.e_commerceapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCategoryFragment:Fragment(R.layout.fragment_select_category) {
    private lateinit var binding: FragmentSelectCategoryBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val selectedCategories = mutableSetOf<Int>()

    //private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = auth.currentUser?.uid

        val categoryButtons = listOf(
            binding.btnSelectClothes,
            binding.btnSelectEducation,
            binding.btnSelectElectronic,
            binding.btnSelectFood,
            binding.btnSelectFurniture
        )

        for (button in categoryButtons) {
            button.setOnClickListener {
                userId?.let { userId ->
                    val categoryId = when (button) {
                        binding.btnSelectClothes -> Category.Clothes.id
                        binding.btnSelectEducation -> Category.Education.id
                        binding.btnSelectElectronic -> Category.Electronics.id
                        binding.btnSelectFood -> Category.Food.id
                        binding.btnSelectFurniture -> Category.Furniture.id
                        else -> -1 // Hata durumunu işleme koymak için
                    }

                    if (categoryId != -1) {
                        toggleCategorySelection(categoryId)
                    } else {
                        //selectedCategories.remove(categoryId)
                        // Hata durumunu işleme koymak için bir şeyler yapabilirsiniz.
                    }
                }
            }
        }
        binding.btnSelectDone.setOnClickListener {
            userId?.let {
                /*userId->
                val selectedCategories = categoryButtons
                .filter { it.isSelected }
                .mapNotNull { button ->
                    when (button) {
                        binding.btnSelectClothes -> Category.Clothes.id
                        binding.btnSelectEducation -> Category.Education.id
                        binding.btnSelectElectronic -> Category.Electronics.id
                        binding.btnSelectFood -> Category.Food.id
                        binding.btnSelectFurniture -> Category.Furniture.id
                        else -> null
                    }
                }*/
                updateCategories(userId, selectedCategories.toList())

                /*val userCategories = mutableListOf<Int>()
                for (button in categoryButtons) {
                    val categoryId = when (button) {
                        binding.btnSelectClothes -> Category.Clothes.id
                        binding.btnSelectEducation -> Category.Education.id
                        binding.btnSelectElectronic -> Category.Electronics.id
                        binding.btnSelectFood -> Category.Food.id
                        binding.btnSelectFurniture -> Category.Furniture.id
                        else -> -1
                    }
                    if (categoryId != -1 && userCategories.contains(categoryId).not()) {
                        userCategories.add(categoryId)
                        /*val categoryMap = mapOf(
                            "id" to categoryId,
                            "name" to getCategoryNameById(categoryId)
                        )
                        userCategories.add(categoryMap)*/

                    }
                    val userData = mapOf(
                        "categories" to userCategories
                    )
                    db.collection("user")
                        .document(userId)
                        .update(userData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Kategori başarıyla güncellendi",
                                Toast.LENGTH_SHORT
                            ).show()
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)   //önceki aktiviteler stackten temizlenir. Yeni aktivite stackte yerini alır
                                startActivity(intent)
                            }
                        }.addOnFailureListener { it ->
                            Toast.makeText(
                                context,
                                "Kategori Güncellenirken bir hata oldu",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }*/
            }

        }
    }


    private fun toggleCategorySelection(categoryId: Int) {
        if (selectedCategories.contains(categoryId)){
            selectedCategories.remove(categoryId)
        }else{
            selectedCategories.add(categoryId)
        }

    }

    private fun getCategoryNameById(categoryId: Int): String {
        return when (categoryId) {
            Category.Clothes.id -> "Clothes"
            Category.Education.id -> "Education"
            Category.Electronics.id -> "Electronics"
            Category.Food.id -> "Food"
            Category.Furniture.id -> "Furniture"
            else -> ""

        }

    }
    private fun updateCategory(categoryId: Int, selected: Int) {
        val categoryData = mapOf(
            "selected" to selected
        )

        db.collection("categories")
            .document(categoryId.toString())
            .update(categoryData)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Kategori başarıyla güncellendi",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Kategori Güncellenirken hata oluştu",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun updateCategories(userId: String?,selectedCategories:List<Int>) {

        val userData= mapOf(
            "categories" to selectedCategories.map {categoryId->
                CategoryItem(categoryId,getCategoryNameById(categoryId))
            }
        )
        userId?.let {
            db.collection("user")
                .document(it)
                .update(userData)
                .addOnSuccessListener { documentSnapshot ->
                    /*if (documentSnapshot.exists()) {
                        val user = documentSnapshot.toObject(User::class.java)
                        user?.let {
                            val currentCategories = it.categories.toMutableList()

                            if (!currentCategories.contains(categoryId)) {
                                currentCategories.add(categoryId)
                                db.collection("user")
                                    .document(userId)
                                    .update("categories", currentCategories)
                                    .addOnSuccessListener {*/
                                        Toast.makeText(
                                            context,
                                            "Kategori başarıyla güncellendi",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Kategori Güncellenirken hata oluştu",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                            }
    }

}

