package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorV2Binding
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.GetSimilarProductsResponse
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.SetInProductBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragmentV2
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetchCoefficientPlanFromApi
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.printPercent
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.ui.fragments.main.functions.getAllCategoriesFromApi
import com.fin_group.aslzar.util.NoInternetDialogFragment
import retrofit2.Callback
import com.fin_group.aslzar.util.formatNumber
import com.fin_group.aslzar.util.showBottomNav
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response

fun DataProductFragment.callInStockDialog(name: String, counts: List<InStock>) {
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

fun DataProductFragment.addProduct(product: Product) {
    sharedViewModel.onProductAddedToCart(product, requireContext())
    Toast.makeText(requireContext(), "Продукт добавлен в корзину", Toast.LENGTH_SHORT).show()
}

fun DataProductFragment.callSetInProduct(id: String) {
    val fragmentManager = requireFragmentManager()
    val tag = "Set product in bottom sheet"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = SetInProductBottomSheetDialogFragment.newInstance(id)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun DataProductFragment.someImagesProduct() {
    imageList = product.img
    recyclerViewSomeImages.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerViewSomeImages.adapter = productSomeImagesAdapter
    productSomeImagesAdapter.updateList(imageList)
}

fun DataProductFragment.likeProducts() {
    recyclerViewLikeProducts.layoutManager =
        LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerViewLikeProducts.adapter = productAlikeAdapter
    productAlikeAdapter.updateList(alikeProductsList)
}

@SuppressLint("SetTextI18n", "UnsafeOptInUsageError")
fun DataProductFragment.setDataProduct(product: Product, binding: FragmentDataProductBinding) {
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
    if (product.description.isNotEmpty()){
        binding.dpDescription.visibility = VISIBLE
        binding.textView46.visibility = VISIBLE
        binding.dpDescription.text = product.description
    } else {
        binding.dpDescription.visibility = GONE
        binding.textView46.visibility = GONE
    }

    binding.apply {
        if (product.img.isNotEmpty()) {
            Glide.with(requireContext()).load(product.img[0]).into(binding.imageView2)
        } else {
            imageView2.setImageResource(R.drawable.ic_no_image)
        }
        dpCode.text = product.name
        dpPrice.text = product.price.toString()
        dpStone.text = product.stone_type.ifEmpty { "Без камня" }
        dpProbe.text = product.content
        dpMetal.text = product.metal
        dpWeight.text = product.weight
        dpSize.text = product.size

        dpInstallmentPrice.text = "(${((product.price.toDouble() * percentInstallment.first_pay.toDouble()) / 100)} UZS п.в.)"
        btnAddToCart.setOnClickListener {
            val addedProduct = Cart.getProductById(product.id)
            if (addedProduct != null) {
                Toast.makeText(
                    requireContext(),
                    "Количество товара увеличено на +1",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT)
                    .show()
            }
            sharedViewModel.onProductAddedToCart(product, requireContext())
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
                        getSimilarProduct = similarProduct
                        productAlikeAdapter.updateList(getSimilarProduct)
                        if (similarProduct.isEmpty()) {
                            binding.textView28.visibility = GONE
                            binding.likeProductsRv.visibility = GONE
                        } else {
                            binding.textView28.visibility = VISIBLE
                            binding.likeProductsRv.visibility = VISIBLE
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось, повторите попытку еще раз!",
                            Toast.LENGTH_SHORT
                        ).show()
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
        val call = apiService.getApiService()
            .getProductByID("Bearer ${sessionManager.fetchToken()}", args.productId)
        call.enqueue(object : Callback<Product?> {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onResponse(call: Call<Product?>, response: Response<Product?>) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        product = productResponse
                        setDataProduct(product, binding)
                        productSomeImagesAdapter.updateList(product.img)


                    }
                }
            }

            override fun onFailure(call: Call<Product?>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    } catch (e: Exception) {
        swipeRefreshLayout.isRefreshing = false
        Log.d("TAG", "getProductByID: ${e.message}")
    }
}

fun DataProductFragment.retrieveFilteredProducts(): List<Product> {
    val productJson = preferences.getString("filteredProducts", null)
    return if (productJson != null) {
        val productListType = object : TypeToken<List<Product>>() {}.type
        Gson().fromJson(productJson, productListType)
    } else {
        emptyList()
    }
}

fun DataProductFragment.retrieveCoefficientPlan(): PercentInstallment {
    val coefficientPlanJson = preferences.getString("coefficientPlan", null)
    return if (coefficientPlanJson != null) {
        val coefficientPlanType = object : TypeToken<PercentInstallment>() {}.type
        val percent = Gson().fromJson<PercentInstallment>(coefficientPlanJson, coefficientPlanType)
        percent
    } else {
        PercentInstallment(
            5, 5, listOf(
                Percent(1.79, 3)
            )
        )
    }
}

fun DataProductFragment.fetchCoefficientPlanFromPrefs() {
    val coefficientPlanJson = preferences.getString("coefficientPlan", null)
    try {
        if (coefficientPlanJson != null) {
            val coefficientPlanType = object : TypeToken<PercentInstallment>() {}.type
            val coefficientPlan = Gson().fromJson<PercentInstallment>(coefficientPlanJson, coefficientPlanType)
            percentInstallment = coefficientPlan
            val price = product.price.toDouble() - ((product.price.toDouble() * percentInstallment.first_pay.toDouble()) / 100)
            printPercent(binding, percentInstallment, price)
        } else {
            fetchCoefficientPlanFromApi()
        }
    } catch (e: Exception) {
        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
        Log.d("TAG", "getAllCategoriesPrefs: ${e.message}")
    }
}

fun DataProductFragment.fetchCoefficientPlanFromApi() {
    try {
        val call =
            apiService.getApiService().getPercentAndMonth("Bearer ${sessionManager.fetchToken()}")
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
                        val price =
                            product.price.toDouble() - ((product.price.toDouble() * percentInstallment.first_pay.toDouble()) / 100)
                        printPercent(
                            binding, percentInstallment, price
                        )
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

@SuppressLint("SetTextI18n")
fun DataProductFragment.createTable(
    binding: FragmentDataProductBinding,
    totalPrice: Number,
    percentInstallment: PercentInstallment
) {
    monthLinearLayout.removeAllViews()
    percentLinearLayout.removeAllViews()
    val monthTextViews = mutableListOf<TextView>()
    val percentTextViews = mutableListOf<TextView>()

    binding.apply {
        val monthLinearLayout = monthTable
        val percentLinearLayout = percentTable

        for (percent in percentInstallment.result) {
            val indexPercent = percentInstallment.result.indexOf(percent)

            val textViewMonth = TextView(requireContext())
            textViewMonth.apply {
                text = "${percent.mounth} платежей"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                setPadding(15, 15, 15, 15)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_1))
                View.TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                if (indexPercent < percentInstallment.result.size - 1) {
                    setBackgroundResource(R.drawable.bg_text_view_in_table)
                }
            }
            monthTextViews.add(textViewMonth)

            val textViewPercent = TextView(requireContext())
            val monthPayment =
                (((totalPrice.toDouble() * percent.coefficient.toDouble()) / 100) + totalPrice.toDouble()) / percent.mounth.toDouble()
            textViewPercent.apply {
                text = formatNumber(monthPayment)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                setPadding(15, 15, 15, 15)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_1))
                View.TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                if (indexPercent < percentInstallment.result.size - 1) {
                    setBackgroundResource(R.drawable.bg_text_view_in_table)
                }
            }
            percentTextViews.add(textViewPercent)
        }

        for (i in 0 until percentInstallment.result.size) {
            val percent = percentInstallment.result[i]
            val monthPayment =
                (((totalPrice.toDouble() * percent.coefficient.toDouble()) / 100) + totalPrice.toDouble()) / percent.mounth.toDouble()

            monthTextViews[i].text = "${percent.mounth} платежей"
            percentTextViews[i].text = formatNumber(monthPayment)
        }

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