package com.fin_group.aslzar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.databinding.RowItemCategoryCheckBinding
import com.fin_group.aslzar.databinding.RowItemProductBinding
import com.fin_group.aslzar.models.Category

class CategoryAdapter(
    private val categories: List<Category>, private val onCategoryClickListener: (Category) -> Unit
): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(binding: RowItemCategoryCheckBinding): RecyclerView.ViewHolder(binding.root) {
        private val title = binding.spinnerTv

        fun bind(category: Category) {
            title.text = category.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = RowItemCategoryCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)

        holder.itemView.setOnClickListener { onCategoryClickListener(category) }
    }

    override fun getItemCount(): Int = categories.size
}
