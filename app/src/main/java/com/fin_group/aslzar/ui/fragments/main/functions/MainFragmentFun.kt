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
import androidx.core.graphics.component2
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
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
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.dialogs.FilterDialogFragment
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.PickCharacterProductDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.ui.fragments.new_products.functions.getAllCategoriesFromApi
import com.fin_group.aslzar.util.FilterDialogListener
import com.fin_group.aslzar.util.UnauthorizedDialogFragment
import com.fin_group.aslzar.util.returnNumber
import com.fin_group.aslzar.util.viewChecked
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Handler
import kotlin.math.log
import kotlin.math.min


fun MainFragment.callFilterDialog(listener: FilterDialogListener) {
    val filterDialog = FilterDialogFragment()
    filterDialog.setFilterListener(listener)
    filterDialog.show(activity?.supportFragmentManager!!, "filter dialog")
}

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

fun MainFragment.searchViewFun() {
    setDefaultFilterViewModelData()

    filterViewModel.filterModel = filterViewModel.defaultFilterModel

    viewCheckedCategory.visibility = GONE
    selectCategory = null
    filterProducts()

    viewSearch.visibility = if (viewChecked(viewSearch)) VISIBLE else GONE
}


fun MainFragment.addProductToCart(product: ResultX) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    updateCartBadge()
}

fun MainFragment.showAddingToCartDialog(product: ResultX, filterModel: FilterModel){
    val filterDialog = PickCharacterProductDialogFragment.newInstance(product, filterModel)
    filterDialog.setListeners(this, this)
    filterDialog.show(activity?.supportFragmentManager!!, "types dialog")
}

fun MainFragment.updateCartBadge() {
    val uniqueProductTypes = Cart.getUniqueProductTypesCount()
    badgeManager.saveBadgeCount(uniqueProductTypes)

    val badge = bottomNavigationView.getOrCreateBadge(R.id.mainCartFragment)
    badge.isVisible = uniqueProductTypes > 0
    badge.number = uniqueProductTypes
}

fun MainFragment.savingAndFetchingFilter(binding: FragmentMainBinding) {
    try {
        val defaultFilterModel = filterViewModel.defaultFilterModel!!
        val thisFilterModel = filterViewModel.filterModel!!


        binding.apply {
            if (!viewChecked(viewSearch)) {
                if (searchText != "") {
                    searchView.setQuery("", false)
                }
                viewSearch.visibility = GONE
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
                viewCheckedCategory.visibility = GONE
                selectCategory = null
                preferences.edit()?.putString("selectedCategory", "all")?.apply()
                setDefaultFilterViewModelData()

                filterViewModel.filterChangeListener.postValue(filterViewModel.defaultFilterModel)
                filterProducts()
                filterProducts2()
                updateSelectedFiltersText(defaultFilterModel)
            }

            if (thisFilterModel != defaultFilterModel){
                viewCheckedCategory.visibility = VISIBLE
            } else {
                viewCheckedCategory.visibility = GONE
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

fun MainFragment.updateSelectedFiltersText(defaultFilterModel: FilterModel): String {
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

fun MainFragment.getSelectedFiltersText(filterModel: FilterModel?, defaultFilterModel: FilterModel): String {
    return (filterModel?.let {
        updateSelectedFiltersText(defaultFilterModel)
    } ?: "All Filters")
}

fun MainFragment.getAllProductsPriceMinMaxValues(): Pair<Double, Double> {
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

fun getNonZeroValues(products: List<ResultX>, extractor: (Type) -> Double): List<Double> {
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


fun MainFragment.getAllProductsSizeMinMaxValues(): Pair<Double, Double> {
    val nonZeroSizes = getNonZeroValues(retrieveProducts()) { type -> returnNumber(type.size.toString())?.toDouble() ?: 0.0 }
    val minSize = nonZeroSizes.minOrNull() ?: 0.0
    val maxSize = nonZeroSizes.maxOrNull() ?: 0.0
    return minSize to maxSize
}

fun MainFragment.getAllProductsWeightMinMaxValues(): Pair<Double, Double> {
    val nonZeroWeights = getNonZeroValues(retrieveProducts()) { type -> returnNumber(type.weight.toString())?.toDouble() ?: 0.0 }
    val minWeight = nonZeroWeights.minOrNull() ?: 0.0
    val maxWeight = nonZeroWeights.maxOrNull() ?: 0.0
    return minWeight to maxWeight
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

fun MainFragment.filterProducts2() {
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
                    type.size.toDouble() >= modelToUse.sizeFrom.toDouble() &&
                    type.size.toDouble() <= modelToUse.sizeTo.toDouble() &&
                    type.weight.toDouble() >= modelToUse.weightFrom.toDouble() &&
                    type.weight.toDouble() <= modelToUse.weightTo.toDouble()
                }
        }
        myAdapter.updateProducts(tempList)
    }
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
    return preferences.getString("productList", null)
        ?.let { Gson().fromJson(it, object : TypeToken<List<ResultX>>() {}.type) }
        ?: emptyList()
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
    recyclerView.startLayoutAnimation()


}


fun MainFragment.goToTop(){
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            btnGoTo.setOnClickListener {
                if (firstVisibleItemPosition <= 40){
                    recyclerView.smoothScrollToPosition(0)
                }else{
                    recyclerView.smoothScrollToPosition(40)

                    android.os.Handler().postDelayed({ recyclerView.scrollToPosition(0) }, 1000)
                }
            }


            if (firstVisibleItemPosition == 0) {
                btnGoTo.visibility = GONE
                if (isButtonVisible) {
                    hideButton()
                }
            } else {
                if (!isButtonVisible) {
                    showButton()
                }
            }

        }
    })
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
        // Запускаем корутину для выполнения асинхронного кода
        lifecycleScope.launch {
            try {
                // Получаем данные из API внутри корутины
                val call = withContext(Dispatchers.IO) {
                    apiService.getApiService().getAllProducts("Bearer ${sessionManager.fetchToken()}")
                }

                handleApiResponse(call)
            } catch (e: Exception) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    } catch (e: Exception) {
        swipeRefreshLayout.isRefreshing = false
    }
}

private fun MainFragment.handleApiResponse(call: Response<GetAllProductV2>) {
    errorTv.visibility = GONE

    if (call.isSuccessful) {
        val getAllProducts = call.body()
        if (getAllProducts?.result != null) {
            allProducts = getAllProducts.result
            val productListJson = Gson().toJson(allProducts)
            preferences.edit().putString("productList", productListJson).apply()

            if (filterModel == null || filterModel == defaultFilterModel) {
                setFilterViewModelData()
                filterProducts()
            }
        } else {
            showError("Произошла ошибка: ответ сервера не содержит данных.")
            Log.d("TAG", "handleApiResponse: Произошла ошибка: ответ сервера не содержит данных.")
        }
    } else {

        when (call.code()) {
            401 -> {
                UnauthorizedDialogFragment.showUnauthorizedError(
                    requireContext(),
                    preferences,
                    this,
                    sessionManager
                )
            }
            500 -> {
                showError("Сервер временно не работает, повторите попытку позже")
                Log.d("TAG", "handleApiResponse: Сервер временно не работает, повторите попытку позже")

            }
            else -> {
                showError("Произошла ошибка: ${call.message()}")
                Log.d("TAG", "handleApiResponse: Произошла ошибка: ${call.message()}")
            }
        }
    }
}


//fun MainFragment.getAllProductsFromApi() {
//    swipeRefreshLayout.isRefreshing = true
//    try {
//        swipeRefreshLayout.isRefreshing = false
//
//        val call = apiService.getApiService().getAllProducts("Bearer ${sessionManager.fetchToken()}")
//        call.enqueue(object : Callback<GetAllProductV2> {
//            override fun onResponse(call: Call<GetAllProductV2>, response: Response<GetAllProductV2>) {
//                swipeRefreshLayout.isRefreshing = false
//                if (response.isSuccessful) {
//                    val getAllProducts = response.body()
//                    if (getAllProducts?.result != null) {
//                        allProducts = getAllProducts.result
//                        val productListJson = Gson().toJson(allProducts)
//                        preferences.edit().putString("productList", productListJson).apply()
//
//                        if (filterModel == null){
//                            setFilterViewModelData()
//                            filterProducts()
//                        }
//                        if (filterModel == defaultFilterModel){
//                            setFilterViewModelData()
//                            filterProducts()
//                        }
//                    } else {
//                        showError("Произошла ошибка: ответ сервера не содержит данных.")
//                    }
//                } else {
//                    when (response.code()){
//                        401 -> {
//                            UnauthorizedDialogFragment.showUnauthorizedError(
//                                requireContext(),
//                                preferences,
//                                this@getAllProductsFromApi,
//                                sessionManager
//                            )
//                        }
//                        500 -> {
//                            showError("Сервер временно не работает, повторите попытку позже")
//                        }
//                        else -> {
//                            showError("Произошла ошибка: ${response.message()}")
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<GetAllProductV2>, t: Throwable) {
//                showError("Ошибка при выполнении запроса: ${t.message}")
//                swipeRefreshLayout.isRefreshing = false
//            }
//        })
//    } catch (e: Exception) {
//        showError("Произошла ошибка: ${e.message}")
//        swipeRefreshLayout.isRefreshing = false
//    }
//}

private fun MainFragment.showError(message: String) {
    errorTv.text = message
    errorTv.visibility = VISIBLE
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

                        val categoryListJson = Gson().toJson(allCategories)
                        preferences.edit().putString("categoryList", categoryListJson).apply()
                        recyclerView.visibility = VISIBLE
                        errorTv.visibility = GONE
                    } else {
                        showError("Произошла ошибка: ответ сервера не содержит данных.")
                        recyclerView.visibility = GONE
                        errorTv.visibility = VISIBLE

                    }
                } else {
                    when (response.code()){
                        401 -> {
                            UnauthorizedDialogFragment.showUnauthorizedError(
                                requireContext(),
                                preferences,
                                this@getAllCategoriesFromApi,
                                sessionManager
                            )
                        }
                        500 -> {
                            showError("Сервер временно не работает, повторите попытку позже")
                        }
                        else -> {
                            showError("Произошла ошибка: ${response.message()}")
                        }
                    }
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

fun MainFragment.setFilterViewModel() {
    val filterDialogFragment = FilterDialogFragment()
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

    Log.d("TAG", "setFilterViewModel: minPrice= $minPrice, maxPrice=$maxPrice")
    Log.d("TAG", "setFilterViewModel: minSize= $minSize, maxSize=$maxSize")
    Log.d("TAG", "setFilterViewModel: minWeight= $minWeight, maxWeight=$maxWeight")

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


fun MainFragment.setFilterViewModelData() {
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
    Log.d("TAG", "setFilterViewModelData: minPrice= $minPrice, maxPrice=$maxPrice")
    Log.d("TAG", "setFilterViewModelData: minSize= $minSize, maxSize=$maxSize")
    Log.d("TAG", "setFilterViewModelData: minWeight= $minWeight, maxWeight=$maxWeight")
    filterViewModel.defaultFilterModel = updatedFilterModel
    if (filterModel == null) {
        filterViewModel.filterModel = updatedFilterModel
    } else {
        filterViewModel.filterModel = filterModel
    }
}

fun MainFragment.setDefaultFilterViewModelData() {
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

    Log.d("TAG", "setDefaultFilterViewModelData: minPrice= $minPrice, maxPrice=$maxPrice")
    Log.d("TAG", "setDefaultFilterViewModelData: minSize= $minSize, maxSize=$maxSize")
    Log.d("TAG", "setDefaultFilterViewModelData: minWeight= $minWeight, maxWeight=$maxWeight")

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