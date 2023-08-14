package com.fin_group.aslzar.ui.fragments.cartMain.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.ui.dialogs.DeleteAllProductFromCartFragmentDialog


class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        val recyclerView : RecyclerView = binding.recyclerViewCart
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val items = fetchData()
        val adaptor = ProductInCartAdapter(items)
        recyclerView.adapter = adaptor

        deleteAllProductFromCart()

        return binding.root
    }

    fun fetchData() : ArrayList<String>{
        val list = ArrayList<String>()
        for (i in 0 until 10){
            list.add("Серьги")
        }
        return list
    }

    private fun deleteAllProductFromCart(){
        binding.delete.setOnClickListener {
            val delete = DeleteAllProductFromCartFragmentDialog()
            delete.show(childFragmentManager, "delete_all_product_from_cart")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}