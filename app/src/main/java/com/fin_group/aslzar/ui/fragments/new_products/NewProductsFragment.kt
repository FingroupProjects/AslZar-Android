package com.fin_group.aslzar.ui.fragments.new_products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentNewProductsBinding


@Suppress("DEPRECATION")
class NewProductsFragment : Fragment() {

    private var _binding: FragmentNewProductsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewProductsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_item -> {
                Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show()
//                searchViewFun()
            }

            R.id.filter_item -> {
//                filterFun()
                Toast.makeText(requireContext(), "Category", Toast.LENGTH_SHORT).show()
            }

            R.id.barcode_item -> {

            }

            R.id.profile_item -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}