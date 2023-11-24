package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.InStockAdapter
import com.fin_group.aslzar.adapter.SetInProductAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentSheetDialogSetInProductBottomBinding
import com.fin_group.aslzar.response.GetAllProducts
import com.fin_group.aslzar.response.GetAllProductsResponse
import com.fin_group.aslzar.response.InStock
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.ui.activities.LoginActivity
import com.fin_group.aslzar.util.OnProductClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SetInProductBottomSheetDialogFragment : BottomSheetDialogFragment(), OnProductClickListener {

    private var _binding: FragmentSheetDialogSetInProductBottomBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView

    val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var selectedProduct: ResultX

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    lateinit var setInProductAdapter: SetInProductAdapter

    private var setInProductId: String = ""
    var allProducts: List<ResultX> = emptyList()

    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var progressBar: ProgressBar

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
        sessionManager = SessionManager(requireContext())
        apiClient = ApiClient()
        apiClient.init(sessionManager)
        progressBar = binding.progressLinearDeterminate2

        binding.close.setOnClickListener { dismiss() }

        arguments?.let {
            setInProductId = it.getString("setInProductId", "")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSetInProduct()
        setInProductAdapter = SetInProductAdapter(allProducts, this)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = setInProductAdapter
        setInProductAdapter.updateList(allProducts)


        binding.apply {
            close.setOnClickListener { dismiss() }
            refresh.setOnClickListener {
                getSetInProduct()
            }
            addToCart.setOnClickListener {
                if (setInProductId != null && setInProductId.isNotEmpty()) {
                    val addedProduct = Cart.getProductById(setInProductId)
                    if (addedProduct != null) {
                        Toast.makeText(
                            requireContext(),
                            "Количество товара увеличено на +1",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Товар добавлен в корзину",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    sharedViewModel.onProductAddedToCart(selectedProduct, requireContext())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Идентификатор продукта пуст или null",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    val isDataProductSelected = checkedId == R.id.dataProductBtn
                    dataProductLayout.visibility = if (isDataProductSelected) VISIBLE else INVISIBLE
                    inStockProductLayout.visibility =
                        if (isDataProductSelected) INVISIBLE else VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setInitialProductAndInterface() {
        selectedProduct = allProducts[0]
        setDataProduct(selectedProduct)
        if (selectedProduct.types.isEmpty()) {
            val firstType = selectedProduct.types[0]

            if (firstType.counts.isNotEmpty()) {
                binding.apply {
                    inStockProductLayout.visibility = GONE
                    dataProductLayout.visibility = VISIBLE
                    toggleButton.visibility = INVISIBLE
                    textView48.visibility = VISIBLE
                }
            }
        } else {
            val firstType = selectedProduct.types[0]
            if (firstType.counts.isNotEmpty()) {
                binding.apply {
                    inStockProductLayout.visibility = INVISIBLE
                    dataProductLayout.visibility = VISIBLE
                    toggleButton.visibility = VISIBLE
                    textView48.visibility = INVISIBLE
                    rvInStock.visibility = VISIBLE
                    inStockError.visibility = GONE
                    textView33.visibility = VISIBLE
                    textView34.visibility = VISIBLE
                    textView35.visibility = VISIBLE
                    rvInStock.layoutManager = LinearLayoutManager(requireContext())
                    rvInStock.adapter = InStockAdapter(firstType.counts)
                }
            }

        }
        setInProductAdapter.setSelectedPositions(0)
        setInProductAdapter.updateList(allProducts)
    }


    private fun setDataProduct(product: ResultX) {
        binding.apply {
            Glide.with(requireContext()).load(product.img[0]).into(binding.mainImageView)

            sipFullName.text = product.full_name
            sipCode.text = product.name
            sipPrice.text = product.price.toString()
            sipStone.text = product.stone_type
            sipProbe.text = product.proba
            sipMetal.text = product.metal

            val firstType = product.types.firstOrNull()
            sipWeight.text = firstType?.weight.toString()
            sipSize.text = firstType?.size.toString()

        }
    }

    private fun getSetInProduct() {
        progressBar.visibility = VISIBLE
        try {
            val call = apiClient.getApiService()
                .getSetInProduct("Bearer ${sessionManager.fetchToken()}", setInProductId)
            call.enqueue(object : Callback<GetAllProducts?> {
                override fun onResponse(
                    call: Call<GetAllProducts?>,
                    response: Response<GetAllProducts?>
                ) {
                    Log.d("TAG", "onResponse: ${response.code()}")
                    Log.d("TAG", "onResponse: ${response.body()}")
                    progressBar.visibility = INVISIBLE
                    if (response.isSuccessful) {
                        Log.d("TAG", "onResponse: ${response.code()}")
                        Log.d("TAG", "onResponse: ${response.body()}")
                        val products = response.body()
                        if (products?.result != null) {
                            Log.d("TAG", "onResponse: ${response.code()}")
                            Log.d("TAG", "onResponse: ${response.body()}")
                            allProducts = products.result
                            val allCounts = allProducts.flatMap { it.types.flatMap { type -> type.counts } }
                            setInProductAdapter.updateList(allProducts)
                            setInitialProductAndInterface()
                            binding.toggleButton.check(R.id.dataProductBtn)

                            binding.inStockError.visibility = GONE
                            binding.rvInStock.layoutManager = LinearLayoutManager(requireContext())
                            binding.rvInStock.adapter = InStockAdapter(allCounts)
//                        setProductBottomSheet(selectedProduct)
                        } else {
                            val handler = Handler(Looper.getMainLooper())
                            handler.postDelayed({
                                Toast.makeText(
                                    requireContext(),
                                    "У данного товара нет комплекта",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismiss()
                            }, 1000)
                        }
                    }
                }

                override fun onFailure(call: Call<GetAllProducts?>, t: Throwable) {
                    Log.d("TAG", "onFailure: ${t.message}")
                    progressBar.visibility = INVISIBLE
                }
            })
        } catch (e: Exception) {
            progressBar.visibility = INVISIBLE
            Log.d("TAG", "getSetInProduct: ${e.message}")
        }
    }


    override fun setProduct(product: ResultX) {
        val image = product.img[0]
        selectedProduct = product
        setDataProduct(product)
        currentSelectedPosition = allProducts.indexOfFirst { it.id == product.id }
        setInProductAdapter.setSelectedPositions(currentSelectedPosition)
        Glide.with(requireContext()).load(image).into(binding.mainImageView)

        binding.toggleButton.check(R.id.dataProductBtn)
        if (selectedProduct.types.isEmpty()) {
            val firstType = selectedProduct.types[0]
            if (firstType.counts.isEmpty()) {
                binding.apply {
                    inStockProductLayout.visibility = GONE
                    dataProductLayout.visibility = VISIBLE
                    toggleButton.visibility = INVISIBLE
                    textView48.visibility = VISIBLE
                }
            }

        } else {


            val firstType = selectedProduct.types[0]
            if (firstType.counts.isNotEmpty()) {
                binding.apply {
                    inStockProductLayout.visibility = INVISIBLE
                    dataProductLayout.visibility = VISIBLE
                    toggleButton.visibility = VISIBLE
                    textView48.visibility = INVISIBLE
                    rvInStock.visibility = VISIBLE
                    inStockError.visibility = GONE
                    textView33.visibility = VISIBLE
                    textView34.visibility = VISIBLE
                    textView35.visibility = VISIBLE
                    rvInStock.layoutManager = LinearLayoutManager(requireContext())
                    rvInStock.adapter = InStockAdapter(firstType.counts)
                }
            }
        }
    }
}