package com.fin_group.aslzar.ui.fragments.sales

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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.SalesProductsV2Adapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentSalesAndPromotionsBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.fragments.sales.functions.addProductToCart
import com.fin_group.aslzar.ui.fragments.sales.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.sales.functions.callOutStock
import com.fin_group.aslzar.ui.fragments.sales.functions.fetchRV
import com.fin_group.aslzar.ui.fragments.sales.functions.filterProducts
import com.fin_group.aslzar.ui.fragments.sales.functions.getAllProductFromPrefs
import com.fin_group.aslzar.ui.fragments.sales.functions.getAllProductsFromApi
import com.fin_group.aslzar.ui.fragments.sales.functions.savingAndFetchSearch
import com.fin_group.aslzar.ui.fragments.sales.functions.searchViewFun
import com.fin_group.aslzar.ui.fragments.sales.functions.updateBadge
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.NoInternetDialogFragment
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class SalesAndPromotionsFragment : Fragment(), ProductOnClickListener {

    private var _binding: FragmentSalesAndPromotionsBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var toolbar: MaterialToolbar

    lateinit var preferences: SharedPreferences

    lateinit var viewSearch: ConstraintLayout
    var searchText: String = ""
    lateinit var searchView: SearchView

    var allProducts: List<ResultX> = emptyList()
    var filteredProducts: List<ResultX> = emptyList()
    lateinit var myAdapter: SalesProductsV2Adapter

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesAndPromotionsBinding.inflate(inflater, container, false)

        NoInternetDialogFragment.showIfNoInternet(requireContext())

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
        val hasInternet = NoInternetDialogFragment.hasInternetConnection(requireContext())

        when (item.itemId) {
            R.id.search_item -> {searchViewFun()}
            R.id.barcode_item -> {
                if (hasInternet){
                    val action = SalesAndPromotionsFragmentDirections.actionSalesAndPromotionsFragmentToBarCodeScannerFragment("SalesProductsBarcode")
                    findNavController().navigate(action)
                } else {
                    NoInternetDialogFragment.showIfNoInternet(requireContext())
                }
            }
            R.id.profile_item -> {
                if (hasInternet){
                    findNavController().navigate(R.id.action_salesAndPromotionsFragment_to_profileFragment)
                } else {
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
        filterProducts()
    }

    override fun addToCart(product: ResultX) {
        addProductToCart(product)    }

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
        val product2 =  ResultX(
            "",
            "",
            "",
            "",
            product.full_name,
            product.id,
            product.img,
            product.is_set,
            "",
            product.name,
            product.price,
            product.proba,
            0,
            "",
            product.types
        )

        val action = SalesAndPromotionsFragmentDirections.actionSalesAndPromotionsFragmentToDataProductFragment(product2.id, product2, "SalesProducts")
        Navigation.findNavController(binding.root).navigate(action)
    }
}