package com.fin_group.aslzar.ui.fragments.dataProduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.databinding.FragmentMainCartBinding
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav

class DataProductFragment : Fragment() {

    private var _binding: FragmentDataProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductBinding.inflate(inflater, container, false)
        hideBottomNav()
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        showBottomNav()
    }

}