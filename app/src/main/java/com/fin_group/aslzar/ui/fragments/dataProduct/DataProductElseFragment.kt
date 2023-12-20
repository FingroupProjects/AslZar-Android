package com.fin_group.aslzar.ui.fragments.dataProduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.R
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.util.AddingProduct

class DataProductElseFragment : Fragment(), AddingProduct {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_product_else, container, false)
    }

    override fun addProduct(product: ResultX, type: Type, count: Count) {
        TODO("Not yet implemented")
    }

}