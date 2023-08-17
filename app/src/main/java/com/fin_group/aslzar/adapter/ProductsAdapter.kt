package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.RowItemProductBinding
import com.fin_group.aslzar.models.Product
import com.fin_group.aslzar.models.ProductV2
import com.fin_group.aslzar.ui.fragments.main.MainFragmentDirections
import com.fin_group.aslzar.util.ProductOnClickListener

class ProductsAdapter(
    var productList: List<ProductV2>,
    val listener: ProductOnClickListener,
): RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private lateinit var binding: RowItemProductBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        binding = RowItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)

        binding.root.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToDataProductFragment(product.barcode)
            holder.itemView.findNavController().navigate(action)
        }
    }

    inner class ViewHolder(binding: RowItemProductBinding): RecyclerView.ViewHolder(binding.root){
        val title = binding.productTitle
        val image = binding.productImage
        val code = binding.productKode
        val btnCheckingInStock = binding.ibHaveInStore
        val btnAddToCart = binding.ibAddToBasket

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(product: ProductV2){
            title.text = product.name
            code.text = product.barcode

//            if (product.image != ""){
//                Glide.with(itemView.context)
//                    .load(product.image)
//                    .override(100, 100)
//                    .centerCrop()
//                    .into(image)
//            } else {
//                image.setImageResource(R.drawable.ic_no_image)
//            }
            if (product.count <= 0){
                btnCheckingInStock.setImageResource(R.drawable.ic_clear_white)
//                btnCheckingInStock.background = context.resources.getDrawable(R.drawable.item_product_bottom_btn_2)
            } else {
                btnCheckingInStock.setImageResource(R.drawable.ic_check)
                btnCheckingInStock.background = context.resources.getDrawable(R.drawable.item_product_bottom_btn)
            }
            btnAddToCart.setOnClickListener {
                listener.addToCart(product)
            }
            btnCheckingInStock.setOnClickListener {
                listener.inStock(product)
            }
        }
    }

    fun updateProducts(newProducts: List<ProductV2>) {
        productList = newProducts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}