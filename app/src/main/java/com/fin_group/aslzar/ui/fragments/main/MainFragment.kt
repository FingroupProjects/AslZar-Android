package com.fin_group.aslzar.ui.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
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
import com.fin_group.aslzar.ui.fragments.main.functions.categoryDialog
import com.fin_group.aslzar.ui.fragments.main.functions.filterFun
import com.fin_group.aslzar.ui.fragments.main.functions.filterProducts
import com.fin_group.aslzar.ui.fragments.main.functions.savingAndFetchingCategory
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        preferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!

//        toolbar = binding.toolbar
//        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar as MaterialToolbar?)
//        toolbar.title = "Главная"
        setHasOptionsMenu(true)
        viewSearch = binding.viewSearch
        viewCheckedCategory = binding.viewCheckedCategory

        binding.fabClearSearch.setOnClickListener {
            if (searchText != "") {
                searchView.setQuery("", false)
            }
            viewSearch.visibility = GONE
        }

        allCategories = listOf(
            Category("all", "Все"),
            Category("00001", "Кольца"),
            Category("00002", "Серьги"),
            Category("00003", "Ожерелья"),
            Category("00004", "Браслеты"),
            Category("00005", "Подвески"),
            Category("00006", "Часы"),
        )



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = binding.searchViewMain

        mainActivity = activity as? MainActivity ?: throw IllegalStateException("Activity is not MainActivity")


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
                id = "00001",
                full_name = "Золотое кольцо с бриллиантом",
                name = "(2.7.1.1.101.2_5,93_0)",
                price = 3288000,
                category_id = "00001",
                sale = 1.99,
                color = "Золотой",
                stone_type = "Бриллиант",
                metal = "Золото 18K",
                content = "965",
                size = "17",
                weight = "3 г",
                country_of_origin = "Италия",
                provider = "Luxe Jewelry",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 20, 5),
                    InStock("Региональный склад", "Санкт-Петербург", 15, 3)
                ),
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/9NPXVyGioXIMThrrxWCOa2dUC1WUlTOvwUSk3Frb.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/oB9W5AC6jBQeFScqr8YFjRs81tCekLKYRe8cHSrH.png"
                )
            ),
            Product(
                id = "00002",
                full_name = "Серебряное колье с жемчугом",
                name = "(1.5.8.1.566.1_3,5_0)",
                price = 8409000,
                category_id = "00003",
                sale = 1.8,
                color = "Серебряный",
                stone_type = "Жемчуг",
                metal = "Серебро 925",
                content = "885.",
                size = "Универсальный",
                weight = "8 г",
                country_of_origin = "Франция",
                provider = "Chic Accessories",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 30, 8),
                    InStock("Региональный склад", "Екатеринбург", 25, 6)
                ),
                img = listOf("http://convertolink.taskpro.tj/photoLink/public/storage/images/GBGXaJxO3lXNbEylCSpKRfSqjRQe8jLnWU4dQsKw.png")
            ),
            Product(
                id = "00002",
                full_name = "Серебряное колье с жемчугом",
                name = "(1.5.8.1.566.1_3,5_0)",
                price = 8409000,
                category_id = "00003",
                sale = 0,
                color = "Серебряный",
                stone_type = "Жемчуг",
                metal = "Серебро 925",
                content = "885.",
                size = "Универсальный",
                weight = "8 г",
                country_of_origin = "Франция",
                provider = "Chic Accessories",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 30, 8),
                    InStock("Региональный склад", "Екатеринбург", 25, 6)
                ),
                img = listOf("http://convertolink.taskpro.tj/photoLink/public/storage/images/STEHglPiZLEQ9XaVP9YUOIBgraScFtwrRuHrRQyJ.png", "http://convertolink.taskpro.tj/photoLink/public/storage/images/Ytlqax4RbuJKmXtV2sZQkO5wSsCXtevUaomQD00T.png")
            ),
            Product(
                id = "00005",
                full_name = "Серьги из белого золота с фианитами",
                name = "(1.4.2.1.349.1_18)",
                price = 1250000,
                category_id = "00002",
                sale = 0,
                color = "Белое золото",
                stone_type = "Сапфир",
                metal = "Белое золото 14K",
                content = "585",
                size = "Маленький",
                weight = "2 г",
                country_of_origin = "Швейцария",
                provider = "Luxury Gems",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 15, 4),
                    InStock("Региональный склад", "Санкт-Петербург", 10, 2)
                ),
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/tZHUKuHG8m5KFIk4Gy5oKc1x3gwzwes6yighLta6.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/cuLXKVveSqWQpdqKbF07W946O8gqNUYgcUcXVJrl.png"
                )
            ),
            Product(
                id = "00006",
                full_name = "Браслет золотой с фианитами",
                name = "(1.8.6.0.033.1_13,01_0)",
                price = 890000.0,
                category_id = "00004",
                sale = 8.0,
                color = "Золотой",
                stone_type = "Бриллиант, изумруд",
                metal = "Золото 18K",
                content = "585",
                size = "Стандартный",
                weight = "10 г",
                country_of_origin = "Франция",
                provider = "Jewel Emporium",
                counts = emptyList(),
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/0rrbEflHrMda1P4y5ulSfurSK0C8Fy6ZS8Z8f5bF.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/7HwYLvcR5CSUYmp5rbdpjDzus9VwZQN8aZkjdz7O.png"
                )
            ),
            Product(
                id = "00007",
                full_name = "Серьги золотые «Цветок океана»",
                name = "(1.5.2.1.560.1_7,31_0)",
                price = 6780000 ,
                category_id = "00002",
                sale = 2.0,
                color = "Золотой",
                stone_type = "Бриллиант, изумруд",
                metal = "Золото 18K",
                content = "585",
                size = "Стандартный",
                weight = "10 г",
                country_of_origin = "Франция",
                provider = "Jewel Emporium",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 15, 4),
                    InStock("Региональный склад", "Санкт-Петербург", 10, 2)
                ),
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/WOS6fpsutw3paVS6PNFIrezsox5jOi7KFaorsrd7.png",
                )
            ),
            Product(
                id = "00008",
                full_name = "Цепочка золотая с плоскими звеньями",
                name = "(2.7.4.1.008.1_0)",
                price = 6612000,
                category_id = "00003",
                sale = 0,
                color = "Золотой",
                stone_type = "Бриллиант, изумруд",
                metal = "Золото 18K",
                content = "585",
                size = "Стандартный",
                weight = "10 г",
                country_of_origin = "Франция",
                provider = "Jewel Emporium",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 15, 4),
                    InStock("Региональный склад", "Санкт-Петербург", 10, 2)
                ),
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/9K20oYczNKgyFNX962CGFJ77u2g5BhOkqHYTnmkx.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/6KYnEDKytyciQfc4GhkJS4FXVfP53EedcRtfYHjL.png"
                )
            ),
            Product(
                id = "00009",
                full_name = "Серьги ",
                name = "(1.6.2.1.035.12)",
                price = 2234000,
                category_id = "00002",
                sale = 1.2,
                color = "Золотой",
                stone_type = "Бриллиант, изумруд",
                metal = "Золото 18K",
                content = "585",
                size = "Стандартный",
                weight = "10 г",
                country_of_origin = "Франция",
                provider = "Jewel Emporium",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 15, 4),
                    InStock("Региональный склад", "Санкт-Петербург", 10, 2)
                ),
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/o370DoheuQKH7dPwaoVGKptHpd0CieEP1xHx7AL2.png"
                )
            ),
            Product(
                id = "000010",
                full_name = "Кольцо золотое с крупным прямоугольным камнем",
                name = "(1.4.1.1.334.1_19)",
                price = 2878000,
                category_id = "00001",
                sale = 1,
                color = "Золотой",
                stone_type = "Бриллиант, изумруд",
                metal = "Золото 18K",
                content = "585.",
                size = "Стандартный",
                weight = "10 г",
                country_of_origin = "Франция",
                provider = "Jewel Emporium",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 15, 4),
                    InStock("Региональный склад", "Санкт-Петербург", 10, 2)
                ),
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/leqH3Jry5hfowKdWGmYC3P1WzV2o9XgE7FJ4ZnYo.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/3jP3sNPLIRWA25E6V30Lm38JI4q0XC0t0EgDyXc9.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/PpjZZwIIUi8dPlo8q6lGiPxdvO2mnN0LLC5Nuap6.png"
                )
            ),
            Product(
                id = "000011",
                full_name = "Кольцо золотое «Бабочка»",
                name = "(1.4.1.1.336.3_17,5)",
                price = 3005000,
                category_id = "00001",
                sale = 8.0,
                color = "Золотой",
                stone_type = "Бриллиант, изумруд",
                metal = "Золото 18K",
                content = "558",
                size = "Стандартный",
                weight = "10 г",
                country_of_origin = "Франция",
                provider = "Jewel Emporium",
                counts = listOf(
                    InStock("Центральный склад", "Москва", 15, 4),
                    InStock("Региональный склад", "Санкт-Петербург", 10, 2)
                ),
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/nGDDpEI7PUHSzhexPDFij3Iu2zcNr2J5AheI2iNO.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/dgXCKEY98znc61F5gknVWSTZjWlaHhTBMMYsqkUm.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/d4qhKaEwLFx9cxHBJp3lR29hPbo6oicCx1flCzFi.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/Ul5HXUtSBtMSkveAh3tyStBqQOM8d3jergkAol6B.png"
                )
            )

        )
        fetchRV(allProducts)
        savingAndFetchingCategory(binding)

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

    override fun onPause() {
        super.onPause()
        Cart.saveCartToPrefs(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Cart.saveCartToPrefs(requireContext())
//        if (selectCategory != null){
//            preferences.edit().putString("selected category", selectCategory!!.id)
//        }
//        viewCheckedCategory.visibility = GONE
//        selectCategory = null
//        filterProducts()
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

    @SuppressLint("CommitPrefEdits")
    override fun onCategorySelected(selectedCategory: Category) {
        selectCategory = selectedCategory
        preferences.edit()?.putString("selectedCategory", selectedCategory.id)?.apply()

        savingAndFetchingCategory(binding)

//        binding.apply {
//            if (!searchBarChecked(viewSearch)) {
//                viewSearch.visibility = GONE
//            }
//            materialCardViewCategory.setOnClickListener {
//                categoryDialog()
//            }
//            if (selectedCategory.id == "all") {
//                viewCheckedCategory.visibility = GONE
//            }
//            fabClearCategory.setOnClickListener {
//                viewCheckedCategory.visibility = GONE
//                selectCategory = null
//                preferences.edit()?.putString("selectedCategory", "all")?.apply()
//                filterProducts()
//            }
//            viewCheckedCategory.visibility = VISIBLE
//            checkedCategoryTv.text = selectedCategory.name
//        }
//        filterProducts()
    }
}
