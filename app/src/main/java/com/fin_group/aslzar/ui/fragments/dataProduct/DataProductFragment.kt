package com.fin_group.aslzar.ui.fragments.dataProduct

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.callInStockDialog
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.displayList
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.hideBottomNav
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


@Suppress("DEPRECATION")
class DataProductFragment : Fragment(), OnImageClickListener {

    private var _binding: FragmentDataProductBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DataProductFragmentArgs>()

    lateinit var recyclerViewSomeImages: RecyclerView
    private var currentSelectedPosition = RecyclerView.NO_POSITION
    var imageList: List<ImageDataModel> = emptyList()
    lateinit var productSomeImagesAdapter: ProductSomeImagesAdapter

    lateinit var sizeChipGroup: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductBinding.inflate(inflater, container, false)
        hideBottomNav()
        setHasOptionsMenu(true)
        recyclerViewSomeImages = binding.otherImgRv
        sizeChipGroup = binding.sizeChipGroup

        productSomeImagesAdapter = ProductSomeImagesAdapter(imageList, this)
        productSomeImagesAdapter.setSelectedPosition(0)

        displayList(this)

        return binding.root
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sizeList = listOf("1.5", "1.7", "1.8", "2.0", "2.1", "2.3")
        for (size in sizeList) {
            val chip = Chip(requireContext())
            chip.text = size
            chip.isCheckable = true
            chip.setChipBackgroundColorResource(R.color.background_1)
            chip.setTextColor(resources.getColorStateList(R.color.text_color_1))
            chip.setChipStrokeColorResource(R.color.border_color_1)
            chip.chipStrokeWidth = 2F
            chip.isCheckable = true
            

            sizeChipGroup.addView(chip)
        }

//        for (size in sizeList) {
//            val button = MaterialButton(requireContext())
//            button.id = View.generateViewId()
//            button.text = size
//            button.isCheckable = true
//            button.isClickable = true
//            button.setTextColor(resources.getColor(R.color.text_color_1))
//            button.strokeWidth = 3
//            button.setStrokeColorResource(R.color.border_color_1)
//            button.width = 20
//            button.height = LayoutParams.WRAP_CONTENT
//
////            val layoutParams = LinearLayout.LayoutParams(
////                LinearLayout.LayoutParams.WRAP_CONTENT,
////                LinearLayout.LayoutParams.WRAP_CONTENT
////            )
////            layoutParams.setMargins(2, 0, 2, 0) // Опционально, для добавления отступов между кнопками
////            button.layoutParams = layoutParams
//
//            sizeToggleGroup.addView(button)
//        }

        sizeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedChip = view.findViewById<Chip>(checkedId)
            val selectedSize = selectedChip?.text.toString()

            binding.textView21.text = "Выбранный размер: $selectedSize"
        }

//        sizeChipGroup.setOnCheckedChangeListener { group, checkedId ->
//            val selectedChip = group.findViewById<Chip>(checkedId)
//            if (selectedChip != null) {
//                val selectedSize = selectedChip.text.toString()
//                // Обработка выбора размера
//                binding.textView21.text = "Выбранный размер: $selectedSize"
//            }
//        }


        binding.textView6.text = args.productId
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_data_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.product_set_item){
            Toast.makeText(requireContext(), "В комплекте", Toast.LENGTH_SHORT).show()
        }
        if (item.itemId == R.id.product_in_stock_item){
            callInStockDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }

    override fun setImage(image: Int) {
        currentSelectedPosition = imageList.indexOfFirst { it.image == image }
        productSomeImagesAdapter.setSelectedPosition(currentSelectedPosition)
//        viewAdapter.notifyItemChanged(currentSelectedPosition)
//        Toast.makeText(requireContext(), currentSelectedPosition, Toast.LENGTH_SHORT).show()
        binding.imageView2.setImageResource(image)

//        Glide.with(requireContext()).load(image).into(binding.imageView2)
    }
}