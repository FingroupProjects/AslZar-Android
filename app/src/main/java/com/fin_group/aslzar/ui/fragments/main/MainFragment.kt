package com.fin_group.aslzar.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.models.Category
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.models.ProductV2
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.fragments.main.functions.addProductToCart
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
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class MainFragment : Fragment(), ProductOnClickListener, CategoryClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var viewSearch: ConstraintLayout
    var searchText: String = ""
    lateinit var searchView: SearchView

    var allProducts: List<ProductV2> = emptyList()
    var filteredProducts: List<ProductV2> = emptyList()
    lateinit var myAdapter: ProductsAdapter

    lateinit var viewCheckedCategory: ConstraintLayout
    private var allCategories: List<ProductV2> = emptyList()
    var selectCategory: Category? = null

    //add Toxa

    private lateinit var notificationBadge: View

    lateinit var mainActivity: MainActivity

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        viewSearch = binding.viewSearch
        viewCheckedCategory = binding.viewCheckedCategory
//        bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        binding.fabClearSearch.setOnClickListener {
            if (searchText != ""){
                searchView.setQuery("", false)
            }
            viewSearch.visibility = GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = binding.searchViewMain

        mainActivity = activity as MainActivity

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
            ProductV2("00-00000001", "Кольцо золотое с бриллиантом", 893, listOf<String>("hello", "hi"), "2.7.5.4.012.1_1,6_0", "00001", 22.9,"Hello", 1225.0 ),
            ProductV2("00-00000002", "Кольцо золотое с фианитом", 433, listOf<String>("hello", "hi"), "2.7.5.2.045.4_1,6_0", "00001", 22.9,"Hello", 1225.0 ),
            ProductV2("00-00000003", "Кольцо золотое с бриллиантом", 753, listOf<String>("hello", "hi"), "2.7.5.6.056.3_1,6_0", "00001", 22.9,"Hello", 1225.0 ),
            ProductV2("00-00000004", "Кольцо золотое с бриллиантом", 823, listOf<String>("hello", "hi"), "2.7.5.1.022.2_1,6_0", "00001", 22.9,"Hello", 1225.0 ),
        )
        fetchRV(allProducts)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchRV(productList: List<ProductV2>){
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

    override fun onResume() {
        super.onResume()
        bottomNavigationView = mainActivity.findViewById(R.id.bottomNavigationView)
    }

    private fun onProductAddedToCart(product: ProductInCart) {
        sharedViewModel.onProductAddedToCart(product)
    }

    override fun addToCart(product: ProductV2) {
        addProductToCart(bottomNavigationView, product)
    }
    override fun inStock(product: ProductV2) {
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
