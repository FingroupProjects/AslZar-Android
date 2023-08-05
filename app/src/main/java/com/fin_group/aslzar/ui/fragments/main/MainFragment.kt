package com.fin_group.aslzar.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.databinding.FragmentMainBinding
import com.fin_group.aslzar.models.Product
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.showBottomNav
import com.google.android.material.appbar.MaterialToolbar


@Suppress("DEPRECATION")
class MainFragment : Fragment(), ProductOnClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar

    private var allProducts: List<Product> = emptyList()
    private lateinit var myAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allProducts = listOf(
            Product(1, "Серьги золотые с бриллиантами", "", "2.7.5.1.066.1_1,6_0", 0),
            Product(2, "Серьги золотые с бриллиантами", "", "2.7.3.1.096.1_7,8_0", 15),
            Product(3, "Серьги золотые с бриллиантами", "", "2.7.2.1.096.1_5,9_0", 10),
            Product(4, "Серьги золотые с бриллиантами", "", "2.7.2.1.096.1_7,2_0", 18),
            Product(5, "Серьги золотые с бриллиантами", "", "2.7.5.1.066.1_1,6_0", 0),
            Product(6, "Серьги золотые с бриллиантами", "", "2.7.3.1.096.1_7,8_0", 15),
            Product(7, "Серьги золотые с бриллиантами", "", "2.7.2.1.096.1_5,9_0", 10),
            Product(8, "Серьги золотые с бриллиантами", "", "2.7.2.1.096.1_7,2_0", 18)
        )
        fetchRV(allProducts)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchRV(productList: List<Product>){
        val recyclerView = binding.mainRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        myAdapter = ProductsAdapter(productList, this)
        recyclerView.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.search_item -> {
                val viewSearch = binding.viewSearch
                if (searchBarChecked()) {
                    viewSearch.visibility = VISIBLE
                } else {
                    viewSearch.visibility = GONE
                }
            }
            R.id.filter_item -> {
                Toast.makeText(requireContext(), "Filter", Toast.LENGTH_SHORT).show()
            }
            R.id.barcode_item -> {
                val action = MainFragmentDirections.actionMainFragmentToBarCodeScannerFragment()
                findNavController().navigate(action)
            }
            R.id.profile_item -> {
                findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchBarChecked(): Boolean{
        val viewSearch = binding.viewSearch
        return viewSearch.visibility != VISIBLE
    }


    override fun onStart() {
        super.onStart()
        showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun addToCart(product: Product) {
        Toast.makeText(requireContext(), "Добавление в корзину ${product.code}", Toast.LENGTH_SHORT).show()
    }

    override fun inStock(product: Product) {
        Toast.makeText(requireContext(), "В наличии ${product.code}", Toast.LENGTH_SHORT).show()
    }
}