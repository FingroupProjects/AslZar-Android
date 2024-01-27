package com.fin_group.aslzar.ui.dialogs

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentScannerBinding
import com.fin_group.aslzar.models.Type
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.NoInternetDialogFragment
import com.fin_group.aslzar.util.SearchGoodsByBarcode
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.handleErrorResponse
import com.fin_group.aslzar.util.safeEnqueue

@Suppress("DEPRECATION")
class ScannerFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private val requestCodeCameraPermission = 1001
    private lateinit var codeScanner: CodeScanner

    lateinit var productList: List<Type>
    private var listener: SearchGoodsByBarcode? = null

    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var progressBar: ProgressBar
    private lateinit var view91: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        isCancelable = false
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        binding.close.setOnClickListener { dismiss() }
        binding.textView13.text = "Поиск товара"
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sessionManager = SessionManager(requireContext())
        apiClient = ApiClient()
        progressBar = binding.progressLinearDeterminate2
        view91 = binding.view91
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hasInternet = NoInternetDialogFragment.hasInternetConnection(requireContext())
        if (hasInternet) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                askForCameraPermission()
            } else {
                codeScanner.decodeCallback = DecodeCallback {
                    requireActivity().runOnUiThread {
                        getProductByID(it.text)
                    }
                }
                binding.scannerView.setOnClickListener {
                    codeScanner.startPreview()
                    binding.errorTextView.visibility = View.GONE
                    Log.d("TAG", "1")
                }
            }
        } else {
            NoInternetDialogFragment.showIfNoInternet(requireContext())
        }
    }

    fun setBarcodeListener(barcodeListener: SearchGoodsByBarcode) {
        listener = barcodeListener
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
        super.onPause()
        codeScanner.releaseResources()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getProductByID(productId: String) {
        val handler = Handler()
        view91.visibility = View.INVISIBLE

        try {
            val call = apiClient.getApiService()
                .getProductInScanner("Bearer ${sessionManager.fetchToken()}", productId)
            call.safeEnqueue(
                this,
                { response ->
                    view91.visibility = View.VISIBLE
                    if (response.isSuccessful) {
                        val barcode = response.body()
                        if (barcode != null) {
                            handler.postDelayed({
                                listener?.setGoods(barcode)
                                dismiss()
                                binding.errorTextView.visibility = View.GONE
                            }, 500)

                        }
                    } else {
                        view91.visibility = View.VISIBLE
                        try {
                            handleErrorResponse(
                                response.code(),
                                requireContext(),
                                sharedPreferences,
                                sessionManager
                            )
                        } catch (e: NullPointerException) {
                            Log.d("TAG", "searchGoodByImei: ${e.message}")
                        }
                    }
                },
                {
                    view91.visibility = View.VISIBLE
                }, progressBar
            )

        } catch (e: Exception) {
            Log.d("TAG", "historyGoodByImei: ${e.message}")
        }
    }
}