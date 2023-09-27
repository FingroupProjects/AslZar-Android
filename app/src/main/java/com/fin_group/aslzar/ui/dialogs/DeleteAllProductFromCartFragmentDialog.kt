package com.fin_group.aslzar.ui.dialogs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fin_group.aslzar.R
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentDeleteAllProductFromCartDialogBinding
import com.fin_group.aslzar.databinding.FragmentSignOutProfileDialogBinding
import com.fin_group.aslzar.ui.activities.LoginActivity
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragmentV2
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.resetCalculator
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.CalculatorResetListener
import com.fin_group.aslzar.util.EditProductInCart
import com.fin_group.aslzar.util.setWidthPercent
import com.google.android.material.bottomnavigation.BottomNavigationView

class DeleteAllProductFromCartFragmentDialog : BaseDialogFragment() {

    private var _binding : FragmentDeleteAllProductFromCartDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomNavigationView: BottomNavigationView
    private var resetListener: CalculatorResetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteAllProductFromCartDialogBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(90)
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)

        binding.apply {
            actionClose.setOnClickListener { dismiss() }
            actionYesBtn.setOnClickListener {
                Toast.makeText(requireContext(), "Корзина успешно очищена!", Toast.LENGTH_SHORT).show()
                bottomNavigationView.removeBadge(R.id.mainCartFragment)
                Cart.clearAllProducts(requireContext())

                val cartFragment = parentFragment as? EditProductInCart
                cartFragment?.onCartCleared()

                val calculatorFragment = parentFragment as? CalculatorResetListener
                calculatorFragment?.resetCalculator()

                dismiss()
            }
            actionNoBtn.setOnClickListener { dismiss() }
        }
    }

    fun setCalculatorResetListener(listener: CalculatorResetListener) {
        resetListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}