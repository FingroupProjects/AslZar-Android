package com.fin_group.aslzar.ui.fragments.dataProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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


@Suppress("DEPRECATION")
class DataProductFragment : Fragment(), OnImageClickListener {

    private var _binding: FragmentDataProductBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DataProductFragmentArgs>()
    lateinit var recyclerView: RecyclerView
    private var currentSelectedPosition = RecyclerView.NO_POSITION
    var imageList: List<ImageDataModel> = emptyList()
    lateinit var productSomeImagesAdapter: ProductSomeImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductBinding.inflate(inflater, container, false)
        hideBottomNav()
        setHasOptionsMenu(true)
        recyclerView = binding.otherImgRv

        productSomeImagesAdapter = ProductSomeImagesAdapter(imageList, this)
        productSomeImagesAdapter.setSelectedPosition(0)

        displayList(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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