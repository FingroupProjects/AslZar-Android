package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import android.view.Menu
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ViewAdapter
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.util.OnImageClickListener

fun DataProductFragment.callInStockDialog(){
    val inStockDialog = InStockBottomSheetDialogFragment()
    inStockDialog.show(activity?.supportFragmentManager!!, "Product in stock Dialog")
}

fun DataProductFragment.callOutStock(){
    val noHave = WarningNoHaveProductFragmentDialog()
    noHave.show(activity?.supportFragmentManager!!, "Product no have dialog")
}

fun DataProductFragment.displayList(listener: OnImageClickListener) {
    val imageList = ArrayList<ImageDataModel>()
    imageList.clear()
    imageList.add(ImageDataModel(R.drawable.earrings, "Test"))
    imageList.add(ImageDataModel(R.drawable.ring_2, "Test"))
    imageList.add(ImageDataModel(R.drawable.ring_3, "Test"))
    imageList.add(ImageDataModel(R.drawable.ring_4, "Test"))
    imageList.add(ImageDataModel(R.drawable.ring_6, "Test"))
    imageList.add(ImageDataModel(R.drawable.ring_7, "Test"))
    recyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
    recyclerView.adapter = ViewAdapter(imageList, listener)
}

