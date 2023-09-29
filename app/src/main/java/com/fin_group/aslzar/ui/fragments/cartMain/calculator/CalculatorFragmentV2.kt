package com.fin_group.aslzar.ui.fragments.cartMain.calculator

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorV2Binding
import com.fin_group.aslzar.models.TypePay
import com.fin_group.aslzar.response.Client
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.cartObserver
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetchClientsAndTypePay
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetchClientsFromApi
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetchClientsFromPrefs
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetchCoefficientPlanFromPrefs
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.resetCalculator
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.retrieveClientList
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.retrieveCoefficientPlan
import com.fin_group.aslzar.util.CalculatorResetListener
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.CustomPopupView
import com.fin_group.aslzar.util.SessionManager

@Suppress("DEPRECATION")
class CalculatorFragmentV2 : Fragment(), CalculatorResetListener {

    private var _binding: FragmentCalculatorV2Binding? = null
    private val binding get() = _binding!!

    lateinit var api: ApiClient
    lateinit var sessionManager: SessionManager
    lateinit var prefs: SharedPreferences
    lateinit var cartObserver: CartObserver

    var selectedClient: Client? = null
    lateinit var typePaySelected: TypePay

    lateinit var clientList: List<Client>
    lateinit var allTypePay: List<TypePay>
    lateinit var percentInstallment: PercentInstallment

    lateinit var arrayAdapterTypeClient: ArrayAdapter<String>

    lateinit var adapterPaymentPercent: TableInstallmentAdapter

    var vlTotalPrice: Number = 0
    var vlTotalPriceWithSale: Number = 0
    var vlTotalPriceWithoutSale: Number = 0
    var vlTotalPriceSale: Number = 0

    lateinit var monthLinearLayout: LinearLayoutCompat
    lateinit var percentLinearLayout: LinearLayoutCompat

    lateinit var textWatcherForFirstPay: TextWatcher
    lateinit var textWatcherForBonus: TextWatcher

    lateinit var averageBillTv: TextView
    lateinit var averageBill: Number

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorV2Binding.inflate(inflater, container, false)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        api = ApiClient()
        api.init(sessionManager)
        averageBillTv = binding.averageBill
        averageBill = sessionManager.fetchCheck()
        monthLinearLayout = binding.monthTable
        percentLinearLayout = binding.percentTable

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchCoefficientPlanFromPrefs(binding)
        percentInstallment = retrieveCoefficientPlan()
        adapterPaymentPercent = TableInstallmentAdapter(percentInstallment, vlTotalPrice, 0.0)
        cartObserver(binding)
        Cart.registerObserver(cartObserver)

        clientList = retrieveClientList()
        Log.d("TAG", "onViewCreated: $clientList")
        fetchClientsFromPrefs()
        fetchClientsAndTypePay(binding)
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

    override fun resetCalculator() {
        resetCalculator(binding)
    }
}