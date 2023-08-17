package com.fin_group.aslzar.ui.fragments.cartMain.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.adapter.ProductInCartAdapter
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.models.Product
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.dialogs.DeleteAllProductFromCartFragmentDialog
import com.fin_group.aslzar.util.EditProductInCart
import com.fin_group.aslzar.util.OnImageClickListener


class CartFragment : Fragment(), EditProductInCart {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    lateinit var myAdapter: ProductInCartAdapter
    var allProducts: List<ProductInCart> = emptyList()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)


        deleteAllProductFromCart()


        allProducts = listOf(
            ProductInCart(1,"Серьги золотые с золотом","","2.7.5.1.066.1_1,6_0",1,"10","100"),
            ProductInCart(2,"Серьги золотые с печеньками","","2.7.3.1.096.1_7,8_0",15,"150","1500"),
            ProductInCart(3,"Серьги золотые с кошками","","2.7.3.1.096.1_7,8_0",18,"180","1800"),
            ProductInCart(4,"Серьги золотые с водичкой","","2.7.3.1.096.1_7,8_0",1,"10","100"),
            ProductInCart(5,"Серьги золотые с печеньками","","2.7.3.1.096.1_7,8_0",15,"150","1500"),
            ProductInCart(6,"Серьги золотые с компом","","2.7.3.1.096.1_7,8_0",21,"210","2100"),
            ProductInCart(7,"Серьги золотые с кольцом","","2.7.3.1.096.1_7,8_0",1,"10","100"),
            ProductInCart(8,"Серьги золотые с бриллиантами","","2.7.3.1.096.1_7,8_0",4,"40","400")
        )
        fetchRV(allProducts)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchRV(productInCartList: List<ProductInCart>){
        val recyclerView = binding.recyclerViewCart
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        myAdapter = ProductInCartAdapter(productInCartList, this)
        recyclerView.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
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

    override fun plusProductInCart(productInCart: ProductInCart) {
        Log.d("TAG", "bind: ${productInCart}")
        var a = productInCart.count
        a += 1
        Log.d("TAG count", "bind: ${a}")
    }

    override fun minusProductInCart(productInCart: ProductInCart) {
        TODO("Not yet implemented")
    }


}