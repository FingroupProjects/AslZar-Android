package com.fin_group.aslzar.ui.fragments.dataProduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentDataProductBinding
import com.fin_group.aslzar.util.hideBottomNav
import com.google.android.material.appbar.MaterialToolbar

class DataProductFragment : Fragment() {

    private var _binding: FragmentDataProductBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DataProductFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductBinding.inflate(inflater, container, false)
        hideBottomNav()
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView6.text = args.productId
    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }
}