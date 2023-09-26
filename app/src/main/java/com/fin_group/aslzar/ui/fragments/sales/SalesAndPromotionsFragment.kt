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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentSalesAndPromotionsBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.fragments.main.MainFragmentDirections
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class SalesAndPromotionsFragment : Fragment(), ProductOnClickListener, CategoryClickListener {

    private var _binding: FragmentSalesAndPromotionsBinding? = null
    private val binding get() = _binding!!

    lateinit var preferences: SharedPreferences

    lateinit var viewSearch: ConstraintLayout
    var searchText: String = ""
    lateinit var searchView: SearchView

    var allProducts: List<Product> = emptyList()
    var filteredProducts: List<Product> = emptyList()

    lateinit var sessionManager: SessionManager
    lateinit var apiService: ApiClient

    lateinit var mainActivity: MainActivity
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var badgeManager: BadgeManager
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesAndPromotionsBinding.inflate(inflater, container, false)

        preferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        swipeRefreshLayout = binding.swipeRefreshLayout
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity =
            activity as? MainActivity ?: throw IllegalStateException("Activity is not MainActivity")

//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                searchText = newText.toString()
//                Log.d("TAG", "onQueryTextChange: $searchText")
////                filterProducts()
//                return true
//            }
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_item -> {
                Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show()
//                searchViewFun()
            }

            R.id.filter_item -> {
//                filterFun()
                Toast.makeText(requireContext(), "Category", Toast.LENGTH_SHORT).show()
            }

            R.id.barcode_item -> {

            }

            R.id.profile_item -> {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        badgeManager = BadgeManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView = mainActivity.findViewById(R.id.bottomNavigationView)
//        updateBadge()
    }

    override fun onDestroy() {
        super.onDestroy()
        preferences.edit()?.putBoolean("first_run", true)?.apply()
    }

    override fun addToCart(product: Product) {
        TODO("Not yet implemented")
    }

    override fun inStock(product: Product) {
        TODO("Not yet implemented")
    }

    override fun getData(product: Product) {
        TODO("Not yet implemented")
    }

    override fun onCategorySelected(selectedCategory: Category) {
        TODO("Not yet implemented")
    }
}