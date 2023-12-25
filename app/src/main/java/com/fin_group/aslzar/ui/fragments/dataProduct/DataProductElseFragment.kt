package com.fin_group.aslzar.ui.fragments.dataProduct

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.AlikeProductsAdapter
import com.fin_group.aslzar.adapter.ProductCharacteristicAdapter
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentDataProductElseBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.fetchCoefficientPlanFromApi
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.fetchCoefficientPlanFromPrefs
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.getProductByIDElse
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.printPercent
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.productCharacteristicElse
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.retrieveCoefficientPlan
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.setDataProduct
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.someImagesProduct
import com.fin_group.aslzar.util.AddingProduct
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.FilialListener
import com.fin_group.aslzar.util.FilterViewModel
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.OnProductCharacteristicClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.formatNumber
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
class DataProductElseFragment : Fragment(), AddingProduct, OnImageClickListener,
    OnProductCharacteristicClickListener, FilialListener {

    private var _binding: FragmentDataProductElseBinding? = null
    private val binding get() = _binding!!

    val args by navArgs<DataProductElseFragmentArgs>()
    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var badgeManager: BadgeManager
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var product: ResultX

    lateinit var preferences: SharedPreferences

    lateinit var recyclerViewSomeImages: RecyclerView
    lateinit var recyclerViewLikeProducts: RecyclerView
    private var currentSelectedPosition = RecyclerView.NO_POSITION

    private lateinit var toolbar: MaterialToolbar

    var imageList: List<String> = emptyList()

    lateinit var productSomeImagesAdapter: ProductSomeImagesAdapter

    lateinit var sessionManager: SessionManager
    lateinit var apiService: ApiClient

    lateinit var percentInstallment: PercentInstallment
    lateinit var adapterPaymentPercent: TableInstallmentAdapter

    lateinit var characteristicRv: RecyclerView
    lateinit var productCharacteristicAdapter: ProductCharacteristicAdapter
    var characteristicList: List<Type> = emptyList()
    private var nextCharacteristic = RecyclerView.NO_POSITION

    lateinit var selectedCharacteristic: Type

    lateinit var filterViewModel: FilterViewModel
    var filterModel: FilterModel? = null

    lateinit var selectedCount: Count

    lateinit var listener1: AddingProduct
    lateinit var listener2: FilialListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductElseBinding.inflate(inflater, container, false)
        filterViewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]

        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        swipeRefreshLayout = binding.swipeRefreshLayout
        badgeManager = BadgeManager(requireContext(), "data_product_else_badge_prefs")
        preferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!

        listener1 = this
        listener2 = this

        percentInstallment = try {
            retrieveCoefficientPlan(binding)
        } catch (e: Exception) {
            Log.e("DataProductFragment", "Ошибка при получении коэффициентов: ${e.message}")
            fetchCoefficientPlanFromApi(binding)
            PercentInstallment(0, 0, 7, emptyList())
        }

        product = args.product
        fetchCoefficientPlanFromPrefs(binding)

        percentInstallment = retrieveCoefficientPlan(binding)
        adapterPaymentPercent = TableInstallmentAdapter(percentInstallment, product.price, 0.0)

        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.title = product.full_name
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)
        recyclerViewSomeImages = binding.otherImgRv

        productSomeImagesAdapter = ProductSomeImagesAdapter(imageList, this)
        productSomeImagesAdapter.setSelectedPosition(0)

        someImagesProduct()
        productCharacteristicElse(binding)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataProduct(product, binding)
        swipeRefreshLayout.setOnRefreshListener {
            getProductByIDElse(binding)
            fetchCoefficientPlanFromApi(binding)
        }
    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }

    override fun addProduct(product: ResultX, type: Type, count: Count) {
        Toast.makeText(requireContext(), "Товар добавлен в корзину: ${product.full_name}", Toast.LENGTH_SHORT).show()
        sharedViewModel.onProductAddedToCartV2(product, requireContext(), type, count)
    }

    override fun addFilial(product: ResultX, type: Type, filial: Count) {
        Toast.makeText(requireContext(), "Товар добавлен в корзину: ${product.full_name}", Toast.LENGTH_SHORT).show()
        sharedViewModel.onProductAddedToCartV2(product, requireContext(), type, filial)
    }

    override fun setImage(image: String) {
        currentSelectedPosition = imageList.indexOfFirst { it == image }
        productSomeImagesAdapter.setSelectedPosition(currentSelectedPosition)
        Glide.with(requireContext()).load(image).into(binding.imageView2)
    }

    override fun clickCharacteristic(characteristic: Type) {
        selectedCharacteristic = characteristic
        if (selectedCharacteristic != null){
            selectedCount = selectedCharacteristic.counts[0]
        }
        nextCharacteristic = characteristicList.indexOfFirst { it == characteristic }
        productCharacteristicAdapter.setSelectedPosition(nextCharacteristic)
    }

    override fun showProductDialog(product: Type) {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        val countsList = product.counts.map {
            "\nЦена: ${formatNumber(it.price)}" +
                    "\nФиллиал: ${it.filial}" +
                    "\nВитрина: ${it.sclad}\n\n"
        }.toTypedArray()
        if (countsList.size > 1) {
            alertDialogBuilder.setTitle("Выберите филиал")
            alertDialogBuilder.setItems(countsList) { _, which ->
                val selectedCount = product.counts[which]
                binding.tvPriceFirst.text = formatNumber(selectedCount.price)
                sharedViewModel.selectedPrice.postValue(selectedCount.price.toDouble())
                val tvPriceFirstSecond = selectedCount.price
                binding.installmentPrice.text = null
                binding.withFirstPay.visibility = View.GONE
                binding.tvWithFirstPay.visibility = View.GONE
                binding.installmentPrice.error = null
                binding.tvFilial.text = selectedCount.filial
                binding.tvVitrina.text = selectedCount.sclad
                printPercent(binding, percentInstallment, tvPriceFirstSecond)
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }else {
            val count = product.counts[0]
            sharedViewModel.selectedPrice.postValue(count.price.toDouble())
            binding.tvPriceFirst.text = formatNumber(count.price)
            val tvPriceFirstSecond = count.price
            binding.tvFilial.text = count.filial
            binding.tvVitrina.text = count.sclad
            binding.installmentPrice.text = null
            binding.withFirstPay.visibility = View.GONE
            binding.tvWithFirstPay.visibility = View.GONE
            printPercent(binding, percentInstallment, tvPriceFirstSecond)
        }
    }
}