package com.fin_group.aslzar.util

import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.constraintlayout.widget.ConstraintLayout
import com.fin_group.aslzar.models.Product
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.main.MainFragment

fun MainFragment.callCategoryDialog(listener: CategoryClickListener) {
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(listener)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun MainFragment.callInStockDialog(){
    val inStockDialog = InStockBottomSheetDialogFragment()
    inStockDialog.show(activity?.supportFragmentManager!!, "Product in stock Dialog")
}

fun MainFragment.callOutStock(){
    val noHave = WarningNoHaveProductFragmentDialog()
    noHave.show(activity?.supportFragmentManager!!, "Product no have dialog")
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
        searchText = ""
        viewSearch.visibility = GONE
    }
    callCategoryDialog(this)
}

fun MainFragment.filterProducts() {
    filteredProducts = if (searchText.isNotEmpty()){
        allProducts.filter { product ->
            product.name.contains(searchText, ignoreCase = true) || product.code.contains(searchText, ignoreCase = true)
        }
    } else {
        if (selectCategory?.id == "all" || selectCategory == null) {
            allProducts
        } else {
            allProducts.filter { product ->
                product.category == selectCategory?.id
            }
        }
    }

    myAdapter.updateProducts(filteredProducts)
}