package com.fin_group.aslzar.ui.fragments.forgotPassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentForgotPasswordBinding
import com.fin_group.aslzar.ui.fragments.forgotPassword.function.checkEmail
import com.fin_group.aslzar.util.SessionManager


class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    lateinit var apiService: ApiClient
    lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        apiService = ApiClient()
        sessionManager = SessionManager(requireContext())
        apiService.init(sessionManager)

        checkEmail(binding)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}