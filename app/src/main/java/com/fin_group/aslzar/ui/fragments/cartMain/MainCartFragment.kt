package com.fin_group.aslzar.ui.fragments.cartMain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.databinding.FragmentMainCartBinding
import androidx.viewpager2.widget.ViewPager2
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.TabLayoutAdapter
import com.fin_group.aslzar.ui.fragments.cartMain.functions.removeBadges
import com.fin_group.aslzar.util.hideToolBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

@Suppress("DEPRECATION")
class MainCartFragment : Fragment() {

    private var _binding: FragmentMainCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var tableLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter : TabLayoutAdapter

    private lateinit var bottomNavigationView: BottomNavigationView

    val database = AslZar.instance.database
    val cartProductDao = database.cartProductDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tableLayout = binding.tabLayout
        viewPager2 = binding.viewPager2
        hideToolBar()
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)

//        val viewPagerAdapter = ViewPagerAdapter(requireFragmentManager())
//        viewPagerAdapter.addFragment(CartFragment(), "Корзина")
//        viewPagerAdapter.addFragment(CalculatorFragment(), "Калькулятор")
//        viewPager2.adapter = viewPagerAdapter
//        tableLayout.setupWithViewPager(viewPager2)


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

        removeBadges(bottomNavigationView)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}