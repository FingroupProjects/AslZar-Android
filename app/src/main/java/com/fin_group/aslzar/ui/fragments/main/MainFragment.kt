package com.fin_group.aslzar.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.models.Category
import com.fin_group.aslzar.models.Product
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.ui.fragments.main.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.main.functions.callOutStock
import com.fin_group.aslzar.ui.fragments.main.functions.filterFun
import com.fin_group.aslzar.ui.fragments.main.functions.filterProducts
import com.fin_group.aslzar.ui.fragments.main.functions.searchBarChecked
import com.fin_group.aslzar.ui.fragments.main.functions.searchViewFun
import com.fin_group.aslzar.util.showBottomNav
import com.fin_group.aslzar.util.showToolBar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class MainFragment : Fragment(), ProductOnClickListener, CategoryClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar
    lateinit var viewSearch: ConstraintLayout
    lateinit var viewCheckedCategory: ConstraintLayout

    var allProducts: List<Product> = emptyList()
    var filteredProducts: List<Product> = emptyList()
    private var allCategories: List<Product> = emptyList()

    var selectCategory: Category? = null

    var searchText: String = ""
    lateinit var searchView: SearchView

    lateinit var myAdapter: ProductsAdapter

    //add Toxa

    private lateinit var notificationBadge: View
    private var count = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        viewSearch = binding.viewSearch
        viewCheckedCategory = binding.viewCheckedCategory

        binding.fabClearSearch.setOnClickListener {
            searchText = ""
            viewSearch.visibility = GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = binding.searchViewMain

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText.toString()
                filterProducts()
                return true
            }
        })

        allProducts = listOf(
            Product("1", "Серьги золотые с золотом", "", "2.7.5.1.066.1_1,6_0", 0, "00001"),
            Product("2", "Серьги золотые с печеньками", "", "2.7.3.1.096.1_7,8_0", 15, "00002"),
            Product("3", "Серьги золотые с кошками", "https://cdn2.thecatapi.com/images/9gg.jpg", "2.7.2.1.096.1_5,9_0", 10, "00002"),
            Product("4", "Серьги золотые с водичкой", "", "2.7.2.1.096.1_7,2_0", 18, "00001"),
            Product("5", "Серьги золотые с собой", "", "2.7.5.1.066.1_1,6_0", 0, "00003"),
            Product("6", "Серьги золотые с компом", "", "2.7.3.1.096.1_7,8_0", 15, "00003"),
            Product("7", "Серьги золотые с кольцом", "", "2.7.2.1.096.1_5,9_0", 10, "00001"),
            Product("8", "Серьги золотые с бриллиантами", "", "2.7.2.1.096.1_7,2_0", 18, "00001")
        )
        fetchRV(allProducts)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchRV(productList: List<Product>){
        val recyclerView = binding.mainRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        myAdapter = ProductsAdapter(productList, this)
        recyclerView.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.search_item -> {
                searchViewFun()
            }
            R.id.filter_item -> {
                filterFun()
            }
            R.id.barcode_item -> {
                val action = MainFragmentDirections.actionMainFragmentToBarCodeScannerFragment()
                findNavController().navigate(action)
            }
            R.id.profile_item -> {
                findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun categoryDialog(){
        val categoryDialog = CheckCategoryFragmentDialog()
        categoryDialog.setCategoryClickListener(this)
        categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
    }

    override fun onStart() {
        super.onStart()
        showToolBar()
        showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun addToCart(product: Product) {
        Toast.makeText(requireContext(), "Добавление в корзину ${product.code}", Toast.LENGTH_SHORT).show()
    }
    override fun inStock(product: Product) {
        if (product.count > 0) {
            callInStockDialog(product.id)
        } else {
            callOutStock(product.id)
        }
    }

    override fun onCategorySelected(selectedCategory: Category) {
        selectCategory = selectedCategory
        binding.apply {
            if (!searchBarChecked(viewSearch)){
                viewSearch.visibility = GONE
            }
            materialCardViewCategory.setOnClickListener {
                categoryDialog()
            }
            if (selectedCategory.id == "all"){
                viewCheckedCategory.visibility = GONE
            }
            fabClearCategory.setOnClickListener {
                viewCheckedCategory.visibility = GONE
                selectCategory = null
                filterProducts()
            }
            viewCheckedCategory.visibility = VISIBLE
            checkedCategoryTv.text = selectedCategory.name
        }
        filterProducts()
    }
}
