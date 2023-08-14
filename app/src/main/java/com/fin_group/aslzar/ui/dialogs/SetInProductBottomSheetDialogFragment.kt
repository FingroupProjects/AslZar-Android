package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.BottomSheetItemAdapter
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.databinding.FragmentSheetDialogSetInProductBottomBinding
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.hideBottomNav
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SetInProductBottomSheetDialogFragment : BottomSheetDialogFragment(), OnImageClickListener {

    private var _binding: FragmentSheetDialogSetInProductBottomBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    var imageList: List<ImageDataModel> = emptyList()
    lateinit var setProduct: BottomSheetItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSheetDialogSetInProductBottomBinding.inflate(inflater, container, false)
        recyclerView = binding.spSomeImagesRv

        setProduct = BottomSheetItemAdapter(imageList, this)
        setProduct.setSelectedPositions(0)
        setProductBottomSheet()
        setProduct()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setProduct() {
        imageList = listOf(
            ImageDataModel(R.drawable.ring_2, "Test"),
            ImageDataModel(R.drawable.ring_3, "Test"),
            ImageDataModel(R.drawable.ring_4, "Test"),
            ImageDataModel(R.drawable.ring_6, "Test"),
            ImageDataModel(R.drawable.ring_7, "Test"),
            ImageDataModel(R.drawable.ring_2, "Test"),
            ImageDataModel(R.drawable.ring_3, "Test"),
            ImageDataModel(R.drawable.ring_4, "Test"),
            ImageDataModel(R.drawable.ring_6, "Test"),
            ImageDataModel(R.drawable.ring_7, "Test")
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = setProduct
        setProduct.updateList(imageList)
    }

    override fun setImage(image: Int) {
        currentSelectedPosition = imageList.indexOfFirst { it.image == image }
        setProduct.setSelectedPositions(currentSelectedPosition)
//        viewAdapter.notifyItemChanged(currentSelectedPosition)
//        Toast.makeText(requireContext(), currentSelectedPosition, Toast.LENGTH_SHORT).show()
        binding.mainImageView.setImageResource(image)

//        Glide.with(requireContext()).load(image).into(binding.imageView2)
    }

    private fun setProductBottomSheet() {
        binding.apply {
            goTo.setOnClickListener {
                Toast.makeText(requireContext(), "Информация о продукте", Toast.LENGTH_SHORT).show()

            }
            add.setOnClickListener {
                Toast.makeText(requireContext(), "Добавление в корзину", Toast.LENGTH_SHORT).show()
            }
        }
    }

}