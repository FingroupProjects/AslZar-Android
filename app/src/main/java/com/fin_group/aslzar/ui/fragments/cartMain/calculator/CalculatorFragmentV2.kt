package com.fin_group.aslzar.ui.fragments.cartMain.calculator

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorV2Binding
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.cartObserver
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.SessionManager

@Suppress("DEPRECATION")
class CalculatorFragmentV2 : Fragment() {

    private var _binding: FragmentCalculatorV2Binding? = null
    private val binding get() = _binding!!

    lateinit var  api : ApiClient
    lateinit var sessionManager: SessionManager
    lateinit var prefs: SharedPreferences
    lateinit var cartObserver: CartObserver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorV2Binding.inflate(inflater, container, false)

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sessionManager = SessionManager(requireContext())

        cartObserver()
        Cart.registerObserver(cartObserver)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Cart.notifyObservers()
        Cart.loadCartFromPrefs(requireContext())
    }

    override fun onPause() {
        super.onPause()
        Cart.saveCartToPrefs(requireContext())
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        Cart.unregisterObserver(cartObserver)
        Cart.saveCartToPrefs(requireContext())
    }
}