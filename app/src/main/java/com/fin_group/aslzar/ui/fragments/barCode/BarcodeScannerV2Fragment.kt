package com.fin_group.aslzar.ui.fragments.barCode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentBarCodeScannerBinding
import com.fin_group.aslzar.databinding.FragmentBarcodeScannerV2Binding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions


class BarcodeScannerV2Fragment : Fragment() {

    private var _binding: FragmentBarcodeScannerV2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarcodeScannerV2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = BarcodeScannerOptions.Builder()
            .enableAllPotentialBarcodes()
            .build()
    }

}