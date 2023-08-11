package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ViewAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSheetDialogSetInProductBottomBinding.inflate(inflater, container, false)
        hideBottomNav()
        recyclerView = binding.rclView

        setProductBottomSheets(this)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProductBottomSheets(listener: OnImageClickListener) {
        val imageList = ArrayList<ImageDataModel>()
        imageList.clear()
        imageList.add(ImageDataModel(R.drawable.earrings, "Test"))
        imageList.add(ImageDataModel(R.drawable.ring_2, "Test"))
        imageList.add(ImageDataModel(R.drawable.ring_3, "Test"))
        imageList.add(ImageDataModel(R.drawable.ring_4, "Test"))
        imageList.add(ImageDataModel(R.drawable.ring_6, "Test"))
        imageList.add(ImageDataModel(R.drawable.ring_7, "Test"))
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = ViewAdapter(imageList, listener)
    }

    override fun setImage(image: Int) {
        TODO("Not yet implemented")
    }

}