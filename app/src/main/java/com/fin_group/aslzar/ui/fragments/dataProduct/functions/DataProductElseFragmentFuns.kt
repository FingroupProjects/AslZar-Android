package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import EqualSpacingItemDecoration
import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductCharacteristicAdapter
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.databinding.FragmentDataProductElseBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.ui.dialogs.PickCharacterProductDialogFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductElseFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragmentDirections
import com.fin_group.aslzar.ui.fragments.dataProduct.SetInProductFragment
import com.fin_group.aslzar.util.formatNumber
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun SetInProductFragment.showAddingToCartDialog(product: ResultX, filterModel: FilterModel){
    val filterDialog = PickCharacterProductDialogFragment.newInstance(product, filterModel)
    filterDialog.setListeners(this, this)
    filterDialog.show(activity?.supportFragmentManager!!, "types dialog")
}

fun DataProductElseFragment.fetchCoefficientPlanFromPrefs(binding: FragmentDataProductElseBinding) {
    val coefficientPlanJson = preferences.getString("coefficientPlan", null)
    try {
        if (coefficientPlanJson != null) {
            val coefficientPlanType = object : TypeToken<PercentInstallment>() {}.type
            val coefficientPlan = Gson().fromJson<PercentInstallment>(coefficientPlanJson, coefficientPlanType)
            percentInstallment = coefficientPlan

            val firstTypePrice = if (product.types.isNotEmpty() && product.types.firstOrNull()?.counts?.isNotEmpty() == true) {
                product.types.first().counts.first().price.toDouble()
            } else {
                0.0
            }
            Log.d("TAG","First Price 1: $firstTypePrice")
            val price = firstTypePrice - ((firstTypePrice * percentInstallment.first_pay.toDouble()) / 100)
            Log.d("TAG","Total Price 1: $price")
            printPercent(binding, percentInstallment, firstTypePrice)
        } else {
            fetchCoefficientPlanFromApi(binding)
        }
    } catch (e: Exception) {
        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
        Log.d("TAG", "getAllCategoriesPrefs: ${e.message}")
    }
}

fun DataProductElseFragment.retrieveCoefficientPlan(binding: FragmentDataProductElseBinding): PercentInstallment {
    val coefficientPlanJson = preferences.getString("coefficientPlan", null)
    return if (coefficientPlanJson != null) {
        val coefficientPlanType = object : TypeToken<PercentInstallment>() {}.type
        val percent = Gson().fromJson<PercentInstallment>(coefficientPlanJson, coefficientPlanType)
        percent
    } else {
        fetchCoefficientPlanFromApi(binding)
        PercentInstallment(
            5, 5, 7, listOf(
                Percent(1.79, 3)
            )
        )
    }
}

fun DataProductElseFragment.getProductByID(binding: FragmentDataProductElseBinding) {
    swipeRefreshLayout.isRefreshing = true

    try {
        val call = apiService.getApiService()
            .getProductByID("Bearer ${sessionManager.fetchToken()}", args.productId)
        call.enqueue(object : Callback<ResultX?> {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onResponse(call: Call<ResultX?>, response: Response<ResultX?>) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        product = productResponse
                        setDataProduct(product, binding)
                        productSomeImagesAdapter.updateList(product.img)
                        productCharacteristicAdapter.updateData(product.types)
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

fun DataProductElseFragment.fetchCoefficientPlanFromApi(binding: FragmentDataProductElseBinding) {
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

                        val firstTypePrice = if (product.types.isNotEmpty() && product.types.firstOrNull()?.counts?.isNotEmpty() == true) {
                            product.types.first().counts.first().price.toDouble()
                        } else {
                            0.0
                        }
                        //val price = firstTypePrice - ((firstTypePrice * percentInstallment.first_pay.toDouble()) / 100)
                        printPercent(binding, percentInstallment, firstTypePrice)
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

fun DataProductElseFragment.setDataProduct(product: ResultX, binding: FragmentDataProductElseBinding) {
    if (product.img.size <= 1) {
        binding.otherImgRv.visibility = View.GONE
    } else {
        binding.otherImgRv.visibility = View.VISIBLE
    }

    if (product.sale != 0) {
        if (product.sale.toString().isNotEmpty() && product.sale.toDouble() > 0.0) {
            binding.productSale.text = "-${formatNumber(product.sale.toDouble())}%"
            binding.productSale.visibility = View.VISIBLE
        } else {
            binding.productSale.visibility = View.GONE
        }
    }
    if (product.description.isNotEmpty()) {
        binding.tvDescription.visibility = View.VISIBLE
        binding.description.visibility = View.VISIBLE
        binding.tvDescription.text = product.description
    } else {
        binding.tvDescription.visibility = View.GONE
        binding.description.visibility = View.GONE
    }

    if (product.proba.isNotEmpty()) {
        binding.tvContent.visibility = View.VISIBLE
        binding.content.visibility = View.VISIBLE
        binding.tvContent.text = product.proba
    } else {
        binding.tvContent.visibility = View.GONE
        binding.content.visibility = View.GONE
    }

    if (product.metal.isNotEmpty()) {
        binding.tvMetal.visibility = View.VISIBLE
        binding.metal.visibility = View.VISIBLE
        binding.tvMetal.text = product.metal
    } else {
        binding.tvMetal.visibility = View.GONE
        binding.metal.visibility = View.GONE
    }

    binding.apply {
        if (product.img.isNotEmpty()) {
            Glide.with(requireContext()).load(product.img[0]).into(binding.imageView2)
        } else {
            imageView2.setImageResource(R.drawable.ic_no_image)
        }
        tvCode.text = product.name
        val price = product.types.flatMap { it.counts }.firstOrNull()?.price ?: 0
        tvPriceFirst.text = formatNumber(price)
        tvStoneType.text = product.stone_type.ifEmpty { "Без камня" }
        tvContent.text = product.proba
        tvMetal.text = product.metal
        sharedViewModel.selectedPrice.postValue(price.toDouble())

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
                        binding.withFirstPay.visibility = View.VISIBLE
                        tvWithFirstPay.visibility = View.VISIBLE
                        val remainingAmount = initialPrice - userInput.toDouble()
                        tvWithFirstPay.text = formatNumber(remainingAmount)
                        printPercent(binding, percentInstallment, remainingAmount)
                    } else {
                        installmentPrice.setText(initialPrice.toString())
                        installmentPrice.setSelection(installmentPrice.text.length)
                    }
                }else{
                    tvWithFirstPay.text = formatNumber(initialPrice)
                    printPercent(binding, percentInstallment, initialPrice)
                }
            }

        })
        btnAddToCart.setOnClickListener {
            val addedProduct = Cart.getProductById(product.id)
            if (addedProduct != null) {
                Toast.makeText(requireContext(), "Количество товара увеличено на +1", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun DataProductElseFragment.printPercent(
    binding: FragmentDataProductElseBinding,
    installment: PercentInstallment,
    totalPrice: Number
) {
    binding.apply {
        printPercent.layoutManager = LinearLayoutManager(requireContext())
        adapterPaymentPercent = TableInstallmentAdapter(installment, totalPrice, 0)
        printPercent.adapter = adapterPaymentPercent
    }
}

fun DataProductElseFragment.addProduct(product: ResultX) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    Toast.makeText(requireContext(), "Продукт добавлен в корзину", Toast.LENGTH_SHORT).show()
}

fun DataProductElseFragment.callSetInProduct(id: String) {
    val action = DataProductFragmentDirections.actionDataProductFragmentToSetInProductFragment(id)
    findNavController().navigate(action)
}

fun DataProductElseFragment.showProductCharacteristicDialog(product: ResultX){
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

fun DataProductElseFragment.someImagesProduct() {
    imageList = product.img
    recyclerViewSomeImages.layoutManager = LinearLayoutManager(requireContext(),
        LinearLayoutManager.HORIZONTAL, false)
    recyclerViewSomeImages.adapter = productSomeImagesAdapter
    productSomeImagesAdapter.updateList(imageList)
}

fun DataProductElseFragment.productCharacteristic(binding: FragmentDataProductElseBinding){
    characteristicRv = binding.characteristicRv
    productCharacteristicAdapter = ProductCharacteristicAdapter(characteristicList, this)
    productCharacteristicAdapter.setSelectedPosition(0)
    characteristicList = product.types.filter { it.counts.isNotEmpty() }
    characteristicRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
    characteristicRv.adapter = productCharacteristicAdapter
    productCharacteristicAdapter.updateData(characteristicList)
    productCharacteristicAdapter.setSelectedPosition(0)
}