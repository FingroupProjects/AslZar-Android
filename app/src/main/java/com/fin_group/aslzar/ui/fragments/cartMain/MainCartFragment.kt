package com.fin_group.aslzar.ui.fragments.cartMain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.databinding.FragmentMainCartBinding
import androidx.viewpager2.widget.ViewPager2
import com.fin_group.aslzar.adapter.TabLayoutAdapter
import com.fin_group.aslzar.util.hideToolBar
import com.google.android.material.tabs.TabLayout

class MainCartFragment : Fragment() {

    private var _binding: FragmentMainCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var tableLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter : TabLayoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCartBinding.inflate(inflater, container, false)

        tableLayout = binding.tabLayout
        viewPager2 = binding.viewPager2
        hideToolBar()

        tableLayout.addTab(tableLayout.newTab().setText("Корзина"))
        tableLayout.addTab(tableLayout.newTab().setText("Калькулятор"))

        adapter = TabLayoutAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter


        tableLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null){
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tableLayout.selectTab(tableLayout.getTabAt(position))
            }
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}