package com.fin_group.aslzar.ui.fragments.dataProduct

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.AlikeProductsAdapter
import com.fin_group.aslzar.adapter.ProductCharacteristicAdapter
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.SimilarProduct
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.ui.dialogs.AlikeProductBottomSheetDialogFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.callSetInProduct
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.fetchCoefficientPlanFromApi
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.fetchCoefficientPlanFromPrefs
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.getProductByID
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.getSimilarProducts
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.likeProducts
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.onBackPressed
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.printPercent
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.productCharacteristic
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.retrieveCoefficientPlan
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.setDataProduct
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.showProductCharacteristicDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.someImagesProduct
import com.fin_group.aslzar.util.AddingProduct
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.FilialListener
import com.fin_group.aslzar.util.FilterViewModel
import com.fin_group.aslzar.util.OnAlikeProductClickListener
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.OnProductCharacteristicClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.formatNumber
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder


@Suppress("DEPRECATION")
class DataProductFragment : Fragment(), OnImageClickListener, OnAlikeProductClickListener,
    AddingProduct, FilialListener, OnProductCharacteristicClickListener {

    private var _binding: FragmentDataProductBinding? = null
    val binding get() = _binding!!
    val args by navArgs<DataProductFragmentArgs>()
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
    var alikeProductsList: List<SimilarProduct> = emptyList()
    lateinit var productSomeImagesAdapter: ProductSomeImagesAdapter
    lateinit var productAlikeAdapter: AlikeProductsAdapter
    var getSimilarProduct: List<SimilarProduct> = emptyList()
    lateinit var sessionManager: SessionManager
    lateinit var apiService: ApiClient
    lateinit var percentInstallment: PercentInstallment
    lateinit var monthLinearLayout: LinearLayoutCompat
    lateinit var percentLinearLayout: LinearLayoutCompat
    lateinit var adapterPaymentPercent: TableInstallmentAdapter

    lateinit var characteristicRv: RecyclerView
    lateinit var productCharacteristicAdapter: ProductCharacteristicAdapter
    var characteristicList: List<Type> = emptyList()
    private var nextCharacteristic = RecyclerView.NO_POSITION

    lateinit var selectedCharacteristic: Type

    lateinit var filterViewModel: FilterViewModel
    var filterModel: FilterModel? = null


    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductBinding.inflate(inflater, container, false)
        filterViewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]

        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        swipeRefreshLayout = binding.swipeRefreshLayout
        badgeManager = BadgeManager(requireContext(), "data_product_badge_prefs")
        preferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        monthLinearLayout = binding.monthTable

        percentLinearLayout = binding.percentTable
        percentInstallment = try {
            retrieveCoefficientPlan()
        } catch (e: Exception) {
            Log.e("DataProductFragment", "Ошибка при получении коэффициентов: ${e.message}")
            fetchCoefficientPlanFromApi()
            PercentInstallment(0, 0, 7, emptyList())
        }

        onBackPressed()
        if (args.product != null) {
            product = args.product!!
        } else {
            getProductByID()
        }

        binding.tvCode.setOnClickListener {
            val action = DataProductFragmentDirections.actionDataProductFragmentToDataProductElseFragment2(product.id, product)
            findNavController().navigate(action)
        }
        if (product.category_id == "") {
            getProductByID()
        }
        fetchCoefficientPlanFromPrefs()
        try {
            percentInstallment = retrieveCoefficientPlan()
        } catch (e: Exception) {
            Log.d("TAG", "onCreateView: ${e.message}")
        }
        adapterPaymentPercent = TableInstallmentAdapter(percentInstallment, product.price, 0.0)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.title = product.full_name
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        hideBottomNav()
        setHasOptionsMenu(true)
        recyclerViewSomeImages = binding.otherImgRv
        recyclerViewLikeProducts = binding.likeProductsRv

        productSomeImagesAdapter = ProductSomeImagesAdapter(imageList, this)
        productSomeImagesAdapter.setSelectedPosition(0)
        productAlikeAdapter = AlikeProductsAdapter(alikeProductsList, this)

        someImagesProduct()
        productCharacteristic()
        likeProducts()
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataProduct(product, binding)
        getSimilarProducts()
        swipeRefreshLayout.setOnRefreshListener {
            getProductByID()
            fetchCoefficientPlanFromApi()
            //binding.installmentPrice.text = null
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (product.is_set && product.types.isNotEmpty() && product.types[0].counts.isNotEmpty()) {
            inflater.inflate(R.menu.product_data_menu, menu)
        } else if (product.is_set && (product.types.isEmpty() || product.types[0].counts.isEmpty())) {
            inflater.inflate(R.menu.product_data_menu_3, menu)
        } else if (!product.is_set && product.types.isNotEmpty() && product.types[0].counts.isNotEmpty()) {
            inflater.inflate(R.menu.product_data_menu_2, menu)
        } else {
            inflater.inflate(R.menu.product_data_menu_4, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("UnsafeOptInUsageError")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.product_set_item) {
            if (product.is_set) {
                callSetInProduct(args.productId)
            } else {
                Toast.makeText(requireContext(), "У данного продукта нет комплекта", Toast.LENGTH_SHORT).show()
            }
        }
        if (item.itemId == R.id.product_in_stock_item) {
            showProductCharacteristicDialog(product)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }

    override fun setImage(image: String) {
        currentSelectedPosition = imageList.indexOfFirst { it == image }
        productSomeImagesAdapter.setSelectedPosition(currentSelectedPosition)
        Glide.with(requireContext()).load(image).into(binding.imageView2)
    }

    override fun callBottomDialog(product: SimilarProduct) {
        val fragmentManager = requireFragmentManager()
        val tag = "alike_product_dialog"
        val existingFragment = fragmentManager.findFragmentByTag(tag)
        if (existingFragment == null) {
            val bottomSheetFragment = AlikeProductBottomSheetDialogFragment.newInstance(product)
            bottomSheetFragment.show(fragmentManager, tag)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNav()
    }
    override fun addProduct(product: ResultX, type: Type, count: Count) {
        Toast.makeText(requireContext(), "Товар добавлен в корзину: ${product.full_name}", Toast.LENGTH_SHORT).show()
        sharedViewModel.onProductAddedToCartV2(product, requireContext(), type, count)
    }

    override fun addFilial(product: ResultX, type: Type, filial: Count) {
        Toast.makeText(requireContext(), "Товар добавлен в корзину: ${product.full_name}", Toast.LENGTH_SHORT).show()
        sharedViewModel.onProductAddedToCartV2(product, requireContext(), type, filial)
    }

    override fun clickCharacteristic(characteristic: Type) {
        selectedCharacteristic = characteristic
        nextCharacteristic = characteristicList.indexOfFirst { it == characteristic }
        productCharacteristicAdapter.setSelectedPosition(nextCharacteristic)
        //binding.installmentPrice.text = null
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
                printPercent(binding, percentInstallment, tvPriceFirstSecond)
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }else {
            val count = product.counts[0]
            sharedViewModel.selectedPrice.postValue(count.price.toDouble())
            binding.tvPriceFirst.text = formatNumber(count.price)
            val tvPriceFirstSecond = count.price
            printPercent(binding, percentInstallment, tvPriceFirstSecond)
        }
    }

}