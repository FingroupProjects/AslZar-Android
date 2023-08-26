package com.fin_group.aslzar.ui.fragments.cartMain.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorBinding
import com.fin_group.aslzar.models.AllClientType
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.calculator
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.fetViews
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    lateinit var typeClient: AutoCompleteTextView
    lateinit var typePay: AutoCompleteTextView
    lateinit var checkBox: MaterialCheckBox
    lateinit var firstPayCalculator: TextInputLayout
    lateinit var checkboxForBonus:MaterialCheckBox
    lateinit var bonus: TextInputLayout
    lateinit var tableSale: TextView
    lateinit var tvTableSale: LinearLayout
    lateinit var firstPay: TextView
    lateinit var sale: TextView
    lateinit var payWithBonus: TextView
    lateinit var tvFirstPayCalculator: TextInputEditText
    lateinit var summa: TextView
    lateinit var tvBonusForClient: TextView
    lateinit var editBonus: TextInputEditText

    // for table
    lateinit var fourSale: TextView
    lateinit var sixSale: TextView
    lateinit var eightSale: TextView
    lateinit var tenSale: TextView
    lateinit var twelveSale: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetViews(binding)
        calculator()
//        tableCount()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Cart.loadCartFromPrefs(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Cart.saveCartToPrefs(requireContext())
    }
}