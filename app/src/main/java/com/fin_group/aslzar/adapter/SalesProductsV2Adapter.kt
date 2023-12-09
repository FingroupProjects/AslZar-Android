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
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.formatNumber

@Suppress("DEPRECATION")
class SalesProductsV2Adapter(
    private var productList: List<ResultX>,
    val listener: ProductOnClickListener
) : RecyclerView.Adapter<SalesProductsV2Adapter.ViewHolder>() {

    private lateinit var binding: RowItemProductBinding
    private lateinit var context: Context

    inner class ViewHolder(binding: RowItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.productTitle
        val image = binding.productImage
        val code = binding.productKode

        //        private val btnCheckingInStock = binding.ibHaveInStore
        private val btnAddProduct = binding.ibAddToBasket
        private val saleTv = binding.productSale

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        fun bind(product: ResultX) {
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

            if (product.types.all { it.counts.isEmpty() }) {
                btnAddProduct.setImageResource(R.drawable.ic_clear_white)
                btnAddProduct.background = context.resources.getDrawable(R.drawable.item_product_bottom_btn_2)
                code.text = "Нет в наличии."
            } else {
                btnAddProduct.setImageResource(R.drawable.ic_add_2)
                btnAddProduct.background =
                    context.resources.getDrawable(R.drawable.ripple_effect_top_btn)
                code.text = "от: ${formatNumber(product.price)} UZS"
            }


            btnAddProduct.setOnClickListener {
                listener.addToCart(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        binding = RowItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(newProducts: List<ResultX>) {
        productList = newProducts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)

        binding.root.setOnClickListener {
            listener.getData(product)
        }
    }
}