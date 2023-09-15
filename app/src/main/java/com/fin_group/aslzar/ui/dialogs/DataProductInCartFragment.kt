package com.fin_group.aslzar.ui.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentDataProductInCartBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.response.SimilarProduct
import com.fin_group.aslzar.ui.fragments.barCode.BarcodeScannerV2FragmentDirections
import com.fin_group.aslzar.ui.fragments.cartMain.cart.CartFragment
import com.fin_group.aslzar.ui.fragments.cartMain.cart.functions.updateBadge
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.formatNumber
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.snackbar.Snackbar

@Suppress("DEPRECATION")
class DataProductInCartFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentDataProductInCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var product: ProductInCart

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var cartFragment: CartFragment

    companion object {
        fun newInstance(product: ProductInCart): DataProductInCartFragment {
            val dialog = DataProductInCartFragment()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, product)
            dialog.arguments = args
            return dialog
        }

        private const val ARG_PRODUCT = "productInCart"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductInCartBinding.inflate(inflater, container, false)
        cartFragment = CartFragment()

        arguments?.let {
            product = it.getParcelable(ARG_PRODUCT)!!
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData(product)
        Log.d("TAG", "onViewCreated: $product")
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setData(product: ProductInCart){
        binding.apply {
            dcFullName.text = product.name
            dcCode.text = "Код: ${product.code}"

            if (product.image.isNotEmpty()){
                Glide.with(requireContext()).load(product.image[0]).into(dcMainImageView)
            } else {
                dcMainImageView.setImageResource(R.drawable.ic_no_image)
            }

            dcClose.setOnClickListener { dismiss() }
//            dcDelete.setOnClickListener {
//                Toast.makeText(requireContext(), "${product.name} удален", Toast.LENGTH_SHORT).show()
//                sharedViewModel.removeProductFromCart(product, requireContext())
//                val allProducts = Cart.getAllProducts()
//
//                cartFragment.myAdapter.notifyDataSetChanged()
//                cartFragment.myAdapter.updateList(allProducts)
//                Cart.notifyObservers()
//                dismiss()
//            }

        }
    }
}