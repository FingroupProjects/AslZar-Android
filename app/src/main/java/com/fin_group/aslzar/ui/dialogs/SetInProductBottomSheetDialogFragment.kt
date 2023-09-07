package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.adapter.SetInProductAdapter
import com.fin_group.aslzar.databinding.FragmentSheetDialogSetInProductBottomBinding
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.util.OnProductClickListener
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SetInProductBottomSheetDialogFragment : BottomSheetDialogFragment(), OnProductClickListener {

    private var _binding: FragmentSheetDialogSetInProductBottomBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView

    val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var selectedProduct: Product

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    lateinit var setInProductAdapter: SetInProductAdapter

    private var setInProductId: String = ""
    var allProducts: List<Product> = emptyList()

    companion object {
        fun newInstance(setInProductId: String): SetInProductBottomSheetDialogFragment {
            val dialog = SetInProductBottomSheetDialogFragment()
            val args = Bundle()
            args.putString("setInProductId", setInProductId)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSheetDialogSetInProductBottomBinding.inflate(inflater, container, false)
        recyclerView = binding.spSomeImagesRv

        binding.close.setOnClickListener { dismiss() }

        arguments?.let {
            setInProductId = it.getString("setInProductId", "")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inStockList = listOf(
            InStock("Магазин 1", "Витрина 3", 8, 0),
            InStock("Магазин 2", "Витрина 8", 8, 0),
            InStock("Магазин 12", "Витрина 7", 8, 0),
            InStock("Магазин 5", "Витрина 6", 8, 0)
        )

        allProducts = listOf(
            Product(
                id = "00001323022",
                full_name = "Кольцо 1",
                name = "Серьги с аметистом 1",
                price = 120000,
                barcode = "",
                category_id = "jewelry",
                sale = 8,
                color = "фиолетовый",
                stone_type = "аметист",
                metal = "Золото",
                content = "Серьги с натуральным аметистом",
                size = "21 мм",
                weight = "5 г",
                country_of_origin = "Турция",
                provider = "Украшения Востока",
                counts = inStockList,
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/mixGa5sQn5AqcURSKl2Lm3tayf2Xb6SEUupuJQXV.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/EI2sNF9keTbJRHDRqSnPhf8uPNs500V6oOyNDGur.png"
                )
            ),
            Product(
                id = "0000032421",
                full_name = "Кольцо 2",
                name = "Серьги с аметистом 2",
                price = 1200,
                barcode = "",
                category_id = "jewelry",
                sale = 10,
                color = "фиолетовый",
                stone_type = "аметист",
                metal = "серебро",
                content = "Серьги с натуральным аметистом",
                size = "17 мм",
                weight = "5 г",
                country_of_origin = "Индия",
                provider = "Украшения Востока",
                counts = inStockList,
                img = listOf(
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/EI2sNF9keTbJRHDRqSnPhf8uPNs500V6oOyNDGur.png",
                    "http://convertolink.taskpro.tj/photoLink/public/storage/images/mixGa5sQn5AqcURSKl2Lm3tayf2Xb6SEUupuJQXV.png"
                )
            )
        )
        setInProductAdapter = SetInProductAdapter(allProducts, this)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = setInProductAdapter
        setInProductAdapter.updateList(allProducts)

        setInitialProductAndInterface()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setInitialProductAndInterface() {
        selectedProduct = allProducts[0]
        setDataProduct(selectedProduct)
        setInProductAdapter.setSelectedPositions(0)
        setInProductAdapter.updateList(allProducts)
    }

    override fun setProduct(product: Product) {
        val image = product.img[0]
        selectedProduct = product
        setDataProduct(product)
        currentSelectedPosition = allProducts.indexOfFirst { it.id == product.id }
        setInProductAdapter.setSelectedPositions(currentSelectedPosition)
        Glide.with(requireContext()).load(image).into(binding.mainImageView)

        setProductBottomSheet(product)
    }

    private fun setProductBottomSheet(product: Product) {
        binding.apply {
            addToCart.setOnClickListener {
                sharedViewModel.onProductAddedToCart(product, requireContext())
                Toast.makeText(requireContext(), "Продукт ${product.full_name} добавлен", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setDataProduct(product: Product){
        binding.apply {
            sipFullName.text = product.full_name
            sipCode.text = product.name
            sipPrice.text = product.price.toString()
            sipStone.text = product.stone_type
            sipProbe.text = product.content
            sipMetal.text = product.metal
            sipWeight.text = product.weight
            sipSize.text = product.size
        }
    }
}