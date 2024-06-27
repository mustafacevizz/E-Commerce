package com.mcvz.e_commerceapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.adapters.BestDealsAdapter
import com.mcvz.e_commerceapp.adapters.HomeViewpagerAdapter
import com.mcvz.e_commerceapp.adapters.SpecialProductsAdapter
import com.mcvz.e_commerceapp.adapters.ViewPager3Adapter
import com.mcvz.e_commerceapp.databinding.FragmentHomeBinding
import com.mcvz.e_commerceapp.fragments.categories.ClothesFragment
import com.mcvz.e_commerceapp.fragments.categories.EducationFragment
import com.mcvz.e_commerceapp.fragments.categories.ElectronicsFragment
import com.mcvz.e_commerceapp.fragments.categories.FoodFragment
import com.mcvz.e_commerceapp.fragments.categories.FurnitureFragment
import com.mcvz.e_commerceapp.fragments.categories.MainCategoryFragment
import kotlin.reflect.typeOf

class HomeFragment:Fragment(R.layout.fragment_home){
    private lateinit var binding:FragmentHomeBinding
    private lateinit var viewPager3Adapter: ViewPager3Adapter
    //private lateinit var specialProductsAdapter: SpecialProductsAdapter
    //private lateinit var bestDealsAdapter: BestDealsAdapter
    //val mainCategoryFragment=MainCategoryFragment()
    private lateinit var viewPager2Adapter:HomeViewpagerAdapter


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
        //specialProductsAdapter = SpecialProductsAdapter(emptyList())
        //bestDealsAdapter = BestDealsAdapter(emptyList())
        //viewPager3Adapter = ViewPager3Adapter(childFragmentManager, lifecycle)
        //mainCategoryFragment.setupSpecialProductsRv(emptyList())

        //binding.viewpagerHome.adapter = viewPager3Adapter

        val categoriesFragments= arrayListOf<Fragment>(
            MainCategoryFragment(),
            ClothesFragment(),
            FurnitureFragment(),
            FoodFragment(),
            EducationFragment(),
            ElectronicsFragment()
        )
        binding.viewpagerHome.isUserInputEnabled=false
        viewPager2Adapter=HomeViewpagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewpagerHome.adapter=viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewpagerHome){ tab,position->
            when(position){
                0->tab.text="Main"
                1->tab.text="Clothes"
                2->tab.text="Furniture"
                3->tab.text="Food"
                4->tab.text="Education"
                5->tab.text="Electronics"
            }
        }.attach()
        //setupSearchView()
    }

    /*private fun setupSearchView() {
        //val fragment:Fragment
        //fragment=childFragmentManager.fragments[binding.viewpagerHome.currentItem-1]
        val fragment=viewPager2Adapter.createFragment(binding.viewpagerHome.currentItem)

        //val fragment = viewPager3Adapter.fragments[binding.viewpagerHome.currentItem]
        val searchBar = view?.findViewById<SearchView>(R.id.searchbar)
        searchBar?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (fragment is MainCategoryFragment ){
                        fragment.filterProducts(it)
                    }
                    else if (fragment is ClothesFragment ){
                        fragment.filterProducts(it)
                    }

                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        if (fragment is MainCategoryFragment) {
                            fragment.filterProducts(it)
                        } else if (fragment is ClothesFragment) {
                            fragment.filterProducts(it)
                        }
                    }

                return false
            }
        })
    }*/
}





