package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentAlikeProductBottomSheetDialogBinding
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.viewmodel.SharedViewModel

@Suppress("DEPRECATION")
class AlikeProductBottomSheetDialogFragment : BaseBottomSheetDialogFragment(), OnImageClickListener {

    private var _binding: FragmentAlikeProductBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var alikeProductID: String = ""
    lateinit var recyclerView: RecyclerView

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    var alikeImageList: List<ImageDataModel> = emptyList()
    lateinit var adapter: ProductSomeImagesAdapter
    lateinit var similarProduct: Product

    companion object {
//        fun newInstance(product: Product): AlikeProductBottomSheetDialogFragment {
//            val dialog = AlikeProductBottomSheetDialogFragment()
//            val args = Bundle()
//            args.putSerializable(ARG_PRODUCT, product)
//            dialog.arguments = args
//            return dialog
//        }

        fun newInstance(likeProductId: String): AlikeProductBottomSheetDialogFragment {
            val dialog = AlikeProductBottomSheetDialogFragment()
            val args = Bundle()
            args.putString("likeProductId", likeProductId)
            dialog.arguments = args
            return dialog
        }

        private const val ARG_PRODUCT = "alikeProduct"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlikeProductBottomSheetDialogBinding.inflate(inflater, container, false)

        arguments?.let {
            alikeProductID = it.getString("likeProductId", "")
            //similarProduct = it.getParcelable(ARG_PRODUCT)!!
        }
        binding.lpTitle.text = alikeProductID
        adapter = ProductSomeImagesAdapter(alikeImageList, this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.lpSomeImagesRv

        val inStockList = listOf(
            InStock("Магазин 1", "Витрина 3", 8),
            InStock("Магазин 2", "Витрина 8", 8),
            InStock("Магазин 12", "Витрина 7", 8),
            InStock("Магазин 5", "Витрина 6", 8)
        )

        similarProduct = Product(
            id = "0000022",
            full_name = "Кольцо с аметистом",
            name = "23..789.77",
            price = 120.0,
            category_id = "jewelry",
            barcode = "123456789",
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
        )

        val cartProduct = ProductInCart (
            similarProduct.id,
            similarProduct.full_name,
            similarProduct.img,
            similarProduct.barcode,
            1,
            similarProduct.sale,
            similarProduct.price
        )

        binding.apply {
            close.setOnClickListener { dismiss() }
            addToCart.setOnClickListener {
//                Cart.addProduct(cartProduct, requireContext())
                sharedViewModel.onProductAddedToCart(cartProduct)
            }
        }

        setProducts()
    }

    private fun setProducts() {
        alikeImageList = listOf(
            ImageDataModel(R.drawable.ring_1, "Кольцо 1"),
            ImageDataModel(R.drawable.ring_3, "Кольцо 2"),
            ImageDataModel(R.drawable.ring_7, "Кольцо 3"),
            ImageDataModel(R.drawable.ring_4, "Кольцо 4"),
            ImageDataModel(R.drawable.ring_6, "Кольцо 5"),
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
        recyclerView.adapter = adapter
        adapter.updateList(alikeImageList)
    }

    override fun setImage(image: Int) {
        currentSelectedPosition = alikeImageList.indexOfFirst { it.image == image }
        adapter.setSelectedPosition(currentSelectedPosition)
        binding.lpMainIv.setImageResource(image)
//        viewAdapter.notifyItemChanged(currentSelectedPosition)
//        Toast.makeText(requireContext(), currentSelectedPosition, Toast.LENGTH_SHORT).show()
//        Glide.with(requireContext()).load(image).into(binding.imageView2)

    }
}