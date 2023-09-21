package com.fin_group.aslzar.ui.fragments.dataProduct

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.AlikeProductsAdapter
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentCalculatorBinding
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.SimilarProduct
import com.fin_group.aslzar.ui.dialogs.AlikeProductBottomSheetDialogFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.callSetInProduct
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.createTable
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.getProductByID
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.getSimilarProducts
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.likeProducts
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.setDataProduct
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.someImagesProduct
import com.fin_group.aslzar.util.OnAlikeProductClickListener
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.chip.ChipGroup


@Suppress("DEPRECATION")
class DataProductFragment : Fragment(), OnImageClickListener, OnAlikeProductClickListener {

    private var _binding: FragmentDataProductBinding? = null
    val binding get() = _binding!!

    val args by navArgs<DataProductFragmentArgs>()
    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var product: Product

    lateinit var recyclerViewSomeImages: RecyclerView
    lateinit var recyclerViewLikeProducts: RecyclerView

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    lateinit var toolbar: MaterialToolbar

    var imageList: List<String> = emptyList()
    var alikeProductsList: List<SimilarProduct> = emptyList()
    lateinit var productSomeImagesAdapter: ProductSomeImagesAdapter
    lateinit var productAlikeAdapter: AlikeProductsAdapter

    lateinit var weightChipGroup: ChipGroup
    var weightSelected: String? = ""

    lateinit var sizeChipGroup: ChipGroup
    var sizeSelected: String? = ""

    var getSimilarProduct: List<SimilarProduct> = emptyList()
    lateinit var sessionManager: SessionManager

    lateinit var apiService: ApiClient

    var isFilterOn: Boolean = false
    var filterBadge: BadgeDrawable? = null

    lateinit var percentInstallment: PercentInstallment


    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        swipeRefreshLayout = binding.swipeRefreshLayout

        percentInstallment = PercentInstallment(
            90, 15, listOf(
                Percent(6.9, 3),
                Percent(8.9, 6),
                Percent(12.9, 9),
                Percent(17.9, 12),
            )
        )

        if (args.product != null) {
            product = args.product!!
            Log.d("TAG", "onCreateView: $product")
        } else {
            getProductByID()
        }
        if (product.category_id == ""){
            getProductByID()
        }
        Log.d("TAG", "onCreateView: $product")
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.title = product.full_name

        if (product.is_set){
            filterBadge = BadgeDrawable.create(requireContext())
            filterBadge?.isVisible = true
            BadgeUtils.attachBadgeDrawable(filterBadge!!, toolbar, R.id.product_set_item)
        }

//        toolbar = binding.toolbar
//        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar as MaterialToolbar?)
//        toolbar.title = "Данные товара"
//        toolbar.setNavigationIcon(R.drawable.ic_back)
//        toolbar.setNavigationOnClickListener {
//            findNavController().popBackStack()
//        }
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
        createTable(binding, product.price)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataProduct(product, binding)
        getSimilarProducts()

        swipeRefreshLayout.setOnRefreshListener {
            getProductByID()
        }
//        val weightList = listOf("1.5", "1.7", "1.8", "2.0", "2.1", "2.3")
//        val sizeList = listOf("10.5", "12.1", "13.5", "13.7", "14", "14.8")
//        callWeightChipGroup(weightList)
//        callSizeChipGroup(sizeList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_data_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.product_set_item){
            if (product.is_set) {
                callSetInProduct(args.productId)
            }else {
                Toast.makeText(requireContext(), "У данного продукта нет комплекта", Toast.LENGTH_SHORT).show()
            }
        }

        if (item.itemId == R.id.product_in_stock_item){
            if (product.counts.isNotEmpty()){
                callInStockDialog(product.full_name, product.counts)
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
//        binding.imageView2.setImageResource(image)
//        viewAdapter.notifyItemChanged(currentSelectedPosition)
//        Toast.makeText(requireContext(), currentSelectedPosition, Toast.LENGTH_SHORT).show()

    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onDestroyView() {
        super.onDestroyView()

        if (filterBadge != null) {
            filterBadge?.isVisible = false
            BadgeUtils.attachBadgeDrawable(filterBadge!!, toolbar, R.id.product_set_item)
        }
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