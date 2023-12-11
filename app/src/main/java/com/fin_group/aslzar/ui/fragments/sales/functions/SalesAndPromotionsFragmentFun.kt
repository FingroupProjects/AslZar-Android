@file:Suppress("DEPRECATION")

package com.fin_group.aslzar.ui.fragments.sales.functions

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.SalesProductsV2Adapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentSalesAndPromotionsBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.SaleProductsResponse
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.PickCharacterProductDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.sales.SalesAndPromotionsFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun SalesAndPromotionsFragment.callCategoryDialog(listener: CategoryClickListener) {
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(listener)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun SalesAndPromotionsFragment.callInStockDialog(name: String, counts: List<Count>) {
    val fragmentManager = requireFragmentManager()
    val tag = "Product in stock Dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = InStockBottomSheetDialogFragment.newInstance(name, counts)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun SalesAndPromotionsFragment.callOutStock(id: String) {
    val fragmentManager = requireFragmentManager()
    val tag = "Product no have dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = WarningNoHaveProductFragmentDialog.newInstance(id)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun SalesAndPromotionsFragment.searchBarChecked(view: ConstraintLayout): Boolean {
    return view.visibility != View.VISIBLE
}

fun SalesAndPromotionsFragment.categoryChecked(view: ConstraintLayout): Boolean {
    return view.visibility != View.VISIBLE
}

fun SalesAndPromotionsFragment.searchViewFun() {
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

fun SalesAndPromotionsFragment.addProductToCart(product: ResultX) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    updateBadge()
}

fun SalesAndPromotionsFragment.showAddingToCartDialog(product: ResultX, filterModel: FilterModel){
    val filterDialog = PickCharacterProductDialogFragment.newInstance(product, filterModel)
    filterDialog.setListeners(this, this)
    filterDialog.show(activity?.supportFragmentManager!!, "types dialog")
}

fun SalesAndPromotionsFragment.updateBadge() {
    val uniqueProductTypes = Cart.getUniqueProductTypesCount()
    badgeManager.saveBadgeCount(uniqueProductTypes)

    val badge = bottomNavigationView.getOrCreateBadge(R.id.mainCartFragment)
    badge.isVisible = uniqueProductTypes > 0
    badge.number = uniqueProductTypes
}

fun SalesAndPromotionsFragment.savingAndFetchSearch(binding: FragmentSalesAndPromotionsBinding) {
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

fun SalesAndPromotionsFragment.filterProducts() {
    filteredProducts = if (searchText.isNotEmpty()) {
        allProducts.filter { product ->
            product.name.contains(searchText, ignoreCase = true) ||
            product.id.contains(searchText, ignoreCase = true) ||
            product.full_name.contains(searchText, ignoreCase = true)
        }
    } else {
        allProducts
    }
    myAdapter.updateProducts(filteredProducts)
}

fun SalesAndPromotionsFragment.getAllProductFromPrefs() {
    try {
        val products = preferences.getString("productListSales", null)
        if (products != null) {
            val productsListType = object : TypeToken<List<ResultX>>() {}.type
            val productList = Gson().fromJson<List<ResultX>>(products, productsListType)
            allProducts = productList
            fetchRV(allProducts)
        } else {
            getAllProductsFromApi()
        }
        filteredProducts = retrieveFilteredProducts()
        fetchRV(filteredProducts)
    } catch (e: Exception) {
        Log.d("TAG", "getAllProductSalesFromPrefs: ${e.message}")
    }
}

fun SalesAndPromotionsFragment.retrieveFilteredProducts(): List<ResultX> {
    val productJson = preferences.getString("filteredProductsSales", null)
    return if (productJson != null) {
        val productListType = object : TypeToken<List<ResultX>>() {}.type
        Gson().fromJson(productJson, productListType)
    } else {
        emptyList()
    }
}

@SuppressLint("NotifyDataSetChanged")
fun SalesAndPromotionsFragment.fetchRV(productList: List<ResultX>) {
    recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    myAdapter = SalesProductsV2Adapter(productList, this)
    recyclerView.adapter = myAdapter
    myAdapter.updateProducts(productList)

    recyclerView.startLayoutAnimation()
}

fun SalesAndPromotionsFragment.getAllProductsFromApi() {
    swipeRefreshLayout.isRefreshing = true
    try {
        val call =
            apiService.getApiService().getSalesProducts("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<SaleProductsResponse?> {
            override fun onResponse(
                call: Call<SaleProductsResponse?>,
                response: Response<SaleProductsResponse?>
            ) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val getAllProducts = response.body()
                    if (getAllProducts?.result != null) {
                        allProducts = getAllProducts.result

                        val productListJson = Gson().toJson(allProducts)
                        preferences.edit().putString("productListSales", productListJson).apply()
                        filterProducts()
                    } else {
                        Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d("TAG", "onResponse if un success: ${response.raw()}")
                }
            }

            override fun onFailure(call: Call<SaleProductsResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "getAllProducts: ${e.message}")
        swipeRefreshLayout.isRefreshing = false
    }
}
