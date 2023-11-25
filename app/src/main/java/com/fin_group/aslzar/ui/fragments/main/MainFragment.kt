package com.fin_group.aslzar.ui.fragments.main

import android.annotation.SuppressLint
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
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.cipher.EncryptionManager
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.ResultXV2
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.fragments.main.functions.addProductToCart
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.ui.fragments.main.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.main.functions.callOutStock
import com.fin_group.aslzar.ui.fragments.main.functions.fetchRV
import com.fin_group.aslzar.ui.fragments.main.functions.filterProducts
import com.fin_group.aslzar.ui.fragments.main.functions.filterProducts2
import com.fin_group.aslzar.ui.fragments.main.functions.getAllCategoriesFromApi
import com.fin_group.aslzar.ui.fragments.main.functions.getAllCategoriesPrefs
import com.fin_group.aslzar.ui.fragments.main.functions.getAllProductFromPrefs
import com.fin_group.aslzar.ui.fragments.main.functions.getAllProductsFromApi
import com.fin_group.aslzar.ui.fragments.main.functions.savingAndFetchSearch
import com.fin_group.aslzar.ui.fragments.main.functions.savingAndFetchingCategory
import com.fin_group.aslzar.ui.fragments.main.functions.searchViewFun
import com.fin_group.aslzar.ui.fragments.main.functions.setFilterViewModel
import com.fin_group.aslzar.ui.fragments.main.functions.updateBadge
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.FilterDialogListener
import com.fin_group.aslzar.util.FilterViewModel
import com.fin_group.aslzar.util.NoInternetDialogFragment
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class MainFragment : Fragment(), ProductOnClickListener, CategoryClickListener,
    FilterDialogListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var toolbar: MaterialToolbar

    lateinit var preferences: SharedPreferences

    lateinit var viewSearch: ConstraintLayout
    var searchText: String = ""
    lateinit var searchView: SearchView

    var allProducts: List<ResultXV2> = emptyList()
    var filteredProducts: List<ResultXV2> = emptyList()
    lateinit var myAdapter: ProductsAdapter

    lateinit var viewCheckedCategory: ConstraintLayout
    var allCategories: List<Category> = emptyList()
    var selectCategory: Category? = null

    private lateinit var mainActivity: MainActivity
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var badgeManager: BadgeManager
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var sessionManager: SessionManager
    lateinit var apiService: ApiClient

    lateinit var recyclerView: RecyclerView

    lateinit var encryptionManager: EncryptionManager

    lateinit var filterViewModel: FilterViewModel
    var filterModel: FilterModel ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        filterViewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]

//        filterModel = filterViewModel.filterModel

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
            viewSearch.visibility = GONE
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

        NoInternetDialogFragment.showIfNoInternet(requireContext())

        getAllCategoriesPrefs()
        getAllProductFromPrefs()

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
        savingAndFetchSearch(binding)
        fetchRV(allProducts)
        val selectedCategoryId = preferences.getString("selectedCategory", "all")
        selectCategory = allCategories.find { it.id == selectedCategoryId }


        filterViewModel.filterChangeListener.observe(viewLifecycleOwner) { newFilterModel ->
            newFilterModel?.let { updatedFilterModel ->
                Toast.makeText(requireContext(),"${updatedFilterModel.category}",Toast.LENGTH_SHORT).show()
                Log.d("TAG", "onFilterChanged: $updatedFilterModel")

                filterModel = updatedFilterModel
                selectCategory = updatedFilterModel.category
                savingAndFetchingCategory(binding, filterModel!!)
            }
        }
    }

    fun hideCategoryView() {
        viewCheckedCategory.visibility = GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val hasInternet = NoInternetDialogFragment.hasInternetConnection(requireContext())

        when (item.itemId) {
            R.id.search_item -> {
                searchViewFun()
            }
            R.id.filter_item -> {
                if (hasInternet){
                    setFilterViewModel()
                } else {
                    NoInternetDialogFragment.showIfNoInternet(requireContext())
                }
            }
            R.id.barcode_item -> {
                if (hasInternet) {
                    val action =
                        MainFragmentDirections.actionMainFragmentToBarCodeScannerFragment("MainBarcode")
                    findNavController().navigate(action)
                } else {
                    NoInternetDialogFragment.showIfNoInternet(requireContext())
                }
            }
            R.id.profile_item -> {
                if (hasInternet) {
                    findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
                }
                else {
                    NoInternetDialogFragment.showIfNoInternet(requireContext())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        Cart.saveCartToPrefs(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Cart.saveCartToPrefs(requireContext())
        _binding = null
        preferences.edit()?.putBoolean("first_run", false)?.apply()
    }

    override fun onStart() {
        super.onStart()
        val firstRun = preferences.getBoolean("first_run", true)
        if (firstRun){
            viewCheckedCategory.visibility = GONE
        } else {
            savingAndFetchingCategory(binding, filterViewModel.filterModel!!)
        }
        savingAndFetchSearch(binding)
        Cart.loadCartFromPrefs(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        badgeManager = BadgeManager(requireContext(), "badge_cart_prefs")
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView = mainActivity.findViewById(R.id.bottomNavigationView)
        updateBadge()
    }


    private fun fetchDataAndFilterProducts() {
        getAllProductsFromApi()
        getAllCategoriesFromApi()
        filterProducts()
        filterProducts2(filterViewModel.filterModel!!)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCategorySelected(selectedCategory: Category) {
        selectCategory = selectedCategory
        preferences.edit()?.putString("selectedCategory", selectedCategory.id)?.apply()
//        savingAndFetchingCategory(binding)
    }

    override fun onFilterApplied(updatedFilterModel: FilterModel) {
        Log.d("TAG", "onFilterApplied: $updatedFilterModel")
        Toast.makeText(requireContext(), "$updatedFilterModel", Toast.LENGTH_SHORT).show()
    }

    override fun addToCart(product: ResultXV2) {
        addProductToCart(product)
    }

    override fun inStock(product: ResultXV2) {
        if (product.types.isNotEmpty()) {
            for (type in product.types) {
                if (type.counts.isNotEmpty()) {
                    callInStockDialog(product.full_name, type.counts)
                    return
                }
            }
        }
        callOutStock(product.id)
    }

    override fun getData(product: ResultXV2) {

        val product2 = ResultXV2(
            product.barcode,
            product.category_id,
            product.color,
            product.description,
            product.full_name,
            product.id,
            product.img,
            product.is_set,
            product.metal,
            product.name,
            product.price,
            product.proba,
            product.sale,
            product.stone_type,
            product.types
        )

        val action = MainFragmentDirections.actionMainFragmentToDataProductFragment(product2.id, product2, "Main")
        Navigation.findNavController(binding.root).navigate(action)
    }
}
