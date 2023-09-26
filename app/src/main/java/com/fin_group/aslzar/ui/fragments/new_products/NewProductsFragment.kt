package com.fin_group.aslzar.ui.fragments.new_products

import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentNewProductsBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class NewProductsFragment : Fragment(), ProductOnClickListener, CategoryClickListener {

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

        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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