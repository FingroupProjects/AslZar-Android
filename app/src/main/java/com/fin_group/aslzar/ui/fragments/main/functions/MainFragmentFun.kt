@file:Suppress("DEPRECATION")

package com.fin_group.aslzar.ui.fragments.main.functions

import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.fin_group.aslzar.R
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.response.GetAllProductsResponse
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun MainFragment.callCategoryDialog(listener: CategoryClickListener) {
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(listener)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun MainFragment.callInStockDialog(id: String){
    val fragmentManager = requireFragmentManager()
    val tag = "Product in stock Dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = InStockBottomSheetDialogFragment.newInstance(id)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun MainFragment.callOutStock(id: String){
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

fun MainFragment.searchViewFun(){
    if (selectCategory != null){
        viewCheckedCategory.visibility = GONE
        selectCategory = null
        filterProducts()
        if (searchBarChecked(viewSearch)) {
            viewSearch.visibility = VISIBLE
        } else {
            viewSearch.visibility = GONE
        }
        Log.d("TAG", "searchViewFun: ${categoryChecked(viewCheckedCategory)}")
    } else {
        viewCheckedCategory.visibility = GONE
        selectCategory = null
        filterProducts()
        if (searchBarChecked(viewSearch)) {
            viewSearch.visibility = VISIBLE
        } else {
            viewSearch.visibility = GONE
        }
        Log.d("TAG", "searchViewFun: ${categoryChecked(viewCheckedCategory)}")
    }
}

fun MainFragment.filterFun(){
    if (searchBarChecked(viewSearch)) {
        if (searchText != ""){
            searchView.setQuery("", false)
        }
        viewSearch.visibility = GONE
    }
    callCategoryDialog(this)
}

fun MainFragment.addProductToCart(product: Product){
    sharedViewModel.onProductAddedToCart(product, requireContext())
    updateBadge()
}
fun MainFragment.updateBadge(){
    val uniqueProductTypes = Cart.getUniqueProductTypesCount()
    badgeManager.saveBadgeCount(uniqueProductTypes)

    val badge = bottomNavigationView.getOrCreateBadge(R.id.mainCartFragment)
    badge.isVisible = uniqueProductTypes > 0
    badge.number = uniqueProductTypes
}

fun MainFragment.savingAndFetchingCategory(binding: FragmentMainBinding){
    try {
        if (selectCategory != null) {
            if (selectCategory!!.id != "all"){
                binding.apply {
                    if (!searchBarChecked(viewSearch)) {
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
    } catch (e: Exception){
        Log.d("TAG", "onViewCreated: ${e.message}")
    }
}

fun MainFragment.categoryDialog() {
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(this)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun MainFragment.filterProducts() {
    filteredProducts = if (searchText.isNotEmpty()){
        allProducts.filter { product ->
            product.name.contains(searchText, ignoreCase = true) || product.id.contains(searchText, ignoreCase = true)
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

fun MainFragment.getAllProducts(){
    val call = apiService.getApiService().getAllProducts("Bearer ${sessionManager.fetchToken()}")
    call.enqueue(object : Callback<GetAllProductsResponse?> {
        override fun onResponse(
            call: Call<GetAllProductsResponse?>,
            response: Response<GetAllProductsResponse?>
        ) {
            if (response.isSuccessful){
                val getAllProducts = response.body()
                if (getAllProducts?.result != null){
                    Toast.makeText(requireContext(), "Работает", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("TAG", "onResponse if un success: ${response.raw()}")
            }
        }

        override fun onFailure(call: Call<GetAllProductsResponse?>, t: Throwable) {
            Log.d("TAG", "onFailure: ${t.message}")
        }
    })
}