package com.fin_group.aslzar.ui.fragments.barCode

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentBarcodeScannerV2Binding
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import com.google.android.material.appbar.MaterialToolbar


class BarcodeScannerV2Fragment : Fragment() {

    private var _binding: FragmentBarcodeScannerV2Binding? = null
    private val binding get() = _binding!!

    private val requestCodeCameraPermission = 1001
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarcodeScannerV2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            //setupControls()
            val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
            val activity = requireActivity()
            codeScanner = CodeScanner(activity, scannerView)
            codeScanner.decodeCallback = DecodeCallback {
                activity.runOnUiThread {
                    Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                    val action =
                        BarcodeScannerV2FragmentDirections.actionBarCodeScannerFragmentToDataProductFragment(
                            it.text.toString()
                        )
                    Navigation.findNavController(binding.root).navigate(action)
                }
            }
//            scannerView.setOnClickListener {
//                codeScanner.startPreview()
//            }
        }

//        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
//        toolbar.setNavigationOnClickListener {
//            findNavController().popBackStack()
//            showBottomNav()
//        }
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}