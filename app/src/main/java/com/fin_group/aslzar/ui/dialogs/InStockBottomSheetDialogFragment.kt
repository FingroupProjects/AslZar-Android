package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.InStockAdapter
import com.fin_group.aslzar.databinding.FragmentBottomSheetDialogInStockBinding
import com.fin_group.aslzar.models.InStockProduct
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InStockBottomSheetDialogFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetDialogInStockBinding? = null
    private val binding get() = _binding!!

    var inStockList: List<InStockProduct> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetDialogInStockBinding.inflate(inflater, container, false)

        inStockList = listOf(
            InStockProduct("Магазин 1", "Витрина 3", 8),
            InStockProduct("Магазин 2", "Витрина 8", 8),
            InStockProduct("Магазин 12", "Витрина 7", 8),
            InStockProduct("Магазин 5", "Витрина 6", 8),
            InStockProduct("Магазин 8", "Витрина 3", 8),
            InStockProduct("Магазин 4", "Витрина 5", 8),
            InStockProduct("Магазин 6", "Витрина 3", 8),
            InStockProduct("Магазин 3", "Витрина 4", 8),
            InStockProduct("Магазин 7", "Витрина 3", 8)
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogHeightPercent(50)

        binding.btnCloseInStock.setOnClickListener { dismiss() }

        val recyclerView = binding.rvInStock
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = InStockAdapter(inStockList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}