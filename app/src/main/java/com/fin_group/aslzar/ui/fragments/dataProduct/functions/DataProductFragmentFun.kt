@file:Suppress("DEPRECATION")

package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductCharacteristicAdapter
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.GetSimilarProductsResponse
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.PickCharacterProductDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragmentDirections
import retrofit2.Callback
import com.fin_group.aslzar.util.formatNumber
import com.fin_group.aslzar.util.showBottomNav
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response

fun DataProductFragment.callInStockDialog(name: String, counts: List<Count>) {
    val fragmentManager = requireFragmentManager()
    val tag = "Product in stock Dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = InStockBottomSheetDialogFragment.newInstance(name, counts)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun DataProductFragment.callOutStock(id: String) {
    val fragmentManager = requireFragmentManager()
    val tag = "Product no have dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = WarningNoHaveProductFragmentDialog.newInstance(id)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun DataProductFragment.addProduct(product: ResultX) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    Toast.makeText(requireContext(), "Продукт добавлен в корзину", Toast.LENGTH_SHORT).show()
}

fun DataProductFragment.callSetInProduct(id: String) {
    val action = DataProductFragmentDirections.actionDataProductFragmentToSetInProductFragment(id)
    findNavController().navigate(action)
}

fun DataProductFragment.showProductCharacteristicDialog(product: ResultX){
    val newFilterModel = FilterModel(
        0,
        10000000000,
        0,
        100000,
        0,
        100000,
        Category("all", "Все")
    )
    val filterDialog = PickCharacterProductDialogFragment.newInstance(product, newFilterModel)
    filterDialog.setListeners(this, this)
    filterDialog.show(activity?.supportFragmentManager!!, "types dialog")
}

fun DataProductFragment.someImagesProduct() {
    imageList = product.img
    recyclerViewSomeImages.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerViewSomeImages.adapter = productSomeImagesAdapter
    productSomeImagesAdapter.updateList(imageList)
}

fun DataProductFragment.productCharacteristic(){
    characteristicRv = binding.characteristicRv
    productCharacteristicAdapter = ProductCharacteristicAdapter(characteristicList, this)
    characteristicList = product.types.filter { it.counts.isNotEmpty() }
    characteristicRv.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    characteristicRv.adapter = productCharacteristicAdapter
    productCharacteristicAdapter.updateData(characteristicList)
    productCharacteristicAdapter.setSelectedPosition(0)
}

fun DataProductFragment.likeProducts() {
    recyclerViewLikeProducts.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerViewLikeProducts.adapter = productAlikeAdapter
    productAlikeAdapter.updateList(getSimilarProduct)
}

@SuppressLint("SetTextI18n", "UnsafeOptInUsageError")
fun DataProductFragment.setDataProduct(product: ResultX, binding: FragmentDataProductBinding) {
    if (product.img.size <= 1) {
        binding.otherImgRv.visibility = GONE
    } else {
        binding.otherImgRv.visibility = VISIBLE
    }

    if (product.sale != 0) {
        if (product.sale.toString().isNotEmpty() && product.sale.toDouble() > 0.0) {
            binding.productSale.text = "-${formatNumber(product.sale.toDouble())}%"
            binding.productSale.visibility = VISIBLE
        } else {
            binding.productSale.visibility = GONE
        }
    }
    if (product.description.isNotEmpty()) {
        binding.tvDescription.visibility = VISIBLE
        binding.description.visibility = VISIBLE
        binding.tvDescription.text = product.description
    } else {
        binding.tvDescription.visibility = GONE
        binding.description.visibility = GONE
    }

    if (product.proba.isNotEmpty()) {
        binding.tvContent.visibility = VISIBLE
        binding.content.visibility = VISIBLE
        binding.tvContent.text = product.proba
    } else {
        binding.tvContent.visibility = GONE
        binding.content.visibility = GONE
    }

    if (product.metal.isNotEmpty()) {
        binding.tvMetal.visibility = VISIBLE
        binding.metal.visibility = VISIBLE
        binding.tvMetal.text = product.metal
    } else {
        binding.tvMetal.visibility = GONE
        binding.metal.visibility = GONE
    }

    binding.installmentPrice.text = null
    binding.withFirstPay.visibility = GONE
    binding.tvWithFirstPay.visibility = GONE

    binding.apply {
        if (product.img.isNotEmpty()) {
            Glide.with(requireContext()).load(product.img[0]).into(binding.imageView2)
        } else {
            imageView2.setImageResource(R.drawable.ic_no_image)
        }
        tvCode.text = product.name

        selectedCharacteristic = productCharacteristicAdapter.getSelectedProduct()
        if (selectedCharacteristic.counts.size <= 1){
            selectedCount = selectedCharacteristic.counts[0]
            tvPriceFirst.text = formatNumber(selectedCount.price)
            binding.tvFilial.text = selectedCount.filial
            binding.tvVitrina.text = selectedCount.sclad
            printPercent(binding, percentInstallment, selectedCount.price)
            Log.d("TAG","Set Data Product 1: ${selectedCount.price}")

        } else if (selectedCharacteristic.counts.size > 1){
            selectedCount = if(selectedCharacteristic.counts.any{ it.is_filial }){
                selectedCharacteristic.counts.find { it.is_filial }!!
            } else {
                selectedCharacteristic.counts.minBy { it.price.toDouble() }
            }
            tvPriceFirst.text = formatNumber(selectedCount.price)
            binding.tvFilial.text = selectedCount.filial
            binding.tvVitrina.text = selectedCount.sclad
            printPercent(binding, percentInstallment, selectedCount.price)
            Log.d("TAG","Set Data Product 2: ${selectedCount.price}")


        }
        else {
            tvPriceFirst.text = formatNumber(selectedCount.price)
            binding.tvFilial.text = selectedCount.filial
            binding.tvVitrina.text = selectedCount.sclad
            printPercent(binding, percentInstallment, selectedCount.price)
            Log.d("TAG","Set Data Product 2: ${selectedCount.price}")

        }
        tvStoneType.text = product.stone_type.ifEmpty { "Без камня" }
        tvContent.text = product.proba
        tvMetal.text = product.metal
        sharedViewModel.selectedPrice.postValue(selectedCount.price.toDouble())

        val installmentPrice = binding.installmentPrice
        val tvWithFirstPay = binding.tvWithFirstPay
        installmentPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                val initialPrice = sharedViewModel.selectedPrice.value ?:0.0

                if (!s.isNullOrEmpty()) {
                    val userInput = s.toString().toDoubleOrNull()
                    if (userInput != null && userInput <= sharedViewModel.selectedPrice.value!!) {
                        binding.withFirstPay.visibility = VISIBLE
                        tvWithFirstPay.visibility = VISIBLE
                        val remainingAmount = initialPrice - userInput.toDouble()
                        tvWithFirstPay.text = formatNumber(remainingAmount)
                        printPercent(binding, percentInstallment, remainingAmount)
                        Log.d("TAG","With First Pay: $remainingAmount")

                    } else {
                        installmentPrice.setText(initialPrice.toString())
                        installmentPrice.setSelection(installmentPrice.text.length)
                    }
                }else{
                    tvWithFirstPay.text = formatNumber(initialPrice)
                    printPercent(binding, percentInstallment, initialPrice)
                    Log.d("TAG","No First Pay: $initialPrice")
                }
            }

        })
        btnAddToCart.setOnClickListener {
            val addedProduct = Cart.getProductById(product.id)
//            if (addedProduct != null) {
//                Toast.makeText(requireContext(), "Количество товара увеличено на +1", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
//            }

            selectedCharacteristic = productCharacteristicAdapter.getSelectedProduct()

            if (selectedCharacteristic.counts.size <= 1){
                selectedCount = selectedCharacteristic.counts[0]
                listener1.addProduct(product, selectedCharacteristic, selectedCount)
            } else if (selectedCharacteristic.counts.size > 1){
                selectedCount = if(selectedCharacteristic.counts.any{ it.is_filial }){
                    selectedCharacteristic.counts.find { it.is_filial }!!
                } else {
                    selectedCharacteristic.counts.minBy { it.price.toDouble() }
                }
                listener1.addProduct(product, selectedCharacteristic, selectedCount)
            }
            else {
                listener1.addProduct(product, selectedCharacteristic, selectedCount)
            }
        }
    }
}
fun DataProductFragment.getSimilarProducts() {
    swipeRefreshLayout.isRefreshing = true

    val call = apiService.getApiService()
        .getSimilarProducts("Bearer ${sessionManager.fetchToken()}", product.id)
    try {
        call.enqueue(object : Callback<GetSimilarProductsResponse?> {
            override fun onResponse(
                call: Call<GetSimilarProductsResponse?>,
                response: Response<GetSimilarProductsResponse?>
            ) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val similarProduct = result.result
                        getSimilarProduct = similarProduct.filter { product ->
                            product.types.any { type ->
                                type.counts.isNotEmpty() && type.counts.any { count -> count.count > 0 }
                            }
                        }
                        productAlikeAdapter.updateList(getSimilarProduct)
                        if (similarProduct.isEmpty()) {
                            binding.similarProducts.visibility = GONE
                            binding.likeProductsRv.visibility = GONE
                        } else {
                            binding.similarProducts.visibility = VISIBLE
                            binding.likeProductsRv.visibility = VISIBLE
                        }
                    } else {
                        Toast.makeText(requireContext(), "Не удалось, повторите попытку еще раз!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetSimilarProductsResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    } catch (e: Exception) {
        swipeRefreshLayout.isRefreshing = false
        Log.d("TAG", "getSimilarProducts: ${e.message}")
    }
}

fun DataProductFragment.getProductByID() {
    swipeRefreshLayout.isRefreshing = true

    try {
        val call = apiService.getApiService().getProductByID("Bearer ${sessionManager.fetchToken()}", args.productId)
        call.enqueue(object : Callback<ResultX?> {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onResponse(call: Call<ResultX?>, response: Response<ResultX?>) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        product = productResponse
                        productSomeImagesAdapter.updateList(product.img)
                        //productCharacteristicAdapter.updateData(product.types)
                        setDataProduct(product, binding)
                        //productCharacteristic()
                    }
                }
            }
            override fun onFailure(call: Call<ResultX?>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    } catch (e: Exception) {
        swipeRefreshLayout.isRefreshing = false
        Log.d("TAG", "getProductByID: ${e.message}")
    }
}
fun DataProductFragment.retrieveCoefficientPlan(): PercentInstallment {
    val coefficientPlanJson = preferences.getString("coefficientPlan", null)
    return if (coefficientPlanJson != null) {
        val coefficientPlanType = object : TypeToken<PercentInstallment>() {}.type
        val percent = Gson().fromJson<PercentInstallment>(coefficientPlanJson, coefficientPlanType)
        percent
    } else {
        fetchCoefficientPlanFromApi()
        PercentInstallment(5, 5, 7, listOf(Percent(1.79, 3)))
    }
}

fun DataProductFragment.fetchCoefficientPlanFromPrefs() {
    val coefficientPlanJson = preferences.getString("coefficientPlan", null)
    try {
        if (coefficientPlanJson != null) {
            val coefficientPlanType = object : TypeToken<PercentInstallment>() {}.type
            val coefficientPlan = Gson().fromJson<PercentInstallment>(coefficientPlanJson, coefficientPlanType)
            percentInstallment = coefficientPlan

            selectedCharacteristic = productCharacteristicAdapter.getSelectedProduct()
            if (selectedCharacteristic.counts.size <= 1){
                selectedCount = selectedCharacteristic.counts[0]
                printPercent(binding, percentInstallment, selectedCount.price)
            } else if (selectedCharacteristic.counts.size > 1){
                selectedCount = if(selectedCharacteristic.counts.any{ it.is_filial }){
                    selectedCharacteristic.counts.find { it.is_filial }!!
                } else {
                    selectedCharacteristic.counts.minBy { it.price.toDouble() }
                }
                printPercent(binding, percentInstallment, selectedCount.price)
            }
            else {
                printPercent(binding, percentInstallment, selectedCount.price)
            }
        } else {
            fetchCoefficientPlanFromApi()
        }
    } catch (e: Exception) {
        Log.d("TAG", "getAllCategoriesPrefs: ${e.message}")
    }
}

fun DataProductFragment.fetchCoefficientPlanFromApi() {
    try {
        val call = apiService.getApiService().getPercentAndMonth("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<PercentInstallment?> {
            override fun onResponse(
                call: Call<PercentInstallment?>,
                response: Response<PercentInstallment?>
            ) {
                if (response.isSuccessful) {
                    val coefficientPlanList = response.body()
                    if (coefficientPlanList != null) {
                        val coefficientPlanJson = Gson().toJson(coefficientPlanList)
                        preferences.edit().putString("coefficientPlan", coefficientPlanJson).apply()
                        percentInstallment = coefficientPlanList

                        selectedCharacteristic = productCharacteristicAdapter.getSelectedProduct()
                        if (selectedCharacteristic.counts.size <= 1){
                            selectedCount = selectedCharacteristic.counts[0]
                            printPercent(binding, percentInstallment, selectedCount.price)
                            Log.d("TAG","fetchCoefficientPlanFromApi 1: ${selectedCount.price}")
                        } else if (selectedCharacteristic.counts.size > 1){
                            selectedCount = if(selectedCharacteristic.counts.any{ it.is_filial }){
                                selectedCharacteristic.counts.find { it.is_filial }!!
                            } else {
                                selectedCharacteristic.counts.minBy { it.price.toDouble() }
                            }
                            printPercent(binding, percentInstallment, selectedCount.price)
                        }
                        else {
                            printPercent(binding, percentInstallment, selectedCount.price)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<PercentInstallment?>, t: Throwable) {
                Log.d("TAG", "onFailure fetchCategoriesFromApi: ${t.message}")
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "fetchCategoriesFromApi: ${e.message}")
    }
}

fun DataProductFragment.printPercent(
    binding: FragmentDataProductBinding,
    installment: PercentInstallment,
    totalPrice: Number
) {
    binding.apply {
        printPercent.layoutManager = LinearLayoutManager(requireContext())
        adapterPaymentPercent = TableInstallmentAdapter(installment, totalPrice, 0)
        printPercent.adapter = adapterPaymentPercent
    }
}

fun DataProductFragment.onBackPressed() {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (args.parentFragment) {
                    "MainBarcode" -> {
                        findNavController().navigate(R.id.action_dataProductFragment_to_mainFragment)
                    }

                    "SalesProductsBarcode" -> {
                        findNavController().navigate(R.id.action_dataProductFragment_to_salesAndPromotionsFragment)
                    }

                    "NewProductsBarcode" -> {
                        findNavController().navigate(R.id.action_dataProductFragment_to_newProductsFragment)
                    }

                    else -> {
                        findNavController().popBackStack()
                    }
                }
                showBottomNav()
            }
        })
}
fun DataProductFragment.updateCartBadge() {
    val uniqueProductTypes = Cart.getUniqueProductTypesCount()
    badgeManager.saveBadgeCount(uniqueProductTypes)
}