package com.mcvz.e_commerceapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mcvz.e_commerceapp.fragments.categories.ClothesFragment
import com.mcvz.e_commerceapp.fragments.categories.EducationFragment
import com.mcvz.e_commerceapp.fragments.categories.ElectronicsFragment
import com.mcvz.e_commerceapp.fragments.categories.FoodFragment
import com.mcvz.e_commerceapp.fragments.categories.FurnitureFragment
import com.mcvz.e_commerceapp.fragments.categories.MainCategoryFragment

class ViewPager3Adapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    val fragments = listOf(
        MainCategoryFragment(),
        ClothesFragment(),
        FurnitureFragment(),
        FoodFragment(),
        EducationFragment(),
        ElectronicsFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}