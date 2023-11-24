package com.fin_group.aslzar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.RowItemProductBinding
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.response.ProductSale
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.formatNumber

class SalesProductsAdapter(
    var productList: List<ProductSale>,
    val listener: ProductOnClickListener
) : RecyclerView.Adapter<SalesProductsAdapter.ViewHolder>() {

    private lateinit var binding: RowItemProductBinding
    private lateinit var context: Context

    class ViewHolder(binding: RowItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.productTitle
        val image = binding.productImage
        val code = binding.productKode
        val btnCheckingInStock = binding.ibHaveInStore
        val btnAddToCart = binding.ibAddToBasket
        private val saleTv = binding.productSale

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun bind(product: ProductSale) {
            title.text = product.full_name
            code.text = product.name

            if (product.img.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(product.img[0])
                    .override(200, 200)
                    .centerCrop()
                    .into(image)
            } else {
                image.setImageResource(R.drawable.ic_no_image)
            }

            if (product.sale.toDouble() <= 0) {
                saleTv.visibility = View.GONE
            } else {
                saleTv.text = "-${formatNumber(product.sale)}%"
                saleTv.visibility = View.VISIBLE
            }

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(newProducts: List<ProductSale>) {
        productList = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SalesProductsAdapter.ViewHolder {
        context = parent.context
        binding = RowItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: SalesProductsAdapter.ViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)

        binding.root.setOnClickListener {

            val product2 = ResultX(
                "",
                "",
                "",
                "",
                product.full_name,
                product.id,
                false,
                "",
                product.name,
                product.price.toInt(),
                "",
                product.sale.toInt(),
                "",
                emptyList(),
                product.img
            )

            listener.getData(product2)
        }
    }
}