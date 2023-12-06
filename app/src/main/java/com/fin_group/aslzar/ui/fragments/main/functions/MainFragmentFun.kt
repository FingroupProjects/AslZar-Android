@file:Suppress("DEPRECATION")

package com.fin_group.aslzar.ui.fragments.main.functions

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.response.GetAllProductV2
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.dialogs.FilterDialogFragment
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.PickCharacterProductDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.main.MainFragment
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

fun MainFragment.callFilterDialog(listener: FilterDialogListener) {
    val filterDialog = FilterDialogFragment()
    filterDialog.setFilterListener(listener)
    filterDialog.show(activity?.supportFragmentManager!!, "filter dialog")
}

//fun MainFragment.callCategoryDialog(listener: CategoryClickListener) {
//    val categoryDialog = CheckCategoryFragmentDialog()
//    categoryDialog.setCategoryClickListener(listener)
//    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
//}

fun MainFragment.callInStockDialog(name: String, counts: List<Count>) {
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
    setFilterViewModelData()
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

//    callCategoryDialog(this)
}


fun MainFragment.addProductToCart(product: ResultX) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    updateBadge()
}

fun MainFragment.showAddingToCartDialog(product: ResultX){
    val filterDialog = PickCharacterProductDialogFragment.newInstance(product)
    filterDialog.setListeners(this, this)
    filterDialog.show(activity?.supportFragmentManager!!, "types dialog")
}

fun MainFragment.updateBadge() {
    val uniqueProductTypes = Cart.getUniqueProductTypesCount()
    badgeManager.saveBadgeCount(uniqueProductTypes)

    val badge = bottomNavigationView.getOrCreateBadge(R.id.mainCartFragment)
    badge.isVisible = uniqueProductTypes > 0
    badge.number = uniqueProductTypes
}

fun MainFragment.savingAndFetchingCategory(binding: FragmentMainBinding, filterModel: FilterModel) {
    try {
        val a = filterViewModel.defaultFilterModel!!
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
                        setFilterViewModel()
                    }
                    fabClearCategory.setOnClickListener {
                        viewCheckedCategory.visibility = GONE
                        selectCategory = null
                        preferences.edit()?.putString("selectedCategory", "all")?.apply()
                        setDefaultFilterViewModelData()

                        filterViewModel.filterModel = a
                        filterViewModel.filterChangeListener.postValue(filterViewModel.defaultFilterModel)
                        filterProducts()
                        filterProducts2(a)
                    }
                    viewCheckedCategory.visibility = VISIBLE
                    checkedCategoryTv.text = selectCategory!!.name
                }
            } else {
                if (selectCategory!!.id == "all") {
                    viewCheckedCategory.visibility = GONE
                    filterProducts()
                    filterProducts2(filterModel)
                }
            }
            filterProducts()
            filterProducts2(filterModel)
        } else {
            filterProducts()
            filterProducts2(a)
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

fun MainFragment.filterProducts2(filterModel: FilterModel) {

    filteredProducts = filteredProducts.filter { product ->
        product.types.any { type ->
            type.size.toDouble() >= filterModel.sizeFrom.toDouble()
                    && product.price.toDouble() <= filterModel.priceTo.toDouble()
                    && type.size.toDouble() <= filterModel.sizeTo.toDouble()
                    && type.size.toDouble() <= filterModel.sizeFrom.toDouble()
                    && type.weight.toDouble() >= filterModel.weightFrom.toDouble()
                    && type.weight.toDouble() <= filterModel.weightTo.toDouble()
        }
    }
    myAdapter.updateProducts(filteredProducts)
}

fun MainFragment.getAllProductFromPrefs() {
    try {
        val products = preferences.getString("productList", null)
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
        Log.d("TAG", "getAllProductFromPrefs: ${e.message}")
    }
}

fun MainFragment.retrieveProducts(): List<ResultX> {
    val productJson = preferences.getString("productList", null)
    return if (productJson != null) {
        val productsListType = object : TypeToken<List<ResultX>>() {}.type
        val productList = Gson().fromJson<List<ResultX>>(productJson, productsListType)
        productList
    } else {
        emptyList()
    }
}

fun MainFragment.retrieveFilteredProducts(): List<ResultX> {
    val productJson = preferences.getString("filteredProducts", null)
    return if (productJson != null) {
        val productListType = object : TypeToken<List<ResultX>>() {}.type
        Gson().fromJson(productJson, productListType)
    } else {
        emptyList()
    }
}

@SuppressLint("NotifyDataSetChanged")
fun MainFragment.fetchRV(productList: List<ResultX>) {
    recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    myAdapter = ProductsAdapter(productList, this)
    recyclerView.adapter = myAdapter
    myAdapter.notifyDataSetChanged()

    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (dy > 0 && isButtonVisible) {
                hideButton()
            } else if (dy < 0 && !isButtonVisible) {
                showButton()
            }
        }
        })
        btnGoTo.setOnClickListener {
        recyclerView.scrollToPosition(0)
            hideButton()
    }
}

fun MainFragment.showButton() {
    val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    btnGoTo.startAnimation(fadeIn)
    btnGoTo.visibility = VISIBLE
    isButtonVisible = true
}

fun MainFragment.hideButton() {
    val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    btnGoTo.startAnimation(fadeOut)
    btnGoTo.visibility = View.INVISIBLE
    isButtonVisible = false
}

fun MainFragment.getAllProductsFromApi() {
    swipeRefreshLayout.isRefreshing = true
    try {
        val call =
            apiService.getApiService().getAllProducts("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<GetAllProductV2> {
            override fun onResponse(
                call: Call<GetAllProductV2>,
                response: Response<GetAllProductV2>
            ) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val getAllProducts = response.body()
                    if (getAllProducts?.result != null) {
                        allProducts = getAllProducts.result
                        val productListJson = Gson().toJson(allProducts)
                        preferences.edit().putString("productList", productListJson).apply()
                        setFilterViewModelData()
                        filterProducts()
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: ${response.body()}")
                        Log.d("TAG", "onResponse: ${response.code()}")
                    } else {
                        Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d("TAG", "onResponse if un success: ${response.raw()}")
                }
            }

            override fun onFailure(call: Call<GetAllProductV2>, t: Throwable) {
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

fun MainFragment.setFilterViewModel() {
//    val filterDialogFragment = FilterDialogFragment()
//    allProducts = retrieveProducts()
//    val minPrice = allProducts.minBy { it.price.toDouble() }.price
//    val maxPrice = allProducts.maxBy { it.price.toDouble() }.price
//    val minSize = allProducts.minByOrNull {
//        it.types.minByOrNull { type -> returnNumber(type.size.toString()).toInt() }?.size ?: 0
//    }?.types?.minByOrNull { returnNumber(it.size.toString()).toDouble() }?.size ?: 0
//    val maxSize = allProducts.maxByOrNull {
//        it.types.maxByOrNull { type -> returnNumber(type.size.toString()).toInt() }?.size ?: 0
//    }?.types?.maxByOrNull { returnNumber(it.size.toString()).toInt() }?.size ?: 0
//    val minWeight = allProducts.minByOrNull {
//        it.types.minByOrNull { type -> returnNumber(type.weight.toString()).toInt() }?.weight ?: 0
//    }?.types?.minByOrNull { returnNumber(it.weight.toString()).toInt() }?.weight ?: 0
//    val maxWeight = allProducts.maxByOrNull {
//        it.types.maxByOrNull { type -> returnNumber(type.weight.toString()).toInt() }?.weight ?: 0
//    }?.types?.maxByOrNull { returnNumber(it.weight.toString()).toInt() }?.weight ?: 0
//    val selectedCategoryId = preferences.getString("selectedCategory", "all")
//    selectCategory = allCategories.find { it.id == selectedCategoryId }
//    val updatedFilterModel = FilterModel(
//        minPrice,
//        maxPrice,
//        minSize,
//        maxSize,
//        minWeight,
//        maxWeight,
//        selectCategory ?: Category("all", "Все")
//    )
//    filterViewModel.defaultFilterModel = updatedFilterModel
//
//    if (filterModel == null) {
//        filterViewModel.filterModel = updatedFilterModel
//    } else {
//        filterViewModel.filterModel = filterModel
//    }
//
//    filterDialogFragment.show(parentFragmentManager, "filterDialog")
}

fun MainFragment.setFilterViewModelData() {
    allProducts = retrieveProducts()
//    val minPrice = allProducts.minBy { it.price.toDouble() }.price
//    val maxPrice = allProducts.maxBy { it.price.toDouble() }.price
//    val minSize = (allProducts.minByOrNull {
//        it.types.minByOrNull { type -> returnNumber(type.size.toString()).toInt() }?.size ?: 0
//    }?.types?.minByOrNull { returnNumber(it.size.toString()).toInt() }?.size ?: 0)
//    val maxSize = allProducts.maxByOrNull {
//        it.types.maxByOrNull { type -> returnNumber(type.size.toString()).toInt() }?.size ?: 0
//    }?.types?.maxByOrNull { returnNumber(it.size.toString()).toInt() }?.size ?: 0
//    val minWeight = allProducts.minByOrNull {
//        it.types.minByOrNull { type -> returnNumber(type.weight.toString()).toInt() }?.weight ?: 0
//    }?.types?.minByOrNull { returnNumber(it.weight.toString()).toInt() }?.weight ?: 0
//    val maxWeight = allProducts.maxByOrNull {
//        it.types.maxByOrNull { type -> returnNumber(type.weight.toString()).toInt() }?.weight ?: 0
//    }?.types?.maxByOrNull { returnNumber(it.weight.toString()).toInt() }?.weight ?: 0
//    val selectedCategoryId = preferences.getString("selectedCategory", "all")
//    selectCategory = allCategories.find { it.id == selectedCategoryId }
//    val updatedFilterModel = FilterModel(
//        minPrice,
//        maxPrice,
//        minSize,
//        maxSize,
//        minWeight,
//        maxWeight,
//        selectCategory ?: Category("all", "Все")
//    )
//    filterViewModel.defaultFilterModel = updatedFilterModel
//    if (filterModel == null) {
//        filterViewModel.filterModel = updatedFilterModel
//    } else {
//        filterViewModel.filterModel = filterModel
//    }
}

fun MainFragment.setDefaultFilterViewModelData() {
//    allProducts = retrieveProducts()
//    val minPrice = allProducts.minBy { it.price.toDouble() }.price
//    val maxPrice = allProducts.maxBy { it.price.toDouble() }.price
//
//    val minSize = allProducts.minByOrNull {
//        it.types.minByOrNull { type -> returnNumber(type.size.toString()).toInt() }?.size ?: 0
//    }?.types?.minByOrNull { returnNumber(it.size.toString()).toInt() }?.size ?: 0
//    val maxSize = allProducts.maxByOrNull {
//        it.types.maxByOrNull { type -> returnNumber(type.size.toString()).toInt() }?.size ?: 0
//    }?.types?.maxByOrNull { returnNumber(it.size.toString()).toInt() }?.size ?: 0
//    val minWeight = allProducts.minByOrNull {
//        it.types.minByOrNull { type -> returnNumber(type.weight.toString()).toInt() }?.weight ?: 0
//    }?.types?.minByOrNull { returnNumber(it.weight.toString()).toInt() }?.weight ?: 0
//    val maxWeight = allProducts.maxByOrNull {
//        it.types.maxByOrNull { type -> returnNumber(type.weight.toString()).toInt() }?.weight ?: 0
//    }?.types?.maxByOrNull { returnNumber(it.weight.toString()).toInt() }?.weight ?: 0
//    val selectedCategoryId = preferences.getString("selectedCategory", "all")
//    selectCategory = allCategories.find { it.id == selectedCategoryId }
//    val updatedFilterModel = FilterModel(
//        minPrice,
//        maxPrice,
//        minSize,
//        maxSize,
//        minWeight,
//        maxWeight,
//        selectCategory ?: Category("all", "Все")
//    )
//    filterViewModel.defaultFilterModel = updatedFilterModel
//    filterViewModel.filterModel = updatedFilterModel
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
