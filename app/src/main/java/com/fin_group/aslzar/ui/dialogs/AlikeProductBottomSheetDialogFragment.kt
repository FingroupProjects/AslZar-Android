package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.AlikeProductsAdapter
import com.fin_group.aslzar.adapter.BottomSheetItemAdapter
import com.fin_group.aslzar.adapter.ProductSomeImagesAdapter
import com.fin_group.aslzar.databinding.FragmentAlikeProductBottomSheetDialogBinding
import com.fin_group.aslzar.models.ImageDataModel
import com.fin_group.aslzar.models.ImageDataModel2
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.OnImageClickListener

class AlikeProductBottomSheetDialogFragment : BaseBottomSheetDialogFragment(), OnImageClickListener {

    private var _binding: FragmentAlikeProductBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private var alikeProductID: String = ""
    lateinit var recyclerView: RecyclerView

    private var currentSelectedPosition = RecyclerView.NO_POSITION
    var alikeImageList: List<ImageDataModel> = emptyList()
    lateinit var adapter: ProductSomeImagesAdapter

    companion object {
        fun newInstance(likeProductId: String): AlikeProductBottomSheetDialogFragment {
            val dialog = AlikeProductBottomSheetDialogFragment()
            val args = Bundle()
            args.putString("likeProductId", likeProductId)
            dialog.arguments = args
            return dialog
        }

        private const val ARG_PRODUCT = "product"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlikeProductBottomSheetDialogBinding.inflate(inflater, container, false)

        arguments?.let {
            alikeProductID = it.getString("likeProductId", "")
        }
        binding.lpTitle.text = alikeProductID
        adapter = ProductSomeImagesAdapter(alikeImageList, this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.lpSomeImagesRv
        setProducts()
    }

    private fun setProducts() {
        alikeImageList = listOf(
            ImageDataModel(R.drawable.ring_1, "Кольцо 1"),
            ImageDataModel(R.drawable.ring_3, "Кольцо 2"),
            ImageDataModel(R.drawable.ring_7, "Кольцо 3"),
            ImageDataModel(R.drawable.ring_4, "Кольцо 4"),
            ImageDataModel(R.drawable.ring_6, "Кольцо 5"),
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
        recyclerView.adapter = adapter
        adapter.updateList(alikeImageList)
    }

    override fun setImage(image: Int) {
        currentSelectedPosition = alikeImageList.indexOfFirst { it.image == image }
        adapter.setSelectedPosition(currentSelectedPosition)
        binding.lpMainIv.setImageResource(image)
//        viewAdapter.notifyItemChanged(currentSelectedPosition)
//        Toast.makeText(requireContext(), currentSelectedPosition, Toast.LENGTH_SHORT).show()
//        Glide.with(requireContext()).load(image).into(binding.imageView2)

    }
}