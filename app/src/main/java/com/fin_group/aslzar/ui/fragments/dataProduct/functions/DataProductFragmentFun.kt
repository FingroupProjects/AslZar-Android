package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.fin_group.aslzar.R
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.util.OnImageClickListener

fun DataProductFragment.callInStockDialog() {
    val inStockDialog = InStockBottomSheetDialogFragment()
    inStockDialog.show(activity?.supportFragmentManager!!, "Product in stock Dialog")
}

fun DataProductFragment.callOutStock() {
    val noHave = WarningNoHaveProductFragmentDialog()
    noHave.show(activity?.supportFragmentManager!!, "Product no have dialog")
}

fun DataProductFragment.displayList(listener: OnImageClickListener) {
    imageList = listOf(
        ImageDataModel(R.drawable.ring_2, "Test"),
        ImageDataModel(R.drawable.ring_3, "Test"),
        ImageDataModel(R.drawable.ring_4, "Test"),
        ImageDataModel(R.drawable.ring_6, "Test"),
        ImageDataModel(R.drawable.ring_7, "Test")
    )
    recyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerView.adapter = productSomeImagesAdapter
    productSomeImagesAdapter.updateList(imageList)
}

