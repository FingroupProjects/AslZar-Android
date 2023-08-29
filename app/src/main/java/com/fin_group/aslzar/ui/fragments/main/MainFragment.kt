package com.fin_group.aslzar.ui.fragments.main

import android.annotation.SuppressLint
import android.content.Context
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
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.models.Category
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.models.ProductV2
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
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
import com.fin_group.aslzar.ui.fragments.main.functions.updateBadge
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class MainFragment : Fragment(), ProductOnClickListener, CategoryClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var toolbar: MaterialToolbar

    lateinit var viewSearch: ConstraintLayout
    var searchText: String = ""
    lateinit var searchView: SearchView

    var allProducts: List<Product> = emptyList()
    var filteredProducts: List<Product> = emptyList()
    lateinit var myAdapter: ProductsAdapter

    lateinit var viewCheckedCategory: ConstraintLayout
    private var allCategories: List<ProductV2> = emptyList()
    var selectCategory: Category? = null

    lateinit var mainActivity: MainActivity
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var badgeManager: BadgeManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
//        toolbar = binding.toolbar
//        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar as MaterialToolbar?)
//        toolbar.title = "Главная"
        setHasOptionsMenu(true)
        viewSearch = binding.viewSearch
        viewCheckedCategory = binding.viewCheckedCategory
//        bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        binding.fabClearSearch.setOnClickListener {
            if (searchText != "") {
                searchView.setQuery("", false)
            }
            viewSearch.visibility = GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = binding.searchViewMain

        mainActivity =
            activity as? MainActivity ?: throw IllegalStateException("Activity is not MainActivity")
//        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)
        //updateBadge()

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
            Product(
                id = "1",
                full_name = "Золотое кольцо с бриллиантом",
                name = "Золотое кольцо",
                price = 500.0,
                category_id = "rings",
                sale = 10.0,
                color = "Золотой",
                stone_type = "Бриллиант",
                metal = "Золото 18K",
                content = "Элегантное золотое кольцо с одним бриллиантом.",
                size = "17",
                weight = "3 г",
                country_of_origin = "Италия",
                provider = "Luxe Jewelry",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 20, 5),
                    InStock("Региональный склад", "Санкт-Петербург", 15, 3)
                ),
                img = listOf("link_to_image_1")
            ),
            Product(
                id = "2",
                full_name = "Серебряное колье с жемчугом",
                name = "Серебряное колье",
                price = 150.0,
                category_id = "necklaces",
                sale = 20.0,
                color = "Серебряный",
                stone_type = "Жемчуг",
                metal = "Серебро 925",
                content = "Прекрасное серебряное колье с подвеской из жемчуга.",
                size = "Универсальный",
                weight = "8 г",
                country_of_origin = "Франция",
                provider = "Chic Accessories",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 30, 8),
                    InStock("Региональный склад", "Екатеринбург", 25, 6)
                ),
                img = listOf("link_to_image_2")
            ),
            Product(
                id = "5",
                full_name = "Серьги из белого золота с сапфирами",
                name = "Серьги из белого золота",
                price = 750.0,
                category_id = "earrings",
                sale = 10.0,
                color = "Белое золото",
                stone_type = "Сапфир",
                metal = "Белое золото 14K",
                content = "Изысканные серьги из белого золота с вставками из сапфиров.",
                size = "Маленький",
                weight = "2 г",
                country_of_origin = "Швейцария",
                provider = "Luxury Gems",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 15, 4),
                    InStock("Региональный склад", "Санкт-Петербург", 10, 2)
                ),
                img = listOf("link_to_image_5")
            ),
            Product(
                id = "6",
                full_name = "Браслет с бриллиантами и изумрудами",
                name = "Браслет с бриллиантами",
                price = 1200.0,
                category_id = "bracelets",
                sale = 8.0,
                color = "Золотой",
                stone_type = "Бриллиант, изумруд",
                metal = "Золото 18K",
                content = "Роскошный браслет с бриллиантами и изумрудами, идеально подходящий для особых случаев.",
                size = "Стандартный",
                weight = "10 г",
                country_of_origin = "Франция",
                provider = "Jewel Emporium",
                counts = emptyList(),
                img = listOf("link_to_image_6")
            )
        )
        fetchRV(allProducts)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchRV(productList: List<Product>) {
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
        when (item.itemId) {
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

    private fun categoryDialog() {
        val categoryDialog = CheckCategoryFragmentDialog()
        categoryDialog.setCategoryClickListener(this)
        categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
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

    override fun onStart() {
        super.onStart()
        Cart.loadCartFromPrefs(requireContext())
        Cart.notifyObservers()
//        showToolBar()
//        showBottomNav()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        badgeManager = BadgeManager(requireContext())
    }


    override fun onResume() {
        super.onResume()
        bottomNavigationView = mainActivity.findViewById(R.id.bottomNavigationView)
        updateBadge()
    }

    override fun addToCart(product: Product) {
        addProductToCart(product)
    }

    override fun inStock(product: Product) {
        if (product.counts.isNotEmpty()) {
            callInStockDialog(product.id)
        } else {
            callOutStock(product.id)
        }
    }

    override fun onCategorySelected(selectedCategory: Category) {
        selectCategory = selectedCategory
        binding.apply {
            if (!searchBarChecked(viewSearch)) {
                viewSearch.visibility = GONE
            }
            materialCardViewCategory.setOnClickListener {
                categoryDialog()
            }
            if (selectedCategory.id == "all") {
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
