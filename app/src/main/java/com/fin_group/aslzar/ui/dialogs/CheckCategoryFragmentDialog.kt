package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.CategoryAdapter
import com.fin_group.aslzar.databinding.FragmentDialogCheckCategoryBinding
import com.fin_group.aslzar.models.Category
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.setWidthPercent


@Suppress("DEPRECATION")
class CheckCategoryFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentDialogCheckCategoryBinding? = null
    private val binding get() = _binding!!

    private var categories: List<Category> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogCheckCategoryBinding.inflate(inflater, container, false)
        setWidthPercent(90)

        categories = listOf(
            Category("00001", "Кольца"),
            Category("00002", "Серьги"),
            Category("00003", "Ожерелья"),
            Category("00004", "Браслеты"),
            Category("00005", "Подвески"),
            Category("00006", "Часы"),
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = binding.rvCategories
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CategoryAdapter(categories) { selectedCategory ->
            val listener = targetFragment as? CategoryClickListener
            listener?.onCategorySelected(selectedCategory)
            dismiss()
        }
    }



}