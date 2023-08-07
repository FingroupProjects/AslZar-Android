package com.fin_group.aslzar.util

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.fin_group.aslzar.ui.dialogs.CheckCategoryFragmentDialog
import com.fin_group.aslzar.ui.fragments.main.MainFragment

fun MainFragment.callCategoryDialog(listener: CategoryClickListener){
    val categoryDialog = CheckCategoryFragmentDialog()
    categoryDialog.setCategoryClickListener(listener)
    categoryDialog.show(activity?.supportFragmentManager!!, "category check dialog")
}

fun MainFragment.searchBarChecked(view: ConstraintLayout): Boolean{
    return view.visibility != View.VISIBLE
}

fun MainFragment.categoryChecked(view: ConstraintLayout): Boolean{
    return view.visibility != View.VISIBLE
}