package com.mcvz.e_commerceapp.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.mcvz.e_commerceapp.R
import com.mcvz.e_commerceapp.adapters.AllProductForSearchAdapter
import com.mcvz.e_commerceapp.data.Product
import com.mcvz.e_commerceapp.databinding.FragmentSearchBinding
import com.mcvz.e_commerceapp.fragments.TAG
import com.mcvz.e_commerceapp.util.Resource
import com.mcvz.e_commerceapp.viewmodel.MainCategoryViewModel
import com.mcvz.e_commerceapp.viewmodel.SearchViewModal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {
    private lateinit var binding:FragmentSearchBinding
    private lateinit var allProductForSearchAdapter:AllProductForSearchAdapter
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val productList = ArrayList<Product>()
    private val searchList = ArrayList<Product>()
    //private val viewModel by viewModels<SearchViewModal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentSearchBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        allProductForSearchAdapter=AllProductForSearchAdapter(productList,this)

        binding.searchRecyclerView.apply {
            adapter=allProductForSearchAdapter
            layoutManager=GridLayoutManager(requireContext(),2)
        }
        getData()

        /*lifecycleScope.launchWhenStarted {
            viewModel.allProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        //showLoading()
                    }
                    is Resource.Success->{
                        allProductForSearchAdapter.notifyDataSetChanged()
                        //hideLoading()
                    }
                    is Resource.Error->{
                        //hideLoading()
                        //Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }else->Unit

                }
            }
        }*/
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //Log.d("SearchFragment", "Query Text Change: $p0")
                filterList(p0)
                return true
            }

        })


    }

    fun getData(){
        firestore.collection("Products").get().addOnSuccessListener {result->
            val products=result.toObjects<Product>()
            productList.clear()
            productList.addAll(products)
            allProductForSearchAdapter.notifyDataSetChanged()

        }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
        }

    private fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            allProductForSearchAdapter.searchDataList(productList)
        } else {
            val filteredList = productList.filter {
                it.name?.lowercase()?.contains(query.lowercase()) ?: false
            }
            allProductForSearchAdapter.searchDataList(filteredList)
        }
        //Log.d("SearchFragment", "Filtered List: ${searchList.size} items")
        //allProductForSearchAdapter.notifyDataSetChanged()
    }
}



