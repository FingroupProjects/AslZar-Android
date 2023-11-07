@file:Suppress("DEPRECATION")

package com.fin_group.aslzar.ui.fragments.main.functions

import android.annotation.SuppressLint
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.response.GetAllProductsResponse
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.dialogs.FilterDialogFragment
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.FilterDialogListener
import com.fin_group.aslzar.util.returnNumber
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//fun MainFragment.callFilterDialog(listener: CategoryClickListener) {
//    val categoryDialog = FilterDialogFragment()
//    categoryDialog.setCategoryClickListener(listener)
//    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
//}

fun MainFragment.callFilterDialog(listener: FilterDialogListener){
    val filterDialog = FilterDialogFragment()
    filterDialog.setFilterListener(listener)
    filterDialog.show(activity?.supportFragmentManager!!, "filter dialog")
}

fun MainFragment.callCategoryDialog(listener: CategoryClickListener) {
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(listener)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun MainFragment.callInStockDialog(name: String, counts: List<InStock>) {
    val fragmentManager = requireFragmentManager()
    val tag = "Product in stock Dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = InStockBottomSheetDialogFragment.newInstance(name, counts)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun MainFragment.callOutStock(id: String) {
    val fragmentManager = requireFragmentManager()
    val tag = "Product no have dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = WarningNoHaveProductFragmentDialog.newInstance(id)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun MainFragment.searchBarChecked(view: ConstraintLayout): Boolean {
    return view.visibility != VISIBLE
}

fun MainFragment.categoryChecked(view: ConstraintLayout): Boolean {
    return view.visibility != VISIBLE
}

fun MainFragment.searchViewFun() {
    if (selectCategory != null) {
        viewCheckedCategory.visibility = GONE
        selectCategory = null
        filterProducts()
        if (searchBarChecked(viewSearch)) {
            viewSearch.visibility = VISIBLE
        } else {
            viewSearch.visibility = GONE
        }
    } else {
        viewCheckedCategory.visibility = GONE
        selectCategory = null
        filterProducts()
        if (searchBarChecked(viewSearch)) {
            viewSearch.visibility = VISIBLE
        } else {
            viewSearch.visibility = GONE
        }
    }
}

fun MainFragment.filterFun() {
    if (searchBarChecked(viewSearch)) {
        if (searchText != "") {
            searchView.setQuery("", false)
        }
        viewSearch.visibility = GONE
    }

    callCategoryDialog(this)
}

fun MainFragment.addProductToCart(product: Product) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    updateBadge()
}

fun MainFragment.updateBadge() {
    val uniqueProductTypes = Cart.getUniqueProductTypesCount()
    badgeManager.saveBadgeCount(uniqueProductTypes)

    val badge = bottomNavigationView.getOrCreateBadge(R.id.mainCartFragment)
    badge.isVisible = uniqueProductTypes > 0
    badge.number = uniqueProductTypes
}

fun MainFragment.savingAndFetchingCategory(binding: FragmentMainBinding) {
    try {
        if (selectCategory != null) {
            if (selectCategory!!.id != "all") {
                binding.apply {
                    if (!searchBarChecked(viewSearch)) {
                        if (searchText != "") {
                            searchView.setQuery("", false)
                        }
                        viewSearch.visibility = GONE
                    }
                    materialCardViewCategory.setOnClickListener {
                        categoryDialog()
                    }
                    fabClearCategory.setOnClickListener {
                        viewCheckedCategory.visibility = GONE
                        selectCategory = null
                        preferences.edit()?.putString("selectedCategory", "all")?.apply()
                        filterProducts()
                    }
                    viewCheckedCategory.visibility = VISIBLE
                    checkedCategoryTv.text = selectCategory!!.name
                }
            } else {
                if (selectCategory!!.id == "all") {
                    viewCheckedCategory.visibility = GONE
                    filterProducts()
                }
            }
            filterProducts()
        } else {
            filterProducts()
        }
    } catch (e: Exception) {
        Log.d("TAG", "onViewCreated: ${e.message}")
    }
}

fun MainFragment.savingAndFetchSearch(binding: FragmentMainBinding) {
    try {
        if (searchText.isNotEmpty()) {
            binding.apply {
                viewCheckedCategory.visibility = GONE
                viewSearch.visibility = VISIBLE

                fabClearSearch.setOnClickListener {
                    if (searchText != "") {
                        searchView.setQuery("", false)
                    }
                    viewSearch.visibility = GONE
                }
                filterProducts()
            }
        }
    } catch (e: Exception) {
        Log.d("TAG", "savingAndFetchSearch: ${e.message}")
    }
}

fun MainFragment.categoryDialog() {
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(this)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun MainFragment.filterProducts() {
    filteredProducts = if (searchText.isNotEmpty()) {
        allProducts.filter { product ->
            product.name.contains(searchText, ignoreCase = true) || product.id.contains(
                searchText,
                ignoreCase = true
            ) || product.full_name.contains(searchText, ignoreCase = true)
        }
    } else {
        if (selectCategory?.id == "all" || selectCategory == null) {
            allProducts
        } else {
            allProducts.filter { product ->
                product.category_id == selectCategory?.id
            }
        }
    }
    myAdapter.updateProducts(filteredProducts)
}

fun MainFragment.getAllProductFromPrefs() {
    try {
        val products = preferences.getString("productList", null)
        if (products != null) {
            val productsListType = object : TypeToken<List<Product>>() {}.type
            val productList = Gson().fromJson<List<Product>>(products, productsListType)
            allProducts = productList
            fetchRV(allProducts)
        } else {
            getAllProductsFromApi()
        }
        filteredProducts = retrieveFilteredProducts()
        fetchRV(filteredProducts)
    } catch (e: Exception) {
        Log.d("TAG", "getAllProductFromPrefs: ${e.message}")
    }
}

fun MainFragment.retrieveProducts(): List<Product> {
    val productJson = preferences.getString("productList", null)
    return if (productJson != null) {
        val productsListType = object : TypeToken<List<Product>>() {}.type
        val productList = Gson().fromJson<List<Product>>(productJson, productsListType)
        productList
    } else {
        emptyList()
    }
}

fun MainFragment.retrieveFilteredProducts(): List<Product> {
    val productJson = preferences.getString("filteredProducts", null)
    return if (productJson != null) {
        val productListType = object : TypeToken<List<Product>>() {}.type
        Gson().fromJson(productJson, productListType)
    } else {
        emptyList()
    }
}

@SuppressLint("NotifyDataSetChanged")
fun MainFragment.fetchRV(productList: List<Product>) {
    recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    myAdapter = ProductsAdapter(productList, this)
    recyclerView.adapter = myAdapter
    myAdapter.notifyDataSetChanged()
}

fun MainFragment.getAllProductsFromApi() {
    swipeRefreshLayout.isRefreshing = true
    try {
        val call = apiService.getApiService().getAllProducts("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<GetAllProductsResponse?> {
            override fun onResponse(
                call: Call<GetAllProductsResponse?>,
                response: Response<GetAllProductsResponse?>
            ) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val getAllProducts = response.body()
                    if (getAllProducts?.result != null) {
                        allProducts = getAllProducts.result
                        val productListJson = Gson().toJson(allProducts)
                        preferences.edit().putString("productList", productListJson).apply()
                        filterProducts()
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: ${response.body()}")
                        Log.d("TAG", "onResponse: ${response.code()}")
                    } else {
                        Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("TAG", "onResponse if un success: ${response.raw()}")
                }
            }
            override fun onFailure(call: Call<GetAllProductsResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "getAllProducts: ${e.message}")
        swipeRefreshLayout.isRefreshing = false
    }
}


fun MainFragment.getAllCategoriesPrefs() {
    try {
        val categoriesListJson = preferences.getString("categoryList", null)
        if (categoriesListJson != null) {
            val categoryListType = object : TypeToken<List<Category>>() {}.type
            val categoryList = Gson().fromJson<List<Category>>(categoriesListJson, categoryListType)
            val firstCategory = Category("all", "Все")
            allCategories = categoryList
            allCategories = mutableListOf(firstCategory).plus(allCategories)
        } else {
            getAllCategoriesFromApi()
        }
    } catch (e: Exception) {
        Log.d("TAG", "getAllCategoriesPrefs: ${e.message}")
    }
}

fun MainFragment.setFilterViewModel(){
    val filterDialogFragment = FilterDialogFragment()
    allProducts = retrieveProducts()
    val minPrice = allProducts.minBy { it.price.toDouble() }.price
    val maxPrice = allProducts.maxBy { it.price.toDouble() }.price
    val minSize = returnNumber(allProducts.minBy { it.size }.size)
    val maxSize = returnNumber(allProducts.maxBy { it.size }.size)
    val minWeight = returnNumber(allProducts.minBy { it.weight }.weight)
    val maxWeight = returnNumber(allProducts.maxBy { it.weight }.weight)
    val selectedCategoryId = preferences.getString("selectedCategory", "all")
    selectCategory = allCategories.find { it.id == selectedCategoryId }
    val updatedFilterModel = FilterModel(
        minPrice, maxPrice, minSize, maxSize, minWeight, maxWeight, selectCategory ?: Category("all", "Все")
    )
    filterViewModel.defaultFilterModel = updatedFilterModel

    if (filterModel == null){
        filterViewModel.filterModel = updatedFilterModel
    } else {
        filterViewModel.filterModel = filterModel
    }

    filterDialogFragment.show(parentFragmentManager, "filterDialog")
}

fun MainFragment.getAllCategoriesFromApi() {
    swipeRefreshLayout.isRefreshing = true
    try {
        val call =
            apiService.getApiService().getAllCategories("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<GetAllCategoriesResponse?> {
            override fun onResponse(
                call: Call<GetAllCategoriesResponse?>,
                response: Response<GetAllCategoriesResponse?>
            ) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val categoryList = response.body()?.result
                    if (categoryList != null) {
                        val firstCategory = Category("all", "Все")
                        allCategories = categoryList
                        allCategories = mutableListOf(firstCategory).plus(allCategories)
                    } else {
                        Toast.makeText(requireContext(), "Категории не найдены", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка, повторите попытку", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<GetAllCategoriesResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "getAllCategories: ${e.message}")
        swipeRefreshLayout.isRefreshing = false

    }
}
