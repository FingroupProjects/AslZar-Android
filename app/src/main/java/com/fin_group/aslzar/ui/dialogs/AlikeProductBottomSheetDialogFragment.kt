package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentAlikeProductBottomSheetDialogBinding
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.SimilarProduct
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class AlikeProductBottomSheetDialogFragment : BaseBottomSheetDialogFragment(),
    OnImageClickListener {

    private var _binding: FragmentAlikeProductBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var alikeProductID: String = ""
    lateinit var recyclerView: RecyclerView

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    var alikeImageList: List<String> = emptyList()

    lateinit var adapter: ProductSomeImagesAdapter
    private lateinit var similarProduct: SimilarProduct
    private lateinit var fullSimilarProduct: Product

    lateinit var progressBar: ProgressBar
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager

    companion object {
        fun newInstance(product: SimilarProduct): AlikeProductBottomSheetDialogFragment {
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
        apiClient = ApiClient()
        sessionManager = SessionManager(requireContext())
        apiClient.init(sessionManager, binding.root)
        progressBar = binding.progressLinearDeterminate
        arguments?.let {
//            alikeProductID = it.getString("likeProductId", "")
            similarProduct = it.getParcelable(ARG_PRODUCT)!!
        }
        recyclerView = binding.lpSomeImagesRv
        adapter = ProductSomeImagesAdapter(alikeImageList, this)
        setProductImages(similarProduct.img)
        getSimilarProduct()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setInitialProductAndInterface()
        binding.apTitle.text = similarProduct.full_name

        binding.apply {
            close.setOnClickListener { dismiss() }
            addToCart.setOnClickListener {
                if (fullSimilarProduct != null){
                    sharedViewModel.onProductAddedToCart(fullSimilarProduct, requireContext())

                    val addedProduct = Cart.getProductById(similarProduct.id)
                    if (addedProduct != null){
                        Toast.makeText(requireContext(), "Количество товара увеличено на +1", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Не удалось добавить товар, загрузите данные заново и повторите попытку.", Toast.LENGTH_SHORT).show()
                }

            }
            refresh.setOnClickListener {
                getSimilarProduct()
            }
        }
    }


    private fun setInitialProductAndInterface() {
        setDataProduct(fullSimilarProduct)
        adapter.setSelectedPosition(0)
        adapter.updateList(similarProduct.img)
    }

    private fun setProductImages(imageList: List<String>) {
        Glide.with(requireContext())
            .load(imageList[0])
            .transform(RoundedCorners(10))
            .error(R.drawable.ic_no_image)
            .into(binding.lpMainIv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
        recyclerView.adapter = adapter
        adapter.updateList(imageList)
    }

    private fun setDataProduct(product: Product) {
        if (product.img.size <= 1){
            binding.lpSomeImagesRv.visibility = GONE
        } else {
            binding.lpSomeImagesRv.visibility = VISIBLE
        }

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
        currentSelectedPosition = similarProduct.img.indexOfFirst { it == image }
        adapter.setSelectedPosition(currentSelectedPosition)
        Glide.with(requireContext())
            .load(image)
            .transform(RoundedCorners(10))
            .error(R.drawable.ic_no_image)
            .into(binding.lpMainIv)
        adapter.notifyItemChanged(currentSelectedPosition)

//        currentSelectedPosition = similarProduct.img.indexOfFirst { it == image }
//        productSomeImagesAdapter.setSelectedPosition(currentSelectedPosition)
//        Glide.with(requireContext()).load(image).into(binding.imageView2)
    }

    private fun getSimilarProduct() {
        progressBar.visibility = VISIBLE
        try {
            val call = apiClient.getApiService().getProductByID("Bearer ${sessionManager.fetchToken()}", similarProduct.id)
            call.enqueue(object : Callback<Product?> {
                override fun onResponse(
                    call: Call<Product?>,
                    response: Response<Product?>
                ) {
                    progressBar.visibility = GONE
                    if (response.isSuccessful) {
                        val similarProductResponse = response.body()
                        if (similarProductResponse != null) {
                            fullSimilarProduct = similarProductResponse
                            setDataProduct(fullSimilarProduct)
                            setInitialProductAndInterface()
//                            setProductImages(fullSimilarProduct.img)
                        }
                    }
                }

                override fun onFailure(call: Call<Product?>, t: Throwable) {
                    Toast.makeText(requireContext(), "Загрузка прошла не успешно, пожалуйста повторите попытку", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = GONE
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        } catch (e: Exception) {
            progressBar.visibility = GONE
            Log.d("TAG", "getSimilarProduct: ${e.message}")
        }
    }

}
