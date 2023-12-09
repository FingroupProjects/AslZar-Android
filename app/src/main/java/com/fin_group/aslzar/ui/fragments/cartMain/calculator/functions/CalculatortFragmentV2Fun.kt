package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorV2Binding
import com.fin_group.aslzar.response.Client
import com.fin_group.aslzar.response.GetAllClientsResponse
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragmentV2
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.formatNumber
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun CalculatorFragmentV2.cartObserver(binding: FragmentCalculatorV2Binding) {
    cartObserver = object : CartObserver {
        @SuppressLint("SetTextI18n")
        override fun onCartChanged(
            totalPriceWithoutSale: Number,
            totalPriceWithSale: Number,
            totalCount: Int,
            totalPrice: Number
        ) {
            binding.totalPriceWithoutSale.text = "${formatNumber(totalPriceWithoutSale)} UZS"
            binding.sumSale.text = "${formatNumber(totalPriceWithSale)} UZS"
            binding.totalPrice.text = "${formatNumber(totalPrice)} UZS"

            vlTotalPrice = totalPrice
            vlTotalPriceWithSale = totalPriceWithSale
            vlTotalPriceWithoutSale = totalPriceWithoutSale
            vlTotalPriceSale = totalPriceWithSale

            val difference = (averageBill.toDouble() - totalPriceWithoutSale.toDouble())
            if (difference > 0) {
                val message = "Нужно еще ${formatNumber(difference)} для среднего чека"
                averageBillTv.text = message
            } else if (difference <= 0) {
                averageBillTv.text = "Средний чек достигнут"
            }

            textWatchers(binding, percentInstallment, totalPrice)
            printPercent(binding, percentInstallment, totalPrice)
        }
    }
}

fun CalculatorFragmentV2.updateClientsData(clients: List<Client>){
    arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, clients.map { it.client_name })
}

fun CalculatorFragmentV2.getAllClientsFromApi(){
    try {
        val call =
            api.getApiService().getAllClients("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<GetAllClientsResponse?> {
            override fun onResponse(
                call: Call<GetAllClientsResponse?>,
                response: Response<GetAllClientsResponse?>
            ) {
                if (response.isSuccessful) {
                    val clientList = response.body()
                    if (clientList != null) {
                        val clientListJson = Gson().toJson(clientList.result)
                        prefs.edit().putString("clientList", clientListJson).apply()
                        updateClientsData(clientList.result)
                        Log.d("TAG", "onResponse: ${clientList.result}")
                    }
                }
            }

            override fun onFailure(call: Call<GetAllClientsResponse?>, t: Throwable) {
                Log.d("TAG", "onViewCreated fetchClientsFromApi: ${t.message}")
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "fetchClientsFromApi: ${e.message}")
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.fetchClientsAndTypePay(binding: FragmentCalculatorV2Binding) {

    arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, clientList.map { it.client_name })

    binding.apply {
        clientType.setAdapter(arrayAdapterTypeClient)
        clientType.setOnItemClickListener { parent, view, position, id ->
            selectedClient = clientList[position]
//            if (selectedClient!!.bonus.toDouble() > 0) {
//                binding.cbBonus.visibility = View.VISIBLE
//            } else {
//                binding.cbBonus.visibility = View.GONE
//                binding.cbBonus.isChecked = false
//                binding.bonus.setText("")
//            }

            val cons = "розничный"
            val containsSubstring = selectedClient?.client_name.toString().contains(cons, true)
            Log.d("TAG", "FFFFFFFFFFFFF: $containsSubstring")
            if (containsSubstring){
                binding.limit.visibility = GONE
            }else{
                if (selectedClient?.limit == 0){
                    binding.limit.visibility = VISIBLE
                    binding.limit.text = "У данного клиента нет лимита!"
                }
                else{
                    binding.limit.visibility = VISIBLE
                    binding.limit.text = "Лимит: ${selectedClient?.limit}"
                }
            }

            bonusClient.text = "${formatNumber(selectedClient!!.bonus)} UZS"
            paymentClient(selectedClient!!, binding, percentInstallment)
            textWatchers(binding, percentInstallment, vlTotalPrice)
            printPercent(binding, percentInstallment, vlTotalPrice, selectedClient!!.limit)
        }
    }
}
fun CalculatorFragmentV2.paymentClient(
    client: Client,
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment
) {
    val contains = "лид"
    val containsSubstring = client.client_type.contains(contains, true)

    if (containsSubstring) {
        binding.cbBonus.visibility = GONE
        binding.cbBonus.isChecked = false
        binding.bonus.setText("")
    } else {
        if (client.bonus.toDouble() > 0) {
            installmentPayReferralClient(client, binding, percent, vlTotalPrice)
            textWatchers(binding, percent, Cart.getTotalPrice())
        } else {
            binding.cbBonus.visibility = GONE
            binding.cbBonus.isChecked = false
            binding.bonus.setText("")
        }
    }
    textWatchers(binding, percentInstallment, vlTotalPrice)
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.installmentPayReferralClient(
    client: Client,
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment,
    totalPrice: Number
) {

    binding.apply {
        cbBonus.visibility = VISIBLE
        cbBonus.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tilBonus.visibility = VISIBLE
            } else {
                tilBonus.visibility = GONE
                bonus.setText("")
            }
        }
    }
}

fun CalculatorFragmentV2.printPercent(
    binding: FragmentCalculatorV2Binding,
    installment: PercentInstallment,
    totalPrice: Number,
    clientLimit: Number = 0.0
) {
    binding.apply {
        rvPayments.layoutManager = LinearLayoutManager(requireContext())
        adapterPaymentPercent = TableInstallmentAdapter(installment, totalPrice, clientLimit)
        rvPayments.adapter = adapterPaymentPercent
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.createTable(binding: FragmentCalculatorV2Binding, totalPrice: Number) {
    monthLinearLayout.removeAllViews()
    percentLinearLayout.removeAllViews()

    binding.apply {
        val monthLinearLayout = monthTable
        val percentLinearLayout = percentTable

        for (percent in percentInstallment.result) {
            val indexPercent = percentInstallment.result.indexOf(percent)

            val textViewMonth = TextView(requireContext())
            textViewMonth.apply {
                text = "${percent.mounth} платежей (${percent.coefficient}%)"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                setPadding(15, 15, 15, 15)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_1))
                TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                if (indexPercent < percentInstallment.result.size - 1) {
                    setBackgroundResource(R.drawable.bg_text_view_in_table)
                }
            }
            monthLinearLayout.addView(textViewMonth)

            val textViewPercent = TextView(requireContext())
            val monthPayment =
                (((totalPrice.toDouble() * percent.coefficient.toDouble()) / 100) + totalPrice.toDouble()) / percent.mounth.toDouble()
            textViewPercent.apply {
                text = "${formatNumber(monthPayment)} UZS"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                setPadding(15, 15, 15, 15)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_1))
                TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                if (indexPercent < percentInstallment.result.size - 1) {
                    setBackgroundResource(R.drawable.bg_text_view_in_table)
                }
            }
            percentLinearLayout.addView(textViewPercent)
        }
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.textWatchers(
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment,
    totalPrice: Number
) {
    val bonusCheckBox = binding.cbBonus
    val bonusEditText = binding.bonus
    val firstPayEditText = binding.firstPay
    val payWithFirstPayTextView = binding.payWithFirstPay
    val payWithBonusTextView = binding.payWithBonus
    val totalPriceTextView = binding.totalPrice

//    val isFirstPayEnabled = totalPrice.toDouble() > 0
//    firstPayEditText.isEnabled = isFirstPayEnabled

    if (totalPrice == 0.0) {
        firstPayEditText.setText("0")
        bonusEditText.setText("0")
        payWithFirstPayTextView.text = "0.00 UZS"
        payWithBonusTextView.text = "0.00 UZS"
        totalPriceTextView.text = "0.00 UZS"

        bonusCheckBox.visibility = GONE
        bonusCheckBox.isChecked = false
//        firstPayEditText.error = null
    } else {
        val maxValueBonus: Double = (totalPrice.toDouble() * percent.payment_bonus.toDouble()) / 100
        val minValueFirstPay: Double = (totalPrice.toDouble() * percent.first_pay.toDouble()) / 100
        firstPayEditText.setText(minValueFirstPay.toString())

        var finalTotalPrice = totalPrice.toDouble()
        var countTextBonus = 0.0
        var countTextFirstPay = 0.0

        fun updateDisplayedValues() {
            val bonusAmount = (finalTotalPrice * percent.payment_bonus.toDouble()) / 100
            val firstPayAmount = (finalTotalPrice * percent.first_pay.toDouble()) / 100

            val enteredBonus = if ((selectedClient?.bonus?.toDouble() ?: 0.0) > 0.0) {
                bonusEditText.text.toString().toDoubleOrNull() ?: 0.0
            } else {
                0.0
            }
            val enteredFirstPay = firstPayEditText.text.toString().toDoubleOrNull() ?: 0.0

            val remainingTotal = finalTotalPrice - enteredFirstPay
            val bonusPercentOfRemaining = (remainingTotal * percent.payment_bonus.toDouble()) / 100

            if (enteredBonus > bonusPercentOfRemaining) {
                bonusEditText.setText(bonusPercentOfRemaining.toString())
                bonusEditText.setSelection(bonusEditText.length())
                countTextBonus = bonusPercentOfRemaining
            } else {
                countTextBonus = enteredBonus
            }

            val newTotalPrice = remainingTotal - countTextBonus

            totalPriceTextView.text = "${formatNumber(newTotalPrice)} UZS"
            payWithBonusTextView.text = "${formatNumber(countTextBonus)} UZS"
            payWithFirstPayTextView.text = "${formatNumber(enteredFirstPay)} UZS"

            adapterPaymentPercent.updateData(percent, newTotalPrice)
        }

        textWatcherForBonus = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString().trim()
                if (!newText.isNullOrEmpty()) {
                    val currentValue = newText.replace(',', '.').toDouble()
                    if (currentValue > (selectedClient?.bonus?.toDouble() ?: 0.0)) {
                        binding.bonus.setText(selectedClient?.bonus?.toString() ?: "")
                        binding.bonus.setSelection(binding.bonus.length())
                    } else if (currentValue > maxValueBonus) {
                        binding.bonus.setText(maxValueBonus.toString())
                        binding.bonus.setSelection(binding.bonus.length())
                    }
                }
                updateDisplayedValues()
            }
        }

        textWatcherForFirstPay = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString().trim()
                if (!newText.isNullOrEmpty()) {
                    val currentValue = newText.replace(',', '.').toDouble()
                    if (currentValue < minValueFirstPay) {
                        firstPayEditText.error = "Минимальное значение первоначального взноса ${percent.first_pay}% ($minValueFirstPay) от итоговой суммы"
                    } else {
                        firstPayEditText.error = null
                    }

                    if (currentValue > totalPrice.toDouble()) {
                        firstPayEditText.setText(totalPrice.toString())
                    }
                }
                updateDisplayedValues()
            }
        }

        bonusEditText.addTextChangedListener(textWatcherForBonus)
        firstPayEditText.addTextChangedListener(textWatcherForFirstPay)

        updateDisplayedValues()
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.resetCalculator(binding: FragmentCalculatorV2Binding) {
    binding.apply {
        try {
            cbBonus.isChecked = false
            cbBonus.visibility = GONE

            bonus.setText("")
            firstPay.setText("")

            val initialTotalPrice = Cart.getTotalPrice()
            totalPrice.text = "${formatNumber(initialTotalPrice)} UZS"
            payWithBonus.text = "0.00 UZS"
            payWithFirstPay.text = "0.00 UZS"

            printPercent(binding, percentInstallment, initialTotalPrice)
        } catch (e: Exception) {
            Log.d("TAG", "resetCalculator: ${e.message}")
        } catch (e: NumberFormatException) {
            Log.d("TAG", "resetCalculator: ${e.message}")
        }
    }
}

fun CalculatorFragmentV2.fetchCoefficientPlanFromPrefs(binding: FragmentCalculatorV2Binding) {
    val coefficientPlanJson = prefs.getString("coefficientPlan", null)
    try {
        if (coefficientPlanJson != null) {
            val coefficientPlanType = object : TypeToken<PercentInstallment>() {}.type
            val coefficientPlan =
                Gson().fromJson<PercentInstallment>(coefficientPlanJson, coefficientPlanType)
            percentInstallment = coefficientPlan
            printPercent(binding, percentInstallment, Cart.getTotalPrice())
        } else {
            fetchCoefficientPlanFromApi(binding)
        }
    } catch (e: Exception) {
        Log.d("TAG", "coefficientPlan: ${e.message}")
    }
}

fun CalculatorFragmentV2.retrieveCoefficientPlan(): PercentInstallment {
    val coefficientPlanJson = prefs.getString("coefficientPlan", null)
    return if (coefficientPlanJson != null) {
        val coefficientPlanType = object : TypeToken<PercentInstallment>() {}.type
        Gson().fromJson(coefficientPlanJson, coefficientPlanType)
    } else {
        PercentInstallment(
            5, 5, 7, emptyList()
        )
    }
}

fun CalculatorFragmentV2.fetchCoefficientPlanFromApi(binding: FragmentCalculatorV2Binding) {
    try {
        val call = api.getApiService().getPercentAndMonth("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<PercentInstallment?> {
            override fun onResponse(
                call: Call<PercentInstallment?>,
                response: Response<PercentInstallment?>
            ) {
                if (response.isSuccessful) {
                    val coefficientPlanList = response.body()
                    if (coefficientPlanList != null) {
                        val coefficientPlanJson = Gson().toJson(coefficientPlanList)
                        prefs.edit().putString("coefficientPlan", coefficientPlanJson).apply()
                        percentInstallment = coefficientPlanList
                        printPercent(binding, percentInstallment, Cart.getTotalPrice())
                    }
                }
            }

            override fun onFailure(call: Call<PercentInstallment?>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Произишла ошибка, повторите попытку",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TAG", "onFailure fetchCategoriesFromApi: ${t.message}")
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "fetchCategoriesFromApi: ${e.message}")
    }
}

fun CalculatorFragmentV2.retrieveClientList(): List<Client> {
    val clientListJson = prefs.getString("clientList", null)
    return if (clientListJson != null) {
        val clientListType = object : TypeToken<List<Client>>() {}.type
        Gson().fromJson(clientListJson, clientListType)
    } else {
        emptyList()
    }
}

fun CalculatorFragmentV2.fetchClientsFromPrefs() {
    val clientListJson = prefs.getString("clientList", null)
    try {
        if (clientListJson != null) {
            val clientListType = object : TypeToken<List<Client>>() {}.type
            val clientListPrefs = Gson().fromJson<List<Client>>(clientListJson, clientListType)
            clientList = clientListPrefs
            arrayAdapterTypeClient = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                clientList.map { it.client_name })
        } else {
            fetchClientsFromApi()
        }
    } catch (e: Exception) {
        Log.d("TAG", "coefficientPlan: ${e.message}")
    }
}

fun CalculatorFragmentV2.fetchClientsFromApi() {
    try {
        val call = api.getApiService().getAllClients("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<GetAllClientsResponse?> {
            override fun onResponse(
                call: Call<GetAllClientsResponse?>,
                response: Response<GetAllClientsResponse?>
            ) {
                if (response.isSuccessful) {
                    val clientListResponse = response.body()
                    if (clientListResponse?.result!!.isNotEmpty()) {
                        val clientListJson = Gson().toJson(clientListResponse.result)
                        prefs.edit().putString("clientList", clientListJson).apply()
                        clientList = clientListResponse.result
                    }
                }
            }

            override fun onFailure(call: Call<GetAllClientsResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ")
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "fetchClientsFromApi: ${e.message}")
    }
}

fun CalculatorFragmentV2.animation(){
    val constraintLayout = binding.constraintLayout
    val animationController = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.rv_layout_anim)
    constraintLayout.layoutAnimation = animationController
    constraintLayout.scheduleLayoutAnimation()
}