package com.fin_group.aslzar.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fin_group.aslzar.models.FilterModel

class FilterViewModelSalesProducts: ViewModel() {
    var filterModel: FilterModel? = null
    var defaultFilterModel: FilterModel? = null

    val filterChangeListener = MutableLiveData<FilterModel>()
}