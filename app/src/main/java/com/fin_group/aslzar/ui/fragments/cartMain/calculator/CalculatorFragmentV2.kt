package com.fin_group.aslzar.ui.fragments.cartMain.calculator

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorV2Binding
import com.fin_group.aslzar.models.TypePay
import com.fin_group.aslzar.response.Client
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.cartObserver
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetchClientsAndTypePay
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.SessionManager
import java.lang.reflect.Type

@Suppress("DEPRECATION")
class CalculatorFragmentV2 : Fragment() {

    private var _binding: FragmentCalculatorV2Binding? = null
    private val binding get() = _binding!!

    lateinit var api: ApiClient
    lateinit var sessionManager: SessionManager
    lateinit var prefs: SharedPreferences
    lateinit var cartObserver: CartObserver

    lateinit var selectedClient: Client
    lateinit var typePaySelected: TypePay

    lateinit var clientList: List<Client>
    lateinit var allTypePay: List<TypePay>
    lateinit var percentInstallment: PercentInstallment

    var vlTotalPrice: Number = 0
    var vlTotalPriceWithSale: Number = 0
    var vlTotalPriceWithoutSale: Number = 0
    var vlTotalPriceSale: Number = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorV2Binding.inflate(inflater, container, false)

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        api = ApiClient()
        api.init(sessionManager)

        cartObserver(binding)
        fetchClientsAndTypePay(binding)


        Cart.registerObserver(cartObserver)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchClient = binding.clientType.text.toString()
        val selectClient = clientList.find { it.client_name == searchClient }
        Log.d("TAG", "onViewCreated: $selectClient")
//        checkTypePay(binding, selectClient!!)
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