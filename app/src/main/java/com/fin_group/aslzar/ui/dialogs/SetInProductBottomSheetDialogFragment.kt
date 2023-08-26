package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.BottomSheetItemAdapter
import com.fin_group.aslzar.databinding.FragmentSheetDialogSetInProductBottomBinding
import com.fin_group.aslzar.models.ImageDataModel2
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SetInProductBottomSheetDialogFragment : BottomSheetDialogFragment(), OnImageClickListener {

    private var _binding: FragmentSheetDialogSetInProductBottomBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView

    val sharedViewModel: SharedViewModel by activityViewModels()

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    var imageList: List<ImageDataModel2> = emptyList()
    lateinit var setProduct: BottomSheetItemAdapter

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

        arguments?.let {
            setInProductId = it.getString("setInProductId", "")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProduct = BottomSheetItemAdapter(imageList, this)
        setProduct.setSelectedPositions(0)
        setProductBottomSheet()
        setProduct()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setProduct() {
        imageList = listOf(
            ImageDataModel2("00011", R.drawable.ring_2, "Кольцо 11"),
            ImageDataModel2("00012", R.drawable.ring_3, "Кольцо 12"),
            ImageDataModel2("00013", R.drawable.ring_7, "Кольцо 13"),
            ImageDataModel2("00014", R.drawable.ring_4, "Кольцо 14"),
            ImageDataModel2("00015", R.drawable.ring_6, "Кольцо 15"),
        )

        val inStockList = listOf(
            InStock("Магазин 1", "Витрина 3", 8, 0),
            InStock("Магазин 2", "Витрина 8", 8, 0),
            InStock("Магазин 12", "Витрина 7", 8, 0),
            InStock("Магазин 5", "Витрина 6", 8, 0)
        )

        allProducts = listOf(
            Product(
                id = "0000022",
                full_name = "Кольцо",
                name = "Серьги с аметистом",
                price = 120.0,
                category_id = "jewelry",
                sale = 0.2,
                color = "фиолетовый",
                stone_type = "аметист",
                metal = "серебро",
                content = "Серьги с натуральным аметистом",
                size = "малый",
                weight = "5 г",
                country_of_origin = "Индия",
                provider = "Украшения Востока",
                counts = inStockList,
                img = listOf(
                    "https://cdn2.thecatapi.com/images/2n3.jpg",
                    "https://cdn2.thecatapi.com/images/2qo.jpg"
                )
            ),
            Product(
                id = "0000021",
                full_name = "Кольцо",
                name = "Серьги с аметистом",
                price = 1200,
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
                    "https://cdn2.thecatapi.com/images/as2.jpg",
                    "https://cdn2.thecatapi.com/images/bbg.jpg"
                )
            )
        )
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = setProduct
        setProduct.updateList(imageList)
    }

    override fun setImage(image: Int) {
        currentSelectedPosition = imageList.indexOfFirst { it.image == image }
        setProduct.setSelectedPositions(currentSelectedPosition)
        binding.mainImageView.setImageResource(image)
//        viewAdapter.notifyItemChanged(currentSelectedPosition)
//        Glide.with(requireContext()).load(image).into(binding.imageView2)
    }

    private fun setProductBottomSheet() {
        binding.apply {
            add.setOnClickListener {
//                sharedViewModel.onProductAddedToCart()

                Toast.makeText(requireContext(), "Добавление в корзину", Toast.LENGTH_SHORT).show()
            }
            close.setOnClickListener { dismiss() }
        }
    }

}