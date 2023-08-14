@file:Suppress("DEPRECATION")

package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.fin_group.aslzar.R
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.models.ImageDataModel2
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.SetInProductBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.util.OnImageClickListener
import com.google.android.material.chip.Chip

fun DataProductFragment.callInStockDialog() {
    val inStockDialog = InStockBottomSheetDialogFragment()
    inStockDialog.show(activity?.supportFragmentManager!!, "Product in stock Dialog")
}

fun DataProductFragment.callOutStock() {
    val noHave = WarningNoHaveProductFragmentDialog()
    noHave.show(activity?.supportFragmentManager!!, "Product no have dialog")
}

fun DataProductFragment.callSetInProduct(){

    val setProductBottomSheet = SetInProductBottomSheetDialogFragment()
    setProductBottomSheet.show(activity?.supportFragmentManager!!, "Set product in bottom sheet")
}

@SuppressLint("UseCompatLoadingForColorStateLists")
fun DataProductFragment.callSizeChipGroup(sizeList: List<String>){
    for (size in sizeList) {
        val chip = Chip(requireContext(), null, R.style.Widget_App_Chip)
        chip.text = size
        chip.isCheckable = true
        chip.isClickable = true
        if (size == sizeList[0]){
            chip.isChecked = true
            sizeSelected = size
        }
        val chipBackgroundSelector = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_selector)
        chip.chipBackgroundColor = chipBackgroundSelector
        chip.setTextColor(resources.getColorStateList(R.color.text_color_1))
        chip.setChipStrokeColorResource(R.color.border_color_1)
        chip.chipStrokeWidth = 3F
        
        sizeChipGroup.addView(chip)
    }

    sizeChipGroup.setOnCheckedChangeListener { group, checkedId ->
        val selectedChip = group.findViewById<Chip>(checkedId)
        if (selectedChip != null) {
            val selectedSize = selectedChip.text.toString()
            sizeSelected = selectedSize
        }
    }
}

@SuppressLint("UseCompatLoadingForColorStateLists")
fun DataProductFragment.callWeightChipGroup(weightList: List<String>){
    for (size in weightList) {
        val chip = Chip(requireContext(), null, R.style.Widget_App_Chip)
        chip.text = size
        chip.isCheckable = true
        chip.isClickable = true
        if (size == weightList[0]){
            chip.isChecked = true
            sizeSelected = size
        }
        val chipBackgroundSelector = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_selector)
        chip.chipBackgroundColor = chipBackgroundSelector
        chip.setTextColor(resources.getColorStateList(R.color.text_color_1))
        chip.setChipStrokeColorResource(R.color.border_color_1)
        chip.chipStrokeWidth = 3F

        weightChipGroup.addView(chip)
    }

    weightChipGroup.setOnCheckedChangeListener { group, checkedId ->
        val selectedChip = group.findViewById<Chip>(checkedId)
        if (selectedChip != null) {
            val selectedWeight = selectedChip.text.toString()
            weightSelected = selectedWeight
        }
    }
}

fun DataProductFragment.someImagesProduct() {
    imageList = listOf(
        ImageDataModel(R.drawable.ring_2, "Test"),
        ImageDataModel(R.drawable.ring_3, "Test"),
        ImageDataModel(R.drawable.ring_4, "Test"),
        ImageDataModel(R.drawable.ring_6, "Test"),
        ImageDataModel(R.drawable.ring_7, "Test")
    )
    recyclerViewSomeImages.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerViewSomeImages.adapter = productSomeImagesAdapter
    productSomeImagesAdapter.updateList(imageList)
}

fun  DataProductFragment.likeProducts(){
    alikeProductsList = listOf(
        ImageDataModel2("00001", R.drawable.ring_2, "Кольцо 1"),
        ImageDataModel2("00005", R.drawable.ring_3, "Кольцо 2"),
        ImageDataModel2("00006", R.drawable.ring_7, "Кольцо 3"),
        ImageDataModel2("00007", R.drawable.ring_4, "Кольцо 4"),
        ImageDataModel2("00008", R.drawable.ring_6, "Кольцо 5"),
    )
    recyclerViewLikeProducts.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerViewLikeProducts.adapter = productAlikeAdapter
    productAlikeAdapter.updateList(alikeProductsList)
}
