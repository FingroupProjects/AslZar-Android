package com.fin_group.aslzar.ui.fragments.login.forgotPassword

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentForgotPasswordBinding
import com.fin_group.aslzar.response.ForgotPasswordResponse
import com.fin_group.aslzar.response.Product
import com.fin_group.aslzar.ui.fragments.login.forgotPassword.function.checkEmail
import com.fin_group.aslzar.util.SessionManager
import com.google.android.material.textfield.TextInputEditText


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