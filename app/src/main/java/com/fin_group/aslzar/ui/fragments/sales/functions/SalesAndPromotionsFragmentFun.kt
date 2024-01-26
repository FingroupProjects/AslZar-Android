@file:Suppress("DEPRECATION")

package com.fin_group.aslzar.ui.fragments.sales.functions

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.SalesProductsV2Adapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentSalesAndPromotionsBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.SaleProductsResponse
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.dialogs.FilterDialogSalesProductsFragment
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.PickCharacterProductDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.new_products.functions.getNonZeroValues
import com.fin_group.aslzar.ui.fragments.sales.SalesAndPromotionsFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.handleErrorResponse
import com.fin_group.aslzar.util.returnNumber
import com.fin_group.aslzar.util.viewChecked
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

fun SalesAndPromotionsFragment.showAddingToCartDialog(product: ResultX, filterModel: FilterModel){
    val filterDialog = PickCharacterProductDialogFragment.newInstance(product, filterModel)
    filterDialog.setListeners(this, this)
    filterDialog.show(activity?.supportFragmentManager!!, "types dialog")
}


fun SalesAndPromotionsFragment.searchViewFun() {
    setDefaultFilterViewModelData()

    filterViewModel.filterModel = filterViewModel.defaultFilterModel

    viewCheckedCategory.visibility = View.GONE
    selectCategory = null
    filterProducts()

    viewSearch.visibility = if (viewChecked(
            viewSearch
        )
    ) VISIBLE else GONE
}

fun SalesAndPromotionsFragment.addProductToCart(product: ResultX) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    updateBadge()
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

fun SalesAndPromotionsFragment.savingAndFetchingFilter(binding: FragmentSalesAndPromotionsBinding) {
    try {
        val defaultFilterModel = filterViewModel.defaultFilterModel!!
        val thisFilterModel = filterViewModel.filterModel!!


        binding.apply {
            if (viewChecked(viewSearch)) {
                if (searchText != "") {
                    searchView.setQuery("", false)
                }
                viewSearch.visibility = View.GONE
            }
            materialCardViewCategory.setOnClickListener {
                setFilterViewModel()

                val (minPrice, maxPrice) = getAllProductsPriceMinMaxValues()
                val (minSize, maxSize) = getAllProductsSizeMinMaxValues()
                val (minWeight, maxWeight) = getAllProductsWeightMinMaxValues()

                val updatedFilterModel = FilterModel(
                    minPrice,
                    maxPrice,
                    minSize,
                    maxSize,
                    minWeight,
                    maxWeight,
                    filterModel?.category ?: Category("all", "Все")
                )

                updateSelectedFiltersText(updatedFilterModel)
            }
            fabClearCategory.setOnClickListener {
                viewCheckedCategory.visibility = View.GONE
                selectCategory = null
                preferences.edit()?.putString("selectedCategory", "all")?.apply()
                setDefaultFilterViewModelData()

                filterViewModel.filterChangeListener.postValue(filterViewModel.defaultFilterModel)
                filterProducts()
                filterProducts2()
                updateSelectedFiltersText(defaultFilterModel)
            }

            if (thisFilterModel != defaultFilterModel){
                viewCheckedCategory.visibility = View.VISIBLE
            } else {
                viewCheckedCategory.visibility = View.GONE
            }
            checkedFiltersTv.text = getSelectedFiltersText(filterModel, defaultFilterModel)
        }
        filterProducts()
        if (thisFilterModel != defaultFilterModel){
            filterProducts2()
        }

    } catch (e: Exception) {
        Log.d("TAG", "onViewCreated: ${e.message}")
    }
}

fun SalesAndPromotionsFragment.updateSelectedFiltersText(defaultFilterModel: FilterModel): String {
    val currentFilterModel = filterModel!!

    val selectedFiltersText = buildString {
        append("Фильтры: ")

        if (currentFilterModel.priceFrom != defaultFilterModel.priceFrom && currentFilterModel.priceTo != defaultFilterModel.priceTo) {
            append("Цена: от ${currentFilterModel.priceFrom} до ${currentFilterModel.priceTo}, ")
        } else if (currentFilterModel.priceFrom != defaultFilterModel.priceFrom) {
            append("Цена от: ${currentFilterModel.priceFrom}, ")
        } else if (currentFilterModel.priceTo != defaultFilterModel.priceTo) {
            append("Цена до: ${currentFilterModel.priceTo}, ")
        }

        if (currentFilterModel.sizeFrom != defaultFilterModel.sizeFrom && currentFilterModel.sizeTo != defaultFilterModel.sizeTo) {
            append("Размер: от ${currentFilterModel.sizeFrom} до ${currentFilterModel.sizeTo}, ")
        } else if (currentFilterModel.sizeFrom != defaultFilterModel.sizeFrom){
            append("Размер от: ${currentFilterModel.sizeFrom}, " )
        } else if (currentFilterModel.sizeTo != defaultFilterModel.sizeTo){
            append("Размер до: ${currentFilterModel.sizeTo}, " )
        }

        if (currentFilterModel.weightFrom != defaultFilterModel.weightFrom && currentFilterModel.weightTo != defaultFilterModel.weightTo) {
            append("Вес от ${currentFilterModel.weightFrom} до ${currentFilterModel.weightTo}.")
        } else if (currentFilterModel.weightFrom != defaultFilterModel.weightFrom){
            append("Вес от: ${currentFilterModel.weightFrom}, " )
        } else if (currentFilterModel.weightTo != defaultFilterModel.weightTo){
            append("Вес до: ${currentFilterModel.weightTo}, " )
        }

        if (currentFilterModel.category.id != "all"){
            append("Категория: ${currentFilterModel.category.name}")
        }
    }

    checkedFiltersTv.text = selectedFiltersText
    return selectedFiltersText
}

fun SalesAndPromotionsFragment.getSelectedFiltersText(filterModel: FilterModel?, defaultFilterModel: FilterModel): String {
    return (filterModel?.let {
        updateSelectedFiltersText(defaultFilterModel)
    } ?: "All Filters")
}

fun SalesAndPromotionsFragment.getAllProductsPriceMinMaxValues(): Pair<Double, Double> {
    val allProducts = retrieveProducts()

    val availableProducts = allProducts.filter { product ->
        product.types.any { type ->
            type.counts.isNotEmpty() && type.counts.any { count -> count.count > 0 }
        }
    }

    val nonZeroPrices = availableProducts
        .flatMap { it.types.filter { type -> type.counts.any{ count -> count.count > 0 } } }
        .flatMap { type -> type.counts.mapNotNull { count -> returnNumber(count.price.toString())?.toDouble()} }
        .filter { it > 0.0 }

    val minPrice = nonZeroPrices.minOrNull() ?: 0.0
    val maxPrice = nonZeroPrices.maxOrNull() ?: 0.0
    return minPrice to maxPrice
}

fun SalesAndPromotionsFragment(products: List<ResultX>, extractor: (Type) -> Double): List<Double> {
    val availableProducts = products.filter { product ->
        product.types.any { type ->
            type.counts.isNotEmpty() && type.counts.any { count -> count.count > 0 }
        }
    }

    return availableProducts
        .flatMap { it.types.filter { type -> type.counts.any { count -> count.count > 0 } } }
        .flatMap { type -> type.counts.mapNotNull { count -> extractor(type) } }
        .filter { it > 0.0 }
}


fun SalesAndPromotionsFragment.getAllProductsSizeMinMaxValues(): Pair<Double, Double> {
    val nonZeroSizes = getNonZeroValues(retrieveProducts()) { type -> returnNumber(type.size.toString())?.toDouble() ?: 0.0 }
    val minSize = nonZeroSizes.minOrNull() ?: 0.0
    val maxSize = nonZeroSizes.maxOrNull() ?: 0.0
    return minSize to maxSize
}

fun SalesAndPromotionsFragment.getAllProductsWeightMinMaxValues(): Pair<Double, Double> {
    val nonZeroWeights = getNonZeroValues(retrieveProducts()) { type -> returnNumber(type.weight.toString())?.toDouble() ?: 0.0 }
    val minWeight = nonZeroWeights.minOrNull() ?: 0.0
    val maxWeight = nonZeroWeights.maxOrNull() ?: 0.0
    return minWeight to maxWeight
}

fun SalesAndPromotionsFragment.filterProducts() {
    filteredProducts = if (searchText.isNotEmpty()) {
        allProducts.filter { product ->
            product.name.replace(".", "").contains(searchText, ignoreCase = true) ||
                    product.id.contains(searchText, ignoreCase = true) ||
                    product.full_name.contains(searchText, ignoreCase = true) ||
                    product.types.any { type -> type.name.replace(".", " ").contains(searchText, ignoreCase = true) }||
                    product.types.any { type -> type.counts.any{filial -> filial.filial.contains(searchText, ignoreCase = true)} }
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

fun SalesAndPromotionsFragment.filterProducts2() {
    val modelToUse = filterModel ?: filterViewModel.defaultFilterModel
    if (modelToUse != null) {
        val tempList = filteredProducts.filter { product ->
            product.types.any { type ->
                (type.filter || type.size.toDouble() > 0.0) &&
                        type.counts.any { count ->
                            count.price.toDouble() >= modelToUse.priceFrom.toDouble() &&
                                    count.price.toDouble() <= modelToUse.priceTo.toDouble()
                        }
            } &&
                    product.types.any { type ->
                        (type.filter || type.size.toDouble() >= modelToUse.sizeFrom.toDouble()) &&
                                (type.filter || type.size.toDouble() <= modelToUse.sizeTo.toDouble()) &&
                                type.weight.toDouble() >= modelToUse.weightFrom.toDouble() &&
                                type.weight.toDouble() <= modelToUse.weightTo.toDouble()
                    }
        }
        myAdapter.updateProducts(tempList)
    }
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
        Log.d("TAG", "getAllProductFromPrefs: ${e.message}")
    }
}

fun SalesAndPromotionsFragment.retrieveProducts(): List<ResultX> {
    return preferences.getString("productListSales", null)
        ?.let { Gson().fromJson(it, object : TypeToken<List<ResultX>>() {}.type) }
        ?: emptyList()
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
                Log.d("TAG", "onResponse: ${response.code()}")
                Log.d("TAG", "onResponse: ${response.body()}")
                Log.d("TAG", "onResponse: ${response.raw()}")
                if (response.isSuccessful) {
                    val getAllProducts = response.body()
                    if (getAllProducts?.result != null) {
                        allProducts = getAllProducts.result

                        val productListJson = Gson().toJson(allProducts)
                        preferences.edit().putString("productListSales", productListJson).apply()

                        if (filterModel == null){
                            setFilterViewModelData()
                            filterProducts()
                        }
                        if (filterModel == defaultFilterModel){
                            setFilterViewModelData()
                            filterProducts()
                        }

                        if (allProducts.isEmpty()){
                            recyclerView.visibility = GONE
                            errorTv.visibility = VISIBLE
                        } else {
                            recyclerView.visibility = VISIBLE
                            errorTv.visibility = GONE
                        }

                    } else {
                        showError("Произошла ошибка: ответ сервера не содержит данных.")
                        recyclerView.visibility = GONE
                        errorTv.visibility = VISIBLE
                    }
                } else {
                    handleErrorResponse(response.code(), requireContext(), preferences, sessionManager)
                }
            }

            override fun onFailure(call: Call<SaleProductsResponse?>, t: Throwable) {
                showError("Ошибка при выполнении запроса: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    } catch (e: Exception) {
        showError("Произошла ошибка: ${e.message}")
        swipeRefreshLayout.isRefreshing = false
    }
}

private fun SalesAndPromotionsFragment.showError(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun SalesAndPromotionsFragment.getAllCategoriesPrefs() {
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

fun SalesAndPromotionsFragment.getAllCategoriesFromApi() {
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

                        val categoryListJson = Gson().toJson(allCategories)
                        preferences.edit().putString("categoryList", categoryListJson).apply()
                    } else {
                        showError("Произошла ошибка: ответ сервера не содержит данных.")
                    }
                } else {
                    handleErrorResponse(response.code(), requireContext(), preferences, sessionManager)
                }
            }

            override fun onFailure(call: Call<GetAllCategoriesResponse?>, t: Throwable) {
                showError("Ошибка при выполнении запроса: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    } catch (e: Exception) {
        showError("Произошла ошибка: ${e.message}")
        swipeRefreshLayout.isRefreshing = false
    }
}

fun SalesAndPromotionsFragment.setFilterViewModel() {
    val filterDialogFragment = FilterDialogSalesProductsFragment()
    allProducts = retrieveProducts()

    val availableProducts = allProducts.filter { product ->
        product.types.any { type ->
            type.counts.isNotEmpty() && type.counts.any { count -> count.count > 0 }
        }
    }

    val nonZeroPrices = availableProducts
        .flatMap { it.types.filter { type -> type.counts.any{ count -> count.count > 0 } } }
        .flatMap { type -> type.counts.mapNotNull { count -> returnNumber(count.price.toString())?.toDouble()} }
        .filter { it > 0.0 }

    val minPrice = nonZeroPrices.minOrNull() ?: 0.0
    val maxPrice = nonZeroPrices.maxOrNull() ?: 0.0

    val nonZeroSizes = availableProducts
        .flatMap { it.types.filter { type -> type.counts.any { count -> count.count > 0 } } }
        .flatMap { type -> type.counts.mapNotNull { count -> returnNumber(type.size.toString())?.toDouble() } }
        .filter { it > 0.0 }

    val minSize = nonZeroSizes.minOrNull() ?: 0.0
    val maxSize = nonZeroSizes.maxOrNull() ?: 0.0

    val nonZeroWeights = availableProducts
        .flatMap { it.types.filter { type -> type.counts.any { count -> count.count > 0 } } }
        .flatMap { type -> type.counts.mapNotNull { count -> returnNumber(type.weight.toString())?.toDouble() } }
        .filter { it > 0.0 }

    val minWeight = nonZeroWeights.minOrNull() ?: 0.0
    val maxWeight = nonZeroWeights.maxOrNull() ?: 0.0

    val selectedCategoryId = preferences.getString("selectedCategory", "all")
    selectCategory = allCategories.find { it.id == selectedCategoryId }
    val updatedFilterModel = FilterModel(
        minPrice,
        maxPrice,
        minSize,
        maxSize,
        minWeight,
        maxWeight,
        selectCategory ?: Category("all", "Все")
    )
    filterViewModel.defaultFilterModel = updatedFilterModel

    if (filterModel == null) {
        filterViewModel.filterModel = updatedFilterModel
    } else {
        filterViewModel.filterModel = filterModel
    }

    filterDialogFragment.show(parentFragmentManager, "filterDialog")
}


fun SalesAndPromotionsFragment.setFilterViewModelData() {
    allProducts = retrieveProducts()

    val availableProducts = allProducts.filter { product ->
        product.types.any { type -> type.counts.isNotEmpty() }
    }

    val minPrice = availableProducts.minByOrNull { it.price.toDouble() }?.price ?: 0.0
    val maxPrice = availableProducts.maxByOrNull { it.price.toDouble() }?.price ?: 0.0
    val minSize = availableProducts.flatMap { it.types.mapNotNull { type -> returnNumber(type.size.toString())?.toDouble() } }.minOrNull() ?: 0.0
    val maxSize = availableProducts.flatMap { it.types.mapNotNull { type -> returnNumber(type.size.toString())?.toDouble() } }.maxOrNull() ?: 0.0
    val minWeight = availableProducts.flatMap { it.types.mapNotNull { type -> returnNumber(type.weight.toString())?.toDouble() } }.minOrNull() ?: 0.0
    val maxWeight = availableProducts.flatMap { it.types.mapNotNull { type -> returnNumber(type.weight.toString())?.toDouble() } }.maxOrNull() ?: 0.0
    val selectedCategoryId = preferences.getString("selectedCategory", "all")
    selectCategory = allCategories.find { it.id == selectedCategoryId }
    val updatedFilterModel = FilterModel(
        minPrice,
        maxPrice,
        minSize,
        maxSize,
        minWeight,
        maxWeight,
        selectCategory ?: Category("all", "Все")
    )
    filterViewModel.defaultFilterModel = updatedFilterModel
    if (filterModel == null) {
        filterViewModel.filterModel = updatedFilterModel
    } else {
        filterViewModel.filterModel = filterModel
    }
}

fun SalesAndPromotionsFragment.setDefaultFilterViewModelData() {
    allProducts = retrieveProducts()

    val availableProducts = allProducts.filter { product ->
        product.types.any { type ->
            type.counts.isNotEmpty() && type.counts.any { count -> count.count > 0 }
        }
    }

    val nonZeroPrices = availableProducts
        .flatMap { it.types.filter { type -> type.counts.any{ count -> count.count > 0 } } }
        .flatMap { type -> type.counts.mapNotNull { count -> returnNumber(count.price.toString())?.toDouble()} }
        .filter { it > 0.0 }

    val nonZeroSizes = availableProducts
        .flatMap { it.types.filter { type -> type.counts.any { count -> count.count > 0 } } }
        .flatMap { type -> type.counts.mapNotNull { count -> returnNumber(type.size.toString())?.toDouble() } }
        .filter { it > 0.0 }

    val nonZeroWeights = availableProducts
        .flatMap { it.types.filter { type -> type.counts.any { count -> count.count > 0 } } }
        .flatMap { type -> type.counts.mapNotNull { count -> returnNumber(type.weight.toString())?.toDouble() } }
        .filter { it > 0.0 }

    val minPrice = nonZeroPrices.minOrNull() ?: 0.0
    val maxPrice = nonZeroPrices.maxOrNull() ?: 0.0
    val minSize = nonZeroSizes.minOrNull() ?: 0.0
    val maxSize = nonZeroSizes.maxOrNull() ?: 0.0
    val minWeight = nonZeroWeights.minOrNull() ?: 0.0
    val maxWeight = nonZeroWeights.maxOrNull() ?: 0.0

    val selectedCategoryId = preferences.getString("selectedCategory", "all")
    selectCategory = allCategories.find { it.id == selectedCategoryId }
    val updatedFilterModel = FilterModel(
        minPrice,
        maxPrice,
        minSize,
        maxSize,
        minWeight,
        maxWeight,
        selectCategory ?: Category("all", "Все")
    )
    filterViewModel.defaultFilterModel = updatedFilterModel
    filterViewModel.filterModel = updatedFilterModel
}