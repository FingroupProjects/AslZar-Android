package com.fin_group.aslzar.ui.fragments.dataProduct.functions

import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.ui.dialogs.PickCharacterProductDialogFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductElseFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.SetInProductFragment
import com.fin_group.aslzar.ui.fragments.main.MainFragment

fun SetInProductFragment.showAddingToCartDialog(product: ResultX, filterModel: FilterModel){
    val filterDialog = PickCharacterProductDialogFragment.newInstance(product, filterModel)
    filterDialog.setListeners(this, this)
    filterDialog.show(activity?.supportFragmentManager!!, "types dialog")
}