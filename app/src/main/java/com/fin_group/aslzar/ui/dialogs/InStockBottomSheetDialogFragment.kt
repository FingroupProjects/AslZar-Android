package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fin_group.aslzar.adapter.InStockAdapter
import com.fin_group.aslzar.databinding.FragmentBottomSheetDialogInStockBinding
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment

@Suppress("DEPRECATION")
class InStockBottomSheetDialogFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetDialogInStockBinding? = null
    private val binding get() = _binding!!

    var inStockList: List<InStock> = emptyList()
    private var inStockProductId: String = ""
    private lateinit var product: Product

    companion object {
//        fun newInstance(product: Product): InStockBottomSheetDialogFragment {
//            val dialog = InStockBottomSheetDialogFragment()
//            val args = Bundle()
//            args.putSerializable(ARG_PRODUCT, product)
//            dialog.arguments = args
//            return dialog
//        }
        fun newInstance(productId: String): InStockBottomSheetDialogFragment{
            val dialog = InStockBottomSheetDialogFragment()
            val args = Bundle()
            args.putString(ARG_PRODUCT_ID, productId)
            dialog.arguments = args
            return dialog
        }

        private const val ARG_PRODUCT = "product"
        private const val ARG_PRODUCT_ID = "productID"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetDialogInStockBinding.inflate(inflater, container, false)

        arguments?.let {
            //product = it.getParcelable(ARG_PRODUCT)!!
            inStockProductId = it.getString(ARG_PRODUCT_ID, "")
        }

        binding.titleProduct.text = inStockProductId
        //inStockList = product.counts

        inStockList = listOf(
            InStock("Магазин 1", "Витрина 3", 8, 0),
            InStock("Магазин 2", "Витрина 8", 8, 0),
            InStock("Магазин 12", "Витрина 7", 8, 0),
            InStock("Магазин 5", "Витрина 6", 8, 0),
            InStock("Магазин 8", "Витрина 3", 8, 0),
            InStock("Магазин 4", "Витрина 5", 8, 0),
            InStock("Магазин 6", "Витрина 3", 8, 0),
            InStock("Магазин 3", "Витрина 4", 8, 0),
            InStock("Магазин 7", "Витрина 3", 8, 0)
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