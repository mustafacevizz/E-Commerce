package com.mcvz.e_commerceapp.data

sealed class Category(val category: String) {
    object ForYou: Category("For You")
    object Clothes: Category("Clothes")
    object Furniture: Category("Furniture")
    object Food: Category("Food")
    object Education: Category("Education")
    object Electronics: Category("Electronics")
}