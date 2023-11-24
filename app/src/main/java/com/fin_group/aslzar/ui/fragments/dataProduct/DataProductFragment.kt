package com.fin_group.aslzar.ui.fragments.dataProduct

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.AlikeProductsAdapter
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.SimilarProduct
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.ui.dialogs.AlikeProductBottomSheetDialogFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.callSetInProduct
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.chipGroup
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.fetchCoefficientPlanFromApi
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.fetchCoefficientPlanFromPrefs
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.getProductByID
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.getSimilarProducts
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.likeProducts
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.onBackPressed
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.retrieveCoefficientPlan
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.setDataProduct
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.someImagesProduct
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.OnAlikeProductClickListener
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


@Suppress("DEPRECATION")
class DataProductFragment : Fragment(), OnImageClickListener, OnAlikeProductClickListener {

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
    lateinit var toolbar: MaterialToolbar

    var imageList: List<String> = emptyList()
    var alikeProductsList: List<SimilarProduct> = emptyList()
    lateinit var productSomeImagesAdapter: ProductSomeImagesAdapter
    lateinit var productAlikeAdapter: AlikeProductsAdapter

    private lateinit var weightChipGroup: ChipGroup
    var weightSelected: String? = ""

    lateinit var sizeChipGroup: ChipGroup
    var sizeSelected: String? = ""

    var getSimilarProduct: List<SimilarProduct> = emptyList()
    lateinit var sessionManager: SessionManager


    lateinit var apiService: ApiClient

    var isFilterOn: Boolean = false
    var filterBadge: BadgeDrawable? = null

    lateinit var percentInstallment: PercentInstallment

    lateinit var monthLinearLayout: LinearLayoutCompat
    lateinit var percentLinearLayout: LinearLayoutCompat

    lateinit var adapterPaymentPercent: TableInstallmentAdapter
    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductBinding.inflate(inflater, container, false)
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
        if (product.category_id == "") {
            getProductByID()
        }
        fetchCoefficientPlanFromPrefs()
        try {
            percentInstallment = retrieveCoefficientPlan()
        }catch (e: Exception){
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
        weightChipGroup = binding.weightChipGroup
        sizeChipGroup = binding.sizeChipGroup

        productSomeImagesAdapter = ProductSomeImagesAdapter(imageList, this)
        productSomeImagesAdapter.setSelectedPosition(0)

        productAlikeAdapter = AlikeProductsAdapter(alikeProductsList, this)

        someImagesProduct()
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
        }

        chipGroup(binding)

    }
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
            if (product.types.isNotEmpty()) {
                val firstType: Type = product.types[0]
                if (firstType.counts.isNotEmpty()) {
                    callInStockDialog(product.full_name, firstType.counts)
                } else {
                    Toast.makeText(requireContext(), "Данного продукта нет в наличии", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Данного продукта нет в наличии", Toast.LENGTH_SHORT).show()
            }
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
}