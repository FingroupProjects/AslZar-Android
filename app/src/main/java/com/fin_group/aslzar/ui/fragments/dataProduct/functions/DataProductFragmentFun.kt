package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.fin_group.aslzar.adapter.ViewAdapter
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.ui.dialogs.InStockBottomSheetDialogFragment
import com.fin_group.aslzar.ui.dialogs.WarningNoHaveProductFragmentDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment

fun DataProductFragment.callInStockDialog(){
    val inStockDialog = InStockBottomSheetDialogFragment()
    inStockDialog.show(activity?.supportFragmentManager!!, "Product in stock Dialog")
}

fun DataProductFragment.callOutStock(){
    val noHave = WarningNoHaveProductFragmentDialog()
    noHave.show(activity?.supportFragmentManager!!, "Product no have dialog")
}

//fun DataProductFragment.displayList() {
//    val imageList = ArrayList<ImageDataModel>()
//    imageList.clear()
//    imageList.add(ImageDataModel("https://conversionxl.com/wp-content/uploads/2018/09/coding-language.jpg", "Test"))
//    imageList.add(ImageDataModel("https://makeawebsitehub.com/wp-content/uploads/2016/02/learn-code-e1455713167295.jpg", "Test"))
//    imageList.add(ImageDataModel("https://www.tecmint.com/wp-content/uploads/2016/11/Convert-PNG-to-JPG-Using-for-loop-Command.png", "Test"))
//    imageList.add(ImageDataModel("https://conversionxl.com/wp-content/uploads/2018/09/coding-language.jpg", "Test"))
//    imageList.add(ImageDataModel("https://www.tecmint.com/wp-content/uploads/2016/11/Convert-PNG-to-JPG-Using-for-loop-Command.png", "Test"))
//    recyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
//    recyclerView.adapter = ViewAdapter(imageList)
//}