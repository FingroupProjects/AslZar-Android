package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.adapter.ProductsInAddAdapter
import com.fin_group.aslzar.databinding.FragmentPickCharacterProductDialogBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.util.AddingProduct
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.BaseDialogFullFragment
import com.fin_group.aslzar.util.FilialListener


@Suppress("DEPRECATION")
class PickCharacterProductDialogFragment : BaseDialogFullFragment(), FilialListener, AddingProduct {

    private var _binding: FragmentPickCharacterProductDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var typeList: List<Type>
    private lateinit var product: ResultX
    private var sortedTypeList: MutableList<Type> = mutableListOf()
    private lateinit var myAdapter: ProductsInAddAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var listener: AddingProduct
    private lateinit var listener2: FilialListener


    companion object {
        fun newInstance(product: ResultX): PickCharacterProductDialogFragment {
            val dialog = PickCharacterProductDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, product)
            dialog.arguments = args
            return dialog
        }

        private const val ARG_PRODUCT = "addingProduct"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickCharacterProductDialogBinding.inflate(inflater, container, false)
        recyclerView = binding.rvCategories

        arguments?.let {
            product = it.getParcelable(ARG_PRODUCT)!!
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val hello = listOf<Count>(
            Count(12, "filial 1", 255, "Sclad 1"),
            Count(12, "filial 2", 255, "Sclad 2"),
            Count(12, "filial 3", 255, "Sclad 3"),
            Count(12, "filial 4", 255, "Sclad 4"),
            Count(12, "filial 5", 255, "Sclad 5"),
            Count(12, "filial 6", 255, "Sclad 6"),
        )
        val hello2 = listOf<Count>(
            Count(12, "filial 1", 84, "Sclad 1"),
            Count(12, "filial 2", 63, "Sclad 2"),
            Count(12, "filial 3", 555, "Sclad 3"),
            Count(12, "filial 4", 355, "Sclad 4"),
            Count(12, "filial 5", 955, "Sclad 5"),
            Count(12, "filial 6", 150, "Sclad 6"),
        )
        val hello3 = listOf<Count>(
            Count(12, "filial 1", 255, "Sclad 1"),
        )
//        typeList = listOf(
//            Type("Россия", hello2, "00001", "product 1", "provider 1", 10, 12),
//            Type("Россия", hello3, "00002", "product 2", "provider 2", 10, 12),
//            Type("Россия", emptyList(), "00003", "product 3", "provider 3", 10, 12),
//            Type("Россия", emptyList(), "00004", "product 4", "provider 4", 10, 12),
//            Type("Россия", hello, "00005", "product 5", "provider 5", 10, 12),
//            Type("Россия", hello2, "00006", "product 6", "provider 6", 10, 12),
//            Type("Россия", hello, "00007", "product 7", "provider 7", 10, 12),
//            Type("Россия", hello3, "00008", "product 8", "provider 9", 10, 12),
//        )

        myAdapter = ProductsInAddAdapter(sortedTypeList, this, this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = myAdapter
        setData(product)
    }

    fun setListeners(
        listenerFun: AddingProduct,
        listener2Fun: FilialListener
    ) {
        listener = listenerFun
        listener2 = listener2Fun
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setData(product: ResultX){
        typeList = product.types

        for (type in typeList){
            if (type.counts.isNotEmpty()){
                sortedTypeList.add(type)
            }
        }
        myAdapter.upgradeList(sortedTypeList)
        binding.productFullName.text = product.full_name
    }

    override fun addProduct(type: Type, count: Count) {
        TODO("Not yet implemented")
    }

    override fun addFilial(filial: Count, position: Int) {
        TODO("Not yet implemented")
    }
}