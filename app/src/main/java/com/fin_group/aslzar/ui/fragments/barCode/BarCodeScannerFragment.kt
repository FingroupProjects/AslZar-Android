package com.fin_group.aslzar.ui.fragments.barCode

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentBarCodeScannerBinding
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.fragments.main.MainFragmentDirections
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import kotlin.math.log


@Suppress("DEPRECATION")
class BarCodeScannerFragment : Fragment() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""

    private var _binding: FragmentBarCodeScannerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarCodeScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav()

        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            //setupControls()
        }
        val aniSlide: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)
    }

//    private fun setupControls() {
//        barcodeDetector =
//            BarcodeDetector.Builder(requireContext()).setBarcodeFormats(Barcode.ALL_FORMATS).build()
//
//        cameraSource = CameraSource.Builder(requireContext(), barcodeDetector)
//            .setRequestedPreviewSize(1920, 1080)
//            .setAutoFocusEnabled(true) //you should add this feature
//            .build()
//
//        binding.cameraSurfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
//            @SuppressLint("MissingPermission")
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                try {
//                    //Start preview after 1s delay
//                    cameraSource.start(holder)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//
//            @SuppressLint("MissingPermission")
//            override fun surfaceChanged(
//                holder: SurfaceHolder,
//                format: Int,
//                width: Int,
//                height: Int
//            ) {
//                try {
//                    cameraSource.start(holder)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                cameraSource.stop()
//            }
//        })
//
//
//        try {
//            barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
//                override fun release() {
//                    Log.d("TAG", "release: Scanner has been closed")
//                }
//                override fun receiveDetections(detections: Detector.Detections<Barcode>) {
//                    val barcodes = detections.detectedItems
//                    if (barcodes.size() == 1) {
//                        scannedValue = barcodes.valueAt(0).rawValue
//                        try {
//                            activity?.runOnUiThread {
//                                cameraSource.stop()
//                                val action =
//                                    BarCodeScannerFragmentDirections.actionBarCodeScannerFragmentToDataProductFragment(
//                                        scannedValue.toString()
//                                    )
//                                Navigation.findNavController(binding.root).navigate(action)
//                            }
//                        }catch (e: Exception){
//                            Log.d("TAG", "receiveDetections: ${e.message}")
//                        }
//                    } else {
//                        Log.d("TAG", "receiveDetections: Values not detected")
//                    }
//                }
//            })
//        }catch (e: Exception){
//            Log.d("TAG", "setupControls: ${e.message}")
//        }
//    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                setupControls()
//            } else {
//                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraSource.stop()
        _binding = null
    }
}