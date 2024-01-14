package com.mcvz.e_commerceapp.data


sealed class Category(val id:Int, val category: String) {
    object Clothes: Category(1,"Clothes")
    object Furniture: Category(2,"Furniture")
    object Food: Category(3,"Food")
    object Education: Category(4,"Education")
    object Electronics: Category(5,"Electronics")
}
val allCategoryIds = listOf(
    Category.Clothes.id,
    Category.Furniture.id,
    Category.Food.id,
    Category.Education.id,
    Category.Electronics.id
)