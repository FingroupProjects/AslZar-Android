package com.fin_group.aslzar.ui.fragments.new_products.functions

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.response.GetAllProductsResponse
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.ui.fragments.new_products.NewProductsFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun NewProductsFragment.callCategoryDialog(listener: CategoryClickListener) {
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(listener)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun NewProductsFragment.callInStockDialog(name: String, counts: List<InStock>) {
    val fragmentManager = requireFragmentManager()
    val tag = "Product in stock Dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = InStockBottomSheetDialogFragment.newInstance(name, counts)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun NewProductsFragment.callOutStock(id: String) {
    val fragmentManager = requireFragmentManager()
    val tag = "Product no have dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = WarningNoHaveProductFragmentDialog.newInstance(id)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun NewProductsFragment.searchBarChecked(view: ConstraintLayout): Boolean {
    return view.visibility != View.VISIBLE
}

fun NewProductsFragment.categoryChecked(view: ConstraintLayout): Boolean {
    return view.visibility != View.VISIBLE
}

fun NewProductsFragment.searchViewFun() {
    if (selectCategory != null) {
        viewCheckedCategory.visibility = View.GONE
        selectCategory = null
        filterProducts()
        if (searchBarChecked(viewSearch)) {
            viewSearch.visibility = View.VISIBLE
        } else {
            viewSearch.visibility = View.GONE
        }
        Log.d("TAG", "searchViewFun: ${categoryChecked(viewCheckedCategory)}")
    } else {
        viewCheckedCategory.visibility = View.GONE
        selectCategory = null
        filterProducts()
        if (searchBarChecked(viewSearch)) {
            viewSearch.visibility = View.VISIBLE
        } else {
            viewSearch.visibility = View.GONE
        }
        Log.d("TAG", "searchViewFun: ${categoryChecked(viewCheckedCategory)}")
    }
}

fun NewProductsFragment.filterFun() {
    if (searchBarChecked(viewSearch)) {
        if (searchText != "") {
            searchView.setQuery("", false)
        }
        viewSearch.visibility = View.GONE
    }

    callCategoryDialog(this)
}

fun NewProductsFragment.addProductToCart(product: Product) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    updateBadge()
}

fun NewProductsFragment.updateBadge() {
    val uniqueProductTypes = Cart.getUniqueProductTypesCount()
    badgeManager.saveBadgeCount(uniqueProductTypes)

    val badge = bottomNavigationView.getOrCreateBadge(R.id.mainCartFragment)
    badge.isVisible = uniqueProductTypes > 0
    badge.number = uniqueProductTypes
}

fun NewProductsFragment.savingAndFetchingCategory(binding: FragmentMainBinding) {
    try {
        if (selectCategory != null) {
            if (selectCategory!!.id != "all") {
                binding.apply {
                    if (!searchBarChecked(viewSearch)) {
                        if (searchText != "") {
                            searchView.setQuery("", false)
                        }
                        viewSearch.visibility = View.GONE
                    }
                    materialCardViewCategory.setOnClickListener {
                        categoryDialog()
                    }
                    fabClearCategory.setOnClickListener {
                        viewCheckedCategory.visibility = View.GONE
                        selectCategory = null
                        preferences.edit()?.putString("selectedCategory", "all")?.apply()
                        filterProducts()
                    }
                    viewCheckedCategory.visibility = View.VISIBLE
                    checkedCategoryTv.text = selectCategory!!.name
                }
            } else {
                if (selectCategory!!.id == "all") {
                    viewCheckedCategory.visibility = View.GONE
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

fun NewProductsFragment.savingAndFetchSearch(binding: FragmentMainBinding) {
    try {
        if (searchText.isNotEmpty()) {
            binding.apply {
                viewCheckedCategory.visibility = View.GONE
                viewSearch.visibility = View.VISIBLE

                fabClearSearch.setOnClickListener {
                    if (searchText != "") {
                        searchView.setQuery("", false)
                    }
                    viewSearch.visibility = View.GONE
                }
                filterProducts()
            }
        }
    } catch (e: Exception) {
        Log.d("TAG", "savingAndFetchSearch: ${e.message}")
    }
}

fun NewProductsFragment.categoryDialog() {
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(this)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun NewProductsFragment.filterProducts() {
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

fun NewProductsFragment.getAllProductFromPrefs() {
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

fun NewProductsFragment.retrieveFilteredProducts(): List<Product> {
    val productJson = preferences.getString("filteredProducts", null)
    return if (productJson != null) {
        val productListType = object : TypeToken<List<Product>>() {}.type
        Gson().fromJson(productJson, productListType)
    } else {
        emptyList()
    }
}

@SuppressLint("NotifyDataSetChanged")
fun NewProductsFragment.fetchRV(productList: List<Product>) {
    recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    myAdapter = ProductsAdapter(productList, this)
    recyclerView.adapter = myAdapter
    myAdapter.notifyDataSetChanged()
}

fun NewProductsFragment.getAllProductsFromApi() {
    swipeRefreshLayout.isRefreshing = true
    try {
        val call =
            apiService.getApiService().getAllProducts("Bearer ${sessionManager.fetchToken()}")
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
                    } else {
                        Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT)
                            .show()
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


fun NewProductsFragment.getAllCategoriesPrefs() {
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

fun NewProductsFragment.getAllCategoriesFromApi() {
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
                        Toast.makeText(requireContext(), "Категории не найдены", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка, повторите попытку",
                        Toast.LENGTH_SHORT
                    ).show()
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
