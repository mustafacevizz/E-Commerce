package com.mcvz.e_commerceapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.adapters.HomeViewpagerAdapter
import com.mcvz.e_commerceapp.databinding.FragmentHomeBinding
import com.mcvz.e_commerceapp.fragments.categories.ClothesFragment
import com.mcvz.e_commerceapp.fragments.categories.EducationFragment
import com.mcvz.e_commerceapp.fragments.categories.ElectronicsFragment
import com.mcvz.e_commerceapp.fragments.categories.FoodFragment
import com.mcvz.e_commerceapp.fragments.categories.ForYouFragment
import com.mcvz.e_commerceapp.fragments.categories.FurnitureFragment
import com.mcvz.e_commerceapp.fragments.categories.MainCategoryFragment

class HomeFragment:Fragment(R.layout.fragment_home) {
    private lateinit var binding:FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesFragments= arrayListOf<Fragment>(
            ForYouFragment(),
            MainCategoryFragment(),
            ClothesFragment(),
            FurnitureFragment(),
            FoodFragment(),
            EducationFragment(),
            ElectronicsFragment()
        )
        binding.viewpagerHome.isUserInputEnabled=false

        val viewPager2Adapter=HomeViewpagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewpagerHome.adapter=viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewpagerHome){ tab,position->
            when(position){
                0->tab.text="For You"
                1->tab.text="Main"
                2->tab.text="Clothes"
                3->tab.text="Furniture"
                4->tab.text="Food"
                5->tab.text="Education"
                6->tab.text="Electronics"
            }
        }.attach()
    }

}