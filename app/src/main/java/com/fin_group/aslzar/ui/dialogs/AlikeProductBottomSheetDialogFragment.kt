package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.InStockAdapter
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentAlikeProductBottomSheetDialogBinding
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.SimilarProduct
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragment
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
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

        private const val ARG_PRODUCT = "alikeProduct"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlikeProductBottomSheetDialogBinding.inflate(inflater, container, false)
        apiClient = ApiClient()
        sessionManager = SessionManager(requireContext())
        apiClient.init(sessionManager)
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
        Log.d("TAG", "onViewCreated: $similarProduct")
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

            toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
                if (isChecked) {
                    val isDataProductSelected = checkedId == R.id.dataProductBtn2
                    dataProductLayout2.visibility = if (isDataProductSelected) VISIBLE else View.INVISIBLE
                    inStockProductLayout2.visibility = if (isDataProductSelected) View.INVISIBLE else VISIBLE
                }
            }
        }
    }

    private fun setInitialProductAndInterface() {
        setDataProduct(fullSimilarProduct)
        Log.d("TAG", "setInitialProductAndInterface: $fullSimilarProduct")

        if (fullSimilarProduct.counts.isEmpty()){
            binding.apply {
                inStockProductLayout2.visibility = GONE
                dataProductLayout2.visibility = VISIBLE
                toggleButton.visibility = GONE
            }
        } else {
            binding.inStockProductLayout2.visibility = INVISIBLE
            binding.dataProductLayout2.visibility = VISIBLE
            binding.toggleButton.visibility = VISIBLE
            binding.rvInStock.visibility = VISIBLE
            binding.inStockError2.visibility = GONE
            binding.textView43.visibility = VISIBLE
            binding.textView44.visibility = VISIBLE
            binding.textView45.visibility = VISIBLE
        }

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

    private fun addToCart(product: Product, productId: String){
        binding.apply {
            addToCart.setOnClickListener {
                Log.d("TAG", "onViewCreated: $product")
                val addedProduct = Cart.getProductById(productId)
                if (addedProduct != null){
                    Toast.makeText(requireContext(), "Количество товара увеличено на +1", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                }
                sharedViewModel.onProductAddedToCart(product, requireContext())
            }
        }
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
                    progressBar.visibility = INVISIBLE
                    if (response.isSuccessful) {
                        val similarProductResponse = response.body()
                        if (similarProductResponse != null) {
                            fullSimilarProduct = similarProductResponse
                            setDataProduct(fullSimilarProduct)
                            setInitialProductAndInterface()

                            binding.inStockError2.visibility = GONE
                            binding.rvInStock.layoutManager = LinearLayoutManager(requireContext())
                            binding.rvInStock.adapter = InStockAdapter(fullSimilarProduct.counts)
                        }
                    }
                }

                override fun onFailure(call: Call<Product?>, t: Throwable) {
                    Toast.makeText(requireContext(), "Загрузка прошла не успешно, пожалуйста повторите попытку", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = INVISIBLE
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        } catch (e: Exception) {
            progressBar.visibility = INVISIBLE
            Log.d("TAG", "getSimilarProduct: ${e.message}")
        }
    }

}
