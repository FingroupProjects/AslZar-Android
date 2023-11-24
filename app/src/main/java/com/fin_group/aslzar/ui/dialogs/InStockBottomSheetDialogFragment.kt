package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fin_group.aslzar.adapter.InStockAdapter
import com.fin_group.aslzar.databinding.FragmentBottomSheetDialogInStockBinding
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.InStockList
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment

@Suppress("DEPRECATION")
class InStockBottomSheetDialogFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetDialogInStockBinding? = null
    private val binding get() = _binding!!

    private var inStockList: ArrayList<Count> = ArrayList()
    private var inStockProductId: String = ""
    private lateinit var product: ResultX
//    private lateinit var inStockList: InStockList

    companion object {
//        fun newInstance(product: Product): InStockBottomSheetDialogFragment {
//            val dialog = InStockBottomSheetDialogFragment()
//            val args = Bundle()
//            args.putSerializable(ARG_PRODUCT, product)
//            dialog.arguments = args
//            return dialog
//        }
        fun newInstance(productName: String, counts: List<Count>): InStockBottomSheetDialogFragment{
            val dialog = InStockBottomSheetDialogFragment()
            val args = Bundle()
            args.putString(ARG_PRODUCT_ID, productName)
            args.putParcelableArrayList(ARG_PRODUCT, ArrayList(counts))
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
            inStockList.addAll(it.getParcelableArrayList(ARG_PRODUCT)?: emptyList())
            inStockProductId = it.getString(ARG_PRODUCT_ID, "")
        }

        binding.titleProduct.text = inStockProductId
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