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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.fin_group.aslzar.databinding.FragmentNewProductsBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.fragments.new_products.functions.addProductToCart
import com.fin_group.aslzar.ui.fragments.new_products.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.new_products.functions.callOutStock
import com.fin_group.aslzar.ui.fragments.new_products.functions.fetchRV
import com.fin_group.aslzar.ui.fragments.new_products.functions.filterProducts
import com.fin_group.aslzar.ui.fragments.new_products.functions.getAllCategoriesFromApi
import com.fin_group.aslzar.ui.fragments.new_products.functions.getAllCategoriesPrefs
import com.fin_group.aslzar.ui.fragments.new_products.functions.getAllProductFromPrefs
import com.fin_group.aslzar.ui.fragments.new_products.functions.getAllProductsFromApi
import com.fin_group.aslzar.ui.fragments.new_products.functions.savingAndFetchSearch
import com.fin_group.aslzar.ui.fragments.new_products.functions.savingAndFetchingFilter
import com.fin_group.aslzar.ui.fragments.new_products.functions.searchViewFun
import com.fin_group.aslzar.ui.fragments.new_products.functions.setFilterViewModel
import com.fin_group.aslzar.ui.fragments.new_products.functions.showAddingToCartDialog
import com.fin_group.aslzar.ui.fragments.new_products.functions.updateBadge
import com.fin_group.aslzar.util.AddingProduct
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.FilialListener
import com.fin_group.aslzar.util.FilterViewModel
import com.fin_group.aslzar.util.NoInternetDialogFragment
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class NewProductsFragment : Fragment(), ProductOnClickListener, AddingProduct, FilialListener {

    private var _binding: FragmentNewProductsBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var toolbar: MaterialToolbar
    lateinit var preferences: SharedPreferences

    lateinit var viewSearch: ConstraintLayout
    var searchText: String = ""
    lateinit var searchView: SearchView

    var allProducts: List<ResultX> = emptyList()
    var filteredProducts: List<ResultX> = emptyList()
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
    var backPressedTime: Long = 0

    lateinit var filterViewModel: FilterViewModel
    var filterModel: FilterModel? = null
    var defaultFilterModel: FilterModel? = null

    lateinit var checkedFiltersTv: TextView
    lateinit var errorTv: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewProductsBinding.inflate(inflater, container, false)
        filterViewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]
        defaultFilterModel = filterViewModel.defaultFilterModel
        checkedFiltersTv = binding.checkedFiltersTv
        errorTv = binding.textView47

        preferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        setHasOptionsMenu(true)
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        swipeRefreshLayout = binding.swipeRefreshLayout
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

        NoInternetDialogFragment.showIfNoInternet(requireContext())

        getAllCategoriesPrefs()
        getAllProductFromPrefs()

        onBackPressed()

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
                filterModel = updatedFilterModel
                selectCategory = updatedFilterModel.category
                savingAndFetchingFilter(binding)
                defaultFilterModel = filterViewModel.defaultFilterModel

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sales_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val hasInternet = NoInternetDialogFragment.hasInternetConnection(requireContext())

        when (item.itemId) {
            R.id.search_item -> {
                if (allProducts.isNotEmpty()){
                    searchViewFun()
                } else {
                    Toast.makeText(requireContext(), "Невозможно искать из пустого списка", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.filter_item -> {
                if (allProducts.isNotEmpty()){
                    setFilterViewModel()
                } else {
                    Toast.makeText(requireContext(), "Невозможно отфильтровать пустой список", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.barcode_item -> {
                if (hasInternet){
                    val action = NewProductsFragmentDirections.actionNewProductsFragmentToBarCodeScannerFragment("NewProductsBarcode")
                    findNavController().navigate(action)
                } else {
                    NoInternetDialogFragment.showIfNoInternet(requireContext())
                }
            }
            R.id.profile_item -> {
                findNavController().navigate(R.id.action_newProductsFragment_to_profileFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        badgeManager = BadgeManager(requireContext(), "badge_cart_prefs")
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

    override fun onResume() {
        super.onResume()
        bottomNavigationView = mainActivity.findViewById(R.id.bottomNavigationView)
        updateBadge()
    }

    override fun onDestroy() {
        super.onDestroy()
        preferences.edit()?.putBoolean("first_run", true)?.apply()
    }
    private fun fetchDataAndFilterProducts() {
        getAllProductsFromApi()
        getAllCategoriesFromApi()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Нажмите еще раз чтобы выйти.", Toast.LENGTH_LONG).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            })
    }

    override fun addToCart(product: ResultX) {
        val newFilterModel = FilterModel(
            0,
            100000000,
            0,
            10000,
            0,
            10000,
            Category("all", "Все")
        )
        if (filterModel != null){
            showAddingToCartDialog(product, filterModel!!)
        } else {
            showAddingToCartDialog(product, newFilterModel)
        }
    }

    override fun inStock(product: ResultX) {
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


    override fun getData(product: ResultX) {

        val product2 = ResultX(
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
            product.types,
        )

        val action = NewProductsFragmentDirections.actionNewProductsFragmentToDataProductFragment(
            product2.id,
            product2,
            "NewProducts"
        )
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun addProduct(product: ResultX, type: Type, count: Count) {
        Toast.makeText(requireContext(), "Товар добавлен в корзину: ${product.full_name}", Toast.LENGTH_SHORT).show()
        sharedViewModel.onProductAddedToCartV2(product, requireContext(), type, count)
        updateBadge()
    }

    override fun addFilial(product: ResultX, type: Type, filial: Count) {
        Toast.makeText(requireContext(), "Товар добавлен в корзину: ${product.full_name}", Toast.LENGTH_SHORT).show()
        sharedViewModel.onProductAddedToCartV2(product, requireContext(), type, filial)
        updateBadge()
    }
}