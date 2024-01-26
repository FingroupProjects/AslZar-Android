package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.RowItemProductInCartBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.util.EditProductInCart
import com.fin_group.aslzar.util.formatNumber

class ProductInCartAdapter(private var listProductInCart: List<ProductInCart>, private var listener: EditProductInCart)
    : RecyclerView.Adapter<ProductInCartAdapter.ViewHolder>() {

    private lateinit var binding: RowItemProductInCartBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        binding = RowItemProductInCartBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = listProductInCart[position]
        holder.bind(product)
    }
    fun updateList(newList: List<ProductInCart>) {
        listProductInCart = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: RowItemProductInCartBinding): RecyclerView.ViewHolder(binding.root){
        val image = binding.imageProductInCart
        val name = binding.nameProductInCart
        val count = binding.countProductInCart
        val btnPlus = binding.addProduct
        val btnMinus = binding.minusProduct

        val size = binding.sizeProductInCart
        val weight = binding.weightProductInCart
        val price = binding.priceProductInCart
        val filial = binding.filialProductInCart

        val saleTv = binding.productSale

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun bind(product: ProductInCart){
            if (product.image.isNotEmpty()){
                Glide.with(itemView.context)
                    .load(product.image[0])
                    .override(200, 200)
                    .centerCrop()
                    .into(image)
            } else {
                image.setImageResource(R.drawable.ic_no_image)
            }

            if (product.sale.toDouble() <= 0) {
                saleTv.visibility = View.GONE
            } else {
                saleTv.text = "-${(product.sale)}%"
                saleTv.visibility = View.VISIBLE
            }

            name.text = product.name
            weight.text = product.weight.toString()
            size.text = product.size.toString()
            count.text = product.countInCart.toString()
            price.text = "${formatNumber(product.filialPrice)} UZS"
            filial.text = product.filial

            btnPlus.setOnClickListener {
                listener.plusProductInCart(product)
            }
            btnMinus.setOnClickListener {
                listener.minusProductInCart(product)
            }
        }
    }

    override fun getItemCount(): Int {
        return listProductInCart.size
    }
}