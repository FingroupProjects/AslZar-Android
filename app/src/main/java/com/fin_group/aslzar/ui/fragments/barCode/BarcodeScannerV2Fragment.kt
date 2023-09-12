package com.fin_group.aslzar.ui.fragments.barCode

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentBarcodeScannerV2Binding
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BarcodeScannerV2Fragment : Fragment() {

    private var _binding: FragmentBarcodeScannerV2Binding? = null
    private val binding get() = _binding!!

    private val requestCodeCameraPermission = 1001
    private lateinit var codeScanner: CodeScanner

    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarcodeScannerV2Binding.inflate(inflater, container, false)
        apiClient = ApiClient()
        sessionManager = SessionManager(requireContext())
        apiClient.init(sessionManager)
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                askForCameraPermission()
            } else {
                //setupControls()
                codeScanner.decodeCallback = DecodeCallback {
                    requireActivity().runOnUiThread {
//                        Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                        getSimilarProduct(it.text)
                    }
                }
                binding.scannerView.setOnClickListener {
                    codeScanner.startPreview()
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", "onViewCreated: ${e.message}")
        }
    }

    private fun getSimilarProduct(productId: String) {
        try {
            val call = apiClient.getApiService()
                .getProductByID("Bearer ${sessionManager.fetchToken()}", productId)
            call.enqueue(object : Callback<Product?> {
                override fun onResponse(
                    call: Call<Product?>,
                    response: Response<Product?>
                ) {
                    if (response.isSuccessful) {
                        val productResponse = response.body()
                        if (productResponse != null) {
                            val action =
                                BarcodeScannerV2FragmentDirections.actionBarCodeScannerFragmentToDataProductFragment(
                                    productResponse.id,
                                    productResponse
                                )
                            Navigation.findNavController(binding.root).navigate(action)
                        } else if (response.body() == null) {
                            Toast.makeText(requireContext(),"Товар с таким идентификатором не найден",Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(binding.root).popBackStack()
                            showBottomNav()
                        }
                    } else {
                        Toast.makeText(requireContext(),"",Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).popBackStack()
                        showBottomNav()
                    }
                }
                override fun onFailure(call: Call<Product?>, t: Throwable) {
                    Toast.makeText(requireContext(),"Загрузка прошла не успешно, пожалуйста повторите попытку",Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("TAG", "getSimilarProduct: ${e.message}")
        }
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