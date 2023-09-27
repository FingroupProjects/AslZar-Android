package com.fin_group.aslzar.ui.fragments.cartMain.calculator

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.resetCalculator
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

//        fetchCoefficientPlanFromApi()
//
//        val coefficientPlanListJson = prefs.getString("coefficientPlan", null)
//        val coefficientPlanListType = object : TypeToken<PercentInstallment>() {}.type
//        val coefficientPlanList = Gson().fromJson<PercentInstallment>(coefficientPlanListJson, coefficientPlanListType)
////        percentInstallment = coefficientPlanList
//        Log.d("TAG", "onViewCreated: $coefficientPlanList")

//        binding.imageButton.setOnClickListener {
//            val popupManager = PopupManager(requireContext())
//            popupManager.showPopup(
//                "Клиент не может выплатить такую сумму за месяц."
//            )
//        }



        percentInstallment = PercentInstallment(
            90, 15, listOf(
                Percent(6.9, 3),
                Percent(8.9, 6),
                Percent(12.9, 9),
                Percent(17.9, 12),
            )
        )
        adapterPaymentPercent = TableInstallmentAdapter(percentInstallment, vlTotalPrice)
        cartObserver(binding)
        Cart.registerObserver(cartObserver)

        clientList = listOf(
            Client("1", "Розничный покупатель", 0, 0, "Silver", "Lead", 8400),
            Client("3", "Tohirjon", 37512, 3, "Silver", "Referral", 8400),
            Client("4", "Nuriddin", 19852, 3, "Silver", "Referral", 10000),
            Client("5", "Tursunboy", 0, 0, "", "Lead", 8000),
            Client("8", "Rustam", 7522, 3, "Silver", "Referral", 9500),
            Client("6", "Jamshed", 85654, 3, "Silver", "Referral", 8500),
            Client("7", "Khusrav", 9654, 5, "Gold", "Lead", 4500),
            Client("2", "Suhrob", 150000, 7, "Diamond", "Referral", 5000000),
        )

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