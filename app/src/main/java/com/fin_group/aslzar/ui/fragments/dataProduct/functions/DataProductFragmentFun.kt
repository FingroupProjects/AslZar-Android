@file:Suppress("DEPRECATION")

package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import android.annotation.SuppressLint
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.response.GetSimilarProductsResponse
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.SetInProductBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import retrofit2.Callback
import com.fin_group.aslzar.util.formatNumber
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
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
//    val noHave = WarningNoHaveProductFragmentDialog()
//    noHave.show(activity?.supportFragmentManager!!, "Product no have dialog")

    val fragmentManager = requireFragmentManager()
    val tag = "Product no have dialog"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = WarningNoHaveProductFragmentDialog.newInstance(id)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}

fun DataProductFragment.addProduct(product: Product){
    sharedViewModel.onProductAddedToCart(product, requireContext())
    Toast.makeText(requireContext(), "Продукт добавлен в корзину", Toast.LENGTH_SHORT).show()
}

fun DataProductFragment.callSetInProduct(id: String){
    val fragmentManager = requireFragmentManager()
    val tag = "Set product in bottom sheet"
    val existingFragment = fragmentManager.findFragmentByTag(tag)

    if (existingFragment == null) {
        val bottomSheetFragment = SetInProductBottomSheetDialogFragment.newInstance(id)
        bottomSheetFragment.show(fragmentManager, tag)
    }
}
//
//@SuppressLint("UseCompatLoadingForColorStateLists")
//fun DataProductFragment.callSizeChipGroup(sizeList: List<String>){
//    for (size in sizeList) {
//        val chip = Chip(requireContext(), null, R.style.Widget_App_Chip)
//        chip.text = size
//        chip.isCheckable = true
//        chip.isClickable = true
//        if (size == sizeList[0]){
//            chip.isChecked = true
//            sizeSelected = size
//        }
//        val chipBackgroundSelector = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_selector)
//        chip.chipBackgroundColor = chipBackgroundSelector
//        chip.setTextColor(resources.getColorStateList(R.color.text_color_1))
//        chip.setChipStrokeColorResource(R.color.border_color_1)
//        chip.chipStrokeWidth = 3F
//
//        sizeChipGroup.addView(chip)
//    }
//
//    sizeChipGroup.setOnCheckedChangeListener { group, checkedId ->
//        val selectedChip = group.findViewById<Chip>(checkedId)
//        if (selectedChip != null) {
//            val selectedSize = selectedChip.text.toString()
//            sizeSelected = selectedSize
//        }
//    }
//}
//
//@SuppressLint("UseCompatLoadingForColorStateLists")
//fun DataProductFragment.callWeightChipGroup(weightList: List<String>){
//    for (size in weightList) {
//        val chip = Chip(requireContext(), null, R.style.Widget_App_Chip)
//        chip.text = size
//        chip.isCheckable = true
//        chip.isClickable = true
//        if (size == weightList[0]){
//            chip.isChecked = true
//            sizeSelected = size
//        }
//        val chipBackgroundSelector = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_selector)
//        chip.chipBackgroundColor = chipBackgroundSelector
//        chip.setTextColor(resources.getColorStateList(R.color.text_color_1))
//        chip.setChipStrokeColorResource(R.color.border_color_1)
//        chip.chipStrokeWidth = 3F
//
//        weightChipGroup.addView(chip)
//    }
//
//    weightChipGroup.setOnCheckedChangeListener { group, checkedId ->
//        val selectedChip = group.findViewById<Chip>(checkedId)
//        if (selectedChip != null) {
//            val selectedWeight = selectedChip.text.toString()
//            weightSelected = selectedWeight
//        }
//    }
//}

fun DataProductFragment.someImagesProduct() {
    imageList = product.img
    recyclerViewSomeImages.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerViewSomeImages.adapter = productSomeImagesAdapter
    productSomeImagesAdapter.updateList(imageList)
}

fun DataProductFragment.likeProducts(){
    val inStockList = listOf(
        InStock("Магазин 1", "Витрина 3", 8, 0),
        InStock("Магазин 2", "Витрина 8", 8, 0),
        InStock("Магазин 12", "Витрина 7", 8, 0),
        InStock("Магазин 5", "Витрина 6", 8, 0)
    )
//    alikeProductsList = listOf(
//        Product(
//            id = "00001323022",
//            full_name = "Кольцо 1",
//            name = "Серьги с аметистом 1",
//            price = 120000,
//            barcode = "",
//            category_id = "jewelry",
//            sale = 8,
//            color = "фиолетовый",
//            stone_type = "аметист",
//            metal = "Золото",
//            content = "Серьги с натуральным аметистом",
//            size = "21 мм",
//            weight = "5 г",
//            country_of_origin = "Турция",
//            provider = "Украшения Востока",
//            counts = inStockList,
//            img = listOf(
//                "http://convertolink.taskpro.tj/photoLink/public/storage/images/mixGa5sQn5AqcURSKl2Lm3tayf2Xb6SEUupuJQXV.png",
//                "http://convertolink.taskpro.tj/photoLink/public/storage/images/EI2sNF9keTbJRHDRqSnPhf8uPNs500V6oOyNDGur.png"
//            )
//        ),
//        Product(
//            id = "0000032421",
//            full_name = "Кольцо 2",
//            name = "Серьги с аметистом 2",
//            price = 1200,
//            barcode = "",
//            category_id = "jewelry",
//            sale = 10,
//            color = "фиолетовый",
//            stone_type = "аметист",
//            metal = "серебро",
//            content = "Серьги с натуральным аметистом",
//            size = "17 мм",
//            weight = "5 г",
//            country_of_origin = "Индия",
//            provider = "Украшения Востока",
//            counts = inStockList,
//            img = listOf(
//                "http://convertolink.taskpro.tj/photoLink/public/storage/images/EI2sNF9keTbJRHDRqSnPhf8uPNs500V6oOyNDGur.png",
//                "http://convertolink.taskpro.tj/photoLink/public/storage/images/mixGa5sQn5AqcURSKl2Lm3tayf2Xb6SEUupuJQXV.png"
//            )
//        )
//    )


    recyclerViewLikeProducts.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerViewLikeProducts.adapter = productAlikeAdapter
    productAlikeAdapter.updateList(alikeProductsList)
}

@SuppressLint("SetTextI18n", "UnsafeOptInUsageError")
fun DataProductFragment.setDataProduct(product: Product, binding: FragmentDataProductBinding){
    if (product.img.size <= 1){
        binding.otherImgRv.visibility = GONE
    } else {
        binding.otherImgRv.visibility = VISIBLE
    }

    if (product.is_set){
        isFilterOn = !isFilterOn
        if (isFilterOn) {
            if (filterBadge == null) {
                filterBadge = BadgeDrawable.create(requireContext())
                filterBadge?.isVisible = true
                BadgeUtils.attachBadgeDrawable(filterBadge!!, toolbar, R.id.product_set_item)
            }
        } else {
            filterBadge?.isVisible = false
        }
    } else {
        filterBadge?.isVisible = false
    }

    if (product.sale != 0){
        if (product.sale.toString().isNotEmpty() && product.sale.toDouble() > 0.0){
            binding.productSale.text = "-${formatNumber(product.sale.toDouble())}%"
            binding.productSale.visibility = VISIBLE
        } else {
            binding.productSale.visibility = GONE
        }
    }

    binding.apply {
        if (product.img.isNotEmpty()){
            Glide.with(requireContext()).load(product.img[0]).into(binding.imageView2)
        } else {
            imageView2.setImageResource(R.drawable.ic_no_image)
        }
//        dpTitle.text = product.full_name
        dpCode.text = product.name
        dpPrice.text = product.price.toString()
        dpStone.text = product.stone_type
        dpProbe.text = product.content
        dpMetal.text = product.metal
        dpWeight.text = product.weight
        dpSize.text = product.size
        dpInstallmentPrice.text = "(${((product.price.toDouble() * 0.06) + product.price.toDouble()) / 4} UZS/мес)"

        btnAddToCart.setOnClickListener {
            val addedProduct = Cart.getProductById(product.id)
            if (addedProduct != null){
                Toast.makeText(requireContext(), "Количество товара увеличено на +1", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
            }
            sharedViewModel.onProductAddedToCart(product, requireContext())

        }
    }
}

fun DataProductFragment.getSimilarProducts(){
    swipeRefreshLayout.isRefreshing = true

    val call = apiService.getApiService().getSimilarProducts("Bearer ${sessionManager.fetchToken()}", product.id)
    try {
        call.enqueue(object : Callback<GetSimilarProductsResponse?>{
            override fun onResponse(
                call: Call<GetSimilarProductsResponse?>,
                response: Response<GetSimilarProductsResponse?>
            ) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful){
                    val result = response.body()
                    if (result != null){
                        val similarProduct = result.result
                        Log.d("Tag", "onResponse: ${response.code()}")
                        Log.d("Tag", "onResponse: ${response.body()}")
                        getSimilarProduct = similarProduct
                        productAlikeAdapter.updateList(getSimilarProduct)
                    }else{
                        Toast.makeText(requireContext(), "Не удалось, повторите попытку еще раз!", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onResponse: ${response.code()}")
                        Log.d("TAG", "onResponse: ${response.body()}")
                    }
                }
            }
            override fun onFailure(call: Call<GetSimilarProductsResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }catch (e: Exception){
        swipeRefreshLayout.isRefreshing = false
        Log.d("TAG", "getSimilarProducts: ${e.message}")
    }
}

fun DataProductFragment.getProductByID(){
    swipeRefreshLayout.isRefreshing = true

    try {
        val call = apiService.getApiService().getProductByID("Bearer ${sessionManager.fetchToken()}", args.productId)
        call.enqueue(object : Callback<Product?> {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onResponse(call: Call<Product?>, response: Response<Product?>) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful){
                    val productResponse = response.body()
                    if (productResponse != null){
                        product = productResponse
                        setDataProduct(product, binding)
                        productSomeImagesAdapter.updateList(product.img)

                        if (product.is_set){
                            filterBadge = BadgeDrawable.create(requireContext())
                            filterBadge?.isVisible = true
                            BadgeUtils.attachBadgeDrawable(filterBadge!!, toolbar, R.id.product_set_item)
                        }


                    }
                }
            }

            override fun onFailure(call: Call<Product?>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    } catch (e: Exception){
        swipeRefreshLayout.isRefreshing = false
        Log.d("TAG", "getProductByID: ${e.message}")
    }
}