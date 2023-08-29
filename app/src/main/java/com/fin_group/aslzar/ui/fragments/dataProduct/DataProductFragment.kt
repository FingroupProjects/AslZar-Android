package com.fin_group.aslzar.ui.fragments.dataProduct

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.AlikeProductsAdapter
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.models.ImageDataModel2
import com.fin_group.aslzar.ui.dialogs.AlikeProductBottomSheetDialogFragment
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.callSetInProduct
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.likeProducts
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.someImagesProduct
import com.fin_group.aslzar.util.OnAlikeProductClickListener
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import com.fin_group.aslzar.util.showToolBar
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.ChipGroup


@Suppress("DEPRECATION")
class DataProductFragment : Fragment(), OnImageClickListener, OnAlikeProductClickListener {

    private var _binding: FragmentDataProductBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DataProductFragmentArgs>()
    val sharedViewModel: SharedViewModel by activityViewModels()


    lateinit var recyclerViewSomeImages: RecyclerView
    lateinit var recyclerViewLikeProducts: RecyclerView

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    lateinit var toolbar: MaterialToolbar

    var imageList: List<ImageDataModel> = emptyList()
    var alikeProductsList: List<ImageDataModel2> = emptyList()
    lateinit var productSomeImagesAdapter: ProductSomeImagesAdapter
    lateinit var productAlikeAdapter: AlikeProductsAdapter

    lateinit var weightChipGroup: ChipGroup
    var weightSelected: String? = ""

    lateinit var sizeChipGroup: ChipGroup
    var sizeSelected: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductBinding.inflate(inflater, container, false)
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

        return binding.root
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val weightList = listOf("1.5", "1.7", "1.8", "2.0", "2.1", "2.3")
//        val sizeList = listOf("10.5", "12.1", "13.5", "13.7", "14", "14.8")
//        callWeightChipGroup(weightList)
//        callSizeChipGroup(sizeList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_data_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.product_set_item){
            callSetInProduct(args.productId)
        }
        if (item.itemId == R.id.product_in_stock_item){
            callInStockDialog(args.productId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }

    override fun setImage(image: String) {
        currentSelectedPosition = imageList.indexOfFirst { it.image == image }
        productSomeImagesAdapter.setSelectedPosition(currentSelectedPosition)
//        binding.imageView2.setImageResource(image)
//        viewAdapter.notifyItemChanged(currentSelectedPosition)
//        Toast.makeText(requireContext(), currentSelectedPosition, Toast.LENGTH_SHORT).show()

        Glide.with(requireContext()).load(image).into(binding.imageView2)
    }

    override fun callBottomDialog(id: String) {
        val fragmentManager = requireFragmentManager()
        val tag = "alike_product_dialog"
        val existingFragment = fragmentManager.findFragmentByTag(tag)

        if (existingFragment == null) {
            val bottomSheetFragment = AlikeProductBottomSheetDialogFragment.newInstance(id)
            bottomSheetFragment.show(fragmentManager, tag)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNav()
    }
}