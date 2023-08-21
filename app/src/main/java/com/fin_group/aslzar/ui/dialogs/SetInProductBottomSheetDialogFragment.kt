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
import com.fin_group.aslzar.models.ImageDataModel2
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.util.OnImageClickListener
import com.fin_group.aslzar.util.hideBottomNav
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SetInProductBottomSheetDialogFragment : BottomSheetDialogFragment(), OnImageClickListener {

    private var _binding: FragmentSheetDialogSetInProductBottomBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    var imageList: List<ImageDataModel2> = emptyList()
    lateinit var setProduct: BottomSheetItemAdapter

    private var setInProductId: String = ""

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

        arguments?.let {
            setInProductId = it.getString("setInProductId", "")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProduct = BottomSheetItemAdapter(imageList, this)
        setProduct.setSelectedPositions(0)
        setProductBottomSheet()
        setProduct()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setProduct() {
        imageList = listOf(
            ImageDataModel2("00011", R.drawable.ring_2, "Кольцо 11"),
            ImageDataModel2("00012", R.drawable.ring_3, "Кольцо 12"),
            ImageDataModel2("00013", R.drawable.ring_7, "Кольцо 13"),
            ImageDataModel2("00014", R.drawable.ring_4, "Кольцо 14"),
            ImageDataModel2("00015", R.drawable.ring_6, "Кольцо 15"),
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = setProduct
        setProduct.updateList(imageList)
    }

    override fun setImage(image: Int) {
        currentSelectedPosition = imageList.indexOfFirst { it.image == image }
        setProduct.setSelectedPositions(currentSelectedPosition)
        binding.mainImageView.setImageResource(image)
//        viewAdapter.notifyItemChanged(currentSelectedPosition)
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