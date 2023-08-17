package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.RowItemProductBinding
import com.fin_group.aslzar.databinding.RowItemProductInCartBinding
import com.fin_group.aslzar.models.Product
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.fragments.main.MainFragmentDirections
import com.fin_group.aslzar.util.EditProductInCart

class ProductInCartAdapter(private val productInCart: List<ProductInCart>, private var listener: EditProductInCart)
    : RecyclerView.Adapter<ProductInCartAdapter.ViewHolder>() {

    private lateinit var binding: RowItemProductInCartBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        binding = RowItemProductInCartBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productInCart.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productInCart[position]
        holder.bind(product)

        binding.root.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToDataProductFragment(product.code)
            holder.itemView.findNavController().navigate(action)
        }

    }
    inner class ViewHolder(binding: RowItemProductInCartBinding): RecyclerView.ViewHolder(binding.root){
        val image = binding.imageProductInCart
        val name = binding.nameProductInCart
        val code = binding.codeProductInCart
        val count = binding.countProductInCart
        val btnPlus = binding.addProduct
        val btnMinus = binding.minusProduct

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(product: ProductInCart){
            name.text = product.name
            code.text = product.code
            count.text = product.count.toString()

            btnPlus.setOnClickListener {

                listener.plusProductInCart(product)
            }
            btnMinus.setOnClickListener {
                listener.minusProductInCart(product)
            }
        }
    }
}