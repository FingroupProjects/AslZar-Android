package com.fin_group.aslzar.ui.fragments.new_products

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentNewProductsBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.fragments.main.MainFragmentDirections
import com.fin_group.aslzar.ui.fragments.new_products.functions.addProductToCart
import com.fin_group.aslzar.ui.fragments.new_products.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.new_products.functions.callOutStock
import com.fin_group.aslzar.ui.fragments.new_products.functions.fetchRV
import com.fin_group.aslzar.ui.fragments.new_products.functions.filterProducts
import com.fin_group.aslzar.ui.fragments.new_products.functions.getAllCategoriesFromApi
import com.fin_group.aslzar.ui.fragments.new_products.functions.getAllProductFromPrefs
import com.fin_group.aslzar.ui.fragments.new_products.functions.getAllProductsFromApi
import com.fin_group.aslzar.ui.fragments.new_products.functions.savingAndFetchSearch
import com.fin_group.aslzar.ui.fragments.new_products.functions.searchViewFun
import com.fin_group.aslzar.ui.fragments.new_products.functions.updateBadge
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class NewProductsFragment : Fragment(), ProductOnClickListener {

    private var _binding: FragmentNewProductsBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var toolbar: MaterialToolbar

    lateinit var preferences: SharedPreferences

    lateinit var viewSearch: ConstraintLayout
    var searchText: String = ""
    lateinit var searchView: SearchView

    var allProducts: List<Product> = emptyList()
    var filteredProducts: List<Product> = emptyList()
    lateinit var myAdapter: ProductsAdapter

    lateinit var viewCheckedCategory: ConstraintLayout
    var allCategories: List<Category> = emptyList()
    var selectCategory: Category? = null

    lateinit var mainActivity: MainActivity
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var badgeManager: BadgeManager
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var sessionManager: SessionManager
    lateinit var apiService: ApiClient

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewProductsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        preferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        swipeRefreshLayout = binding.swipeRefreshLayout
        setHasOptionsMenu(true)
        viewSearch = binding.viewSearch
        viewCheckedCategory = binding.viewCheckedCategory
        recyclerView = binding.mainRecyclerView

        binding.fabClearSearch.setOnClickListener {
            if (searchText != "") {
                searchView.setQuery("", false)
            }
            viewSearch.visibility = View.GONE
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchDataAndFilterProducts()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = binding.searchViewMain
        mainActivity = activity as? MainActivity ?: throw IllegalStateException("Activity is not MainActivity")
        getAllProductFromPrefs()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText.toString()
                Log.d("TAG", "onQueryTextChange: $searchText")
                filterProducts()
                return true
            }
        })
        savingAndFetchSearch(binding)
        fetchRV(allProducts)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sales_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_item -> {
                searchViewFun()
            }
            R.id.barcode_item -> {
                findNavController().navigate(R.id.action_newProductsFragment_to_barCodeScannerFragment)
            }
            R.id.profile_item -> {
                findNavController().navigate(R.id.action_newProductsFragment_to_profileFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        badgeManager = BadgeManager(requireContext())
    }

    override fun inStock(product: Product) {
        if (product.counts.isNotEmpty()) {
            callInStockDialog(product.full_name, product.counts)
        } else {
            callOutStock(product.id)
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView = mainActivity.findViewById(R.id.bottomNavigationView)
        updateBadge()
    }

    override fun onPause() {
        super.onPause()
        Cart.saveCartToPrefs(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Cart.saveCartToPrefs(requireContext())
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        preferences.edit()?.putBoolean("first_run", true)?.apply()
    }

    override fun addToCart(product: Product) {
        addProductToCart(product)
    }

    override fun getData(product: Product) {
        val action = NewProductsFragmentDirections.actionNewProductsFragmentToDataProductFragment(product.id, product)
        Navigation.findNavController(binding.root).navigate(action)
    }

    private fun fetchDataAndFilterProducts() {
        getAllProductsFromApi()
        filterProducts()
    }
}