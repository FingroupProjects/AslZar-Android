package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
        fun newInstance(product: Product): AlikeProductBottomSheetDialogFragment {
            val dialog = AlikeProductBottomSheetDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, product)
            dialog.arguments = args
            return dialog
        }

//        fun newInstance(likeProductId: String): AlikeProductBottomSheetDialogFragment {
//            val dialog = AlikeProductBottomSheetDialogFragment()
//            val args = Bundle()
//            args.putString("likeProductId", likeProductId)
//            dialog.arguments = args
//            return dialog
//        }

        private const val ARG_PRODUCT = "alikeProduct"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlikeProductBottomSheetDialogBinding.inflate(inflater, container, false)

        arguments?.let {
//            alikeProductID = it.getString("likeProductId", "")
            similarProduct = it.getParcelable(ARG_PRODUCT)!!
        }
//        binding.lpTitle.text = alikeProductID
        adapter = ProductSomeImagesAdapter(alikeImageList, this)
        setDataProduct(similarProduct)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.lpSomeImagesRv

        val inStockList = listOf(
            InStock("Магазин 1", "Витрина 3", 8, 0),
            InStock("Магазин 2", "Витрина 8", 8, 0),
            InStock("Магазин 12", "Витрина 7", 8, 0),
            InStock("Магазин 5", "Витрина 6", 8, 0)
        )

//        similarProduct = Product(
//            id = "0000022",
//            full_name = "Кольцо с аметистом",
//            name = "23..789.77",
//            price = 120.0,
//            category_id = "jewelry",
//            sale = 0.2,
//            color = "фиолетовый",
//            stone_type = "аметист",
//            metal = "серебро",
//            content = "Серьги с натуральным аметистом",
//            size = "малый",
//            weight = "5 г",
//            country_of_origin = "Индия",
//            provider = "Украшения Востока",
//            counts = inStockList,
//            img = listOf(
//                "https://cdn2.thecatapi.com/images/2n3.jpg",
//                "https://cdn2.thecatapi.com/images/2qo.jpg"
//            )
//        )
        

        binding.apply {
            close.setOnClickListener { dismiss() }
            addToCart.setOnClickListener {
                sharedViewModel.onProductAddedToCart(similarProduct, requireContext())
                Toast.makeText(requireContext(), "Товар добавлен ", Toast.LENGTH_SHORT).show()
            }
        }

        setProducts()
    }

    private fun setProducts() {
        alikeImageList = listOf(
            ImageDataModel("http://convertolink.taskpro.tj/photoLink/public/storage/images/PlNk0wsmedvtLhkPu7wzj7Sk7OIiaKJosxy8NidO.png", "Кольцо 2"),
            ImageDataModel("http://convertolink.taskpro.tj/photoLink/public/storage/images/85cIg9T9cwf3fevuQJ8rnGxrrG80Jh5mHatHRZWr.png", "Кольцо 3"),
            ImageDataModel("http://convertolink.taskpro.tj/photoLink/public/storage/images/oB9W5AC6jBQeFScqr8YFjRs81tCekLKYRe8cHSrH.png", "Кольцо 4"),
            ImageDataModel("http://convertolink.taskpro.tj/photoLink/public/storage/images/hIu6UbR6WAiCK1YYLUqd6KvOKYU5lzMHoYrLmqjW.png", "Кольцо 5"),
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
        recyclerView.adapter = adapter
        adapter.updateList(alikeImageList)
    }

    private fun setDataProduct(product: Product){
        binding.apply {
            apTitle.text = product.full_name
            apCode.text = product.name
            apPrice.text = product.price.toString()
            apStone.text = product.stone_type
            apProbe.text = product.content
            apMetal.text = product.metal
            apWeight.text = product.weight
            apSize.text = product.size
        }
    }

    override fun setImage(image: String) {
        currentSelectedPosition = alikeImageList.indexOfFirst { it.image == image }
        adapter.setSelectedPosition(currentSelectedPosition)
        Glide.with(requireContext())
            .load(image)
            .transform(RoundedCorners(10))
            .error(R.drawable.ic_no_image)
            .into(binding.lpMainIv)
        //binding.lpMainIv.setImageResource(image)
        adapter.notifyItemChanged(currentSelectedPosition)
    }
}