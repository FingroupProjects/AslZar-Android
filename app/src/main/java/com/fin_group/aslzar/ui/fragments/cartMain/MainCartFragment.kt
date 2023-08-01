package com.fin_group.aslzar.ui.fragments.cartMain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentCartBinding
import com.fin_group.aslzar.databinding.FragmentMainCartBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainCartFragment : Fragment() {

    private var _binding: FragmentMainCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onStart() {
//        super.onStart()
//        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
//        toolbar.visibility = VISIBLE
//        toolbar.animate().translationY(toolbar.height.toFloat()).setDuration(100)
//            .withEndAction {
//                toolbar.visibility = View.GONE
//            }.start()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
//        toolbar.visibility = VISIBLE
//        toolbar.animate().translationY(0f).setDuration(100).start()
////        val bottomNavView =
////            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
////        bottomNavView.visibility = VISIBLE
////        bottomNavView.animate().translationY(0f).setDuration(300).start()
//    }
}