package com.fin_group.aslzar.ui.fragments.cartMain.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorBinding
import com.fin_group.aslzar.models.AllTypePay
import com.fin_group.aslzar.models.Installment
import com.fin_group.aslzar.response.Client
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.calculator
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.cartObserver
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.createTable
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetViews
import com.fin_group.aslzar.util.CartObserver
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    lateinit var typeClient: AutoCompleteTextView
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
    lateinit var sale: TextView
    lateinit var payWithBonus: TextView
    lateinit var tvFirstPayCalculator: TextInputEditText
    lateinit var summa: TextView
    lateinit var tvBonusForClient: TextView
    lateinit var tvSale: TextView
    lateinit var editBonus: TextInputEditText

    lateinit var getInstallment: List<Installment>

    var allClientType: List<Client> = emptyList()
    var allTypePay: List<AllTypePay> = emptyList()


    var totalCart: Number = 0
    lateinit var cartObserver: CartObserver


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        cartObserver(binding)
        Cart.registerObserver(cartObserver)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetViews(binding)
        calculator()
        createTable()
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