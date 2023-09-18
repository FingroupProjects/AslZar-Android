package com.fin_group.aslzar.ui.fragments.cartMain.calculator

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorBinding
import com.fin_group.aslzar.response.Client
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.all
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.cartObserver
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.createTable
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetViews
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetchClientFromApi
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetchClientNameFromPrefs
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.SessionManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

@Suppress("DEPRECATION")
class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    lateinit var typePay: AutoCompleteTextView
    lateinit var checkBox: MaterialCheckBox
    lateinit var firstPayCalculator: TextInputLayout
    lateinit var checkboxForBonus: MaterialCheckBox
    lateinit var bonus: TextInputLayout
    lateinit var tableSale: TextView
    lateinit var tvTable: LinearLayoutCompat
    lateinit var monthTable: LinearLayoutCompat
    lateinit var percentTable: LinearLayoutCompat
    lateinit var firstPay: TextView
    lateinit var payWithBonus: TextView
    lateinit var tvFirstPayCalculator: TextInputEditText
    lateinit var summa: TextView
    lateinit var tvBonusForClient: TextView
    lateinit var tvSale: TextView
    lateinit var editBonus: TextInputEditText
    lateinit var spinnerClientType : AutoCompleteTextView
    lateinit var getAllClient: List<Client>
    lateinit var getPercentAndMonth: List<Percent>
    lateinit var getFirstPay: PercentInstallment
    lateinit var cartObserver: CartObserver
    lateinit var  api : ApiClient
    lateinit var sessionManager: SessionManager
    lateinit var prefs: SharedPreferences
    var totalCart: Number = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        api = ApiClient()
        api.init(sessionManager)
        cartObserver(binding)
        Cart.registerObserver(cartObserver)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetViews(binding)
        all(binding)
        createTable()
        //getForPercentAndMonth()
        fetchClientFromApi()
        fetchClientNameFromPrefs()
        super.onViewCreated(view, savedInstanceState)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Cart.unregisterObserver(cartObserver)
        Cart.saveCartToPrefs(requireContext())
    }
}