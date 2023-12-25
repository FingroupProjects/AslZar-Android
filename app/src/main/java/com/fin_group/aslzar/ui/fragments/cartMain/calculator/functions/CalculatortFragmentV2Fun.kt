package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
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
import com.fin_group.aslzar.util.doubleFormat2
import com.fin_group.aslzar.util.formatNumber
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs


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

            textWatchers(binding, percentInstallment, totalPrice.toDouble())
            printPercent(binding, percentInstallment, totalPrice)
        }
    }
}


@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.setupDiscountButtons(
    binding: FragmentCalculatorV2Binding,
    installment: PercentInstallment
) {
    binding.handSaleCount.text = manualDiscount.toString()
    maxManualDiscount = installment.sale_limit ?: 0.0
    binding.tvHandSale.text = "Ручная скидка (Макс: $maxManualDiscount)"

    binding.apply {
        handSalePlus.setOnClickListener {
            increaseManualDiscount(binding)
            updateDisplayedValues(binding)
        }

        handSaleMinus.setOnClickListener {
            decreaseManualDiscount(binding)
            updateDisplayedValues(binding)
        }
    }
}

fun CalculatorFragmentV2.increaseManualDiscount(binding: FragmentCalculatorV2Binding) {
    if (Cart.getTotalPrice()
            .toDouble() > 0 && manualDiscount + 0.5 <= maxManualDiscount.toDouble()
    ) {
        manualDiscount += 0.5
    }
    checkManualDiscount(binding)
}

fun CalculatorFragmentV2.decreaseManualDiscount(binding: FragmentCalculatorV2Binding) {
    if (Cart.getTotalPrice().toDouble() > 0 && manualDiscount - 0.5 >= 0.0) {
        manualDiscount -= 0.5
    }
    checkManualDiscount(binding)
}

fun CalculatorFragmentV2.resetManualDiscount(binding: FragmentCalculatorV2Binding) {
    manualDiscount = 0.0

    checkManualDiscount(binding)
    updateDisplayedValues(binding)
}


fun CalculatorFragmentV2.updateFirstPayTextWatcher(binding: FragmentCalculatorV2Binding) {
    val textWatcher = createTextWatcherForFirstPay(
        binding,
        Cart.getTotalPrice().toDouble()
    ) { updateDisplayedValues(binding) }
    binding.firstPay.removeTextChangedListener(textWatcher)
    binding.firstPay.addTextChangedListener(textWatcher)
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.checkManualDiscount(binding: FragmentCalculatorV2Binding) {
    if (Cart.getTotalPrice() == 0) {
        manualDiscount = 0.0
        binding.payWithHandSale.text = "0.00 UZS"
        binding.handSaleCount.text = "0.0"
    }
    if (manualDiscount == 0.0) {
        binding.tvBonusClient.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_color_2
            )
        )
        binding.payWithHandSale.text = "0.00 UZS"

        val client = selectedClient
        if (client != null) {
            if (client.bonus.toDouble() > 0) binding.cbBonus.visibility = VISIBLE
        }
    }
    if (manualDiscount > 0) {
        binding.cbBonus.visibility = GONE
        binding.cbBonus.isChecked = false
        binding.bonus.setText("")

        val client = selectedClient
        if (client != null) {
            if (client.bonus.toDouble() > 0) {
                binding.bonusClient.text = "${formatNumber(client.bonus)} UZS"
                binding.tvBonusClient.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_7
                    )
                )
                binding.tvBonusClient.setOnClickListener {
                    val dialog = MaterialAlertDialogBuilder(requireContext())
                    dialog.setTitle("Предуприждение")
                    dialog.setMessage("Задействована ручная скидка, поэтому не удастся использовать бонус клиента")
                    val positiveButton = dialog.setPositiveButton("OK", null).show().getButton(
                        DialogInterface.BUTTON_POSITIVE
                    )
                    positiveButton.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.background_2
                        )
                    )
                }
            } else {
                binding.tvBonusClient.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_color_2
                    )
                )
            }
        }
    }
}

fun CalculatorFragmentV2.updateDisplayedValues(binding: FragmentCalculatorV2Binding) {
    val manual = if (Cart.getTotalPrice() == 0) {
        0.0
    } else {
        manualDiscount
    }

    binding.handSaleCount.text = manual.toString()
    val clientLimit = if (selectedClient != null) {
        selectedClient!!.limit
    } else {
        0
    }
    val firstPay = if (binding.firstPay.text.toString().isNotEmpty()) {
        binding.firstPay.text.toString().trim().replace(",", ".").toDouble()
    } else {
        0.0
    }

    printPercent(binding, percentInstallment, Cart.getTotalPrice(), clientLimit, firstPay, manual)
}

fun CalculatorFragmentV2.updateClientsData(clients: List<Client>) {
    arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, clients.map { it.client_name })
    Log.d("TAG", "updateClientsData: $clients")
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.fetchClientsAndTypePay(binding: FragmentCalculatorV2Binding) {

    arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, clientList.map { it.client_name })

    binding.apply {
        clientType.setAdapter(arrayAdapterTypeClient)
        clientType.setOnItemClickListener { parent, view, position, id ->
            selectedClient = clientList[position]

            val cons = "розничный"
            val containsSubstring = selectedClient?.client_name.toString().contains(cons, true)
            if (containsSubstring) {
                binding.limit.visibility = GONE
            } else {
                if (selectedClient?.limit == 0) {
                    binding.limit.visibility = VISIBLE
                    binding.limit.text = "У данного клиента нет лимита!"
                } else {
                    binding.limit.visibility = VISIBLE
                    binding.limit.text = "Лимит: ${selectedClient?.limit}"
                }
            }

            bonusClient.text = "${formatNumber(selectedClient!!.bonus)} UZS"
            checkManualDiscount(binding)
            paymentClient(selectedClient!!, binding, percentInstallment)
            textWatchers(binding, percentInstallment, vlTotalPrice.toDouble())
            val firstPayment = binding.firstPay.text.toString().trim().toDouble()

            printPercent(
                binding,
                percentInstallment,
                vlTotalPrice,
                selectedClient!!.limit,
                firstPayment,
                manualDiscount
            )
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
            textWatchers(binding, percent, Cart.getTotalPrice().toDouble())
        } else {
            binding.cbBonus.visibility = GONE
            binding.cbBonus.isChecked = false
            binding.bonus.setText("")
        }
    }
    textWatchers(binding, percentInstallment, vlTotalPrice.toDouble())
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.installmentPayReferralClient(
    client: Client,
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment,
    totalPrice: Number
) {

    binding.apply {

        if (manualDiscount <= 0) {
            cbBonus.visibility = VISIBLE
        } else {
            cbBonus.visibility = GONE
        }

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
    clientLimit: Number = 0.0,
    initialPayment: Number = 0.0,
    handSale: Number = 0.0
) {
    binding.apply {
        var priceTotal = totalPrice.toDouble() - initialPayment.toDouble()
        priceTotal = (priceTotal - ((priceTotal * handSale.toDouble()) / 100))

        rvPayments.layoutManager = LinearLayoutManager(requireContext())
        adapterPaymentPercent = TableInstallmentAdapter(installment, priceTotal, clientLimit)
        rvPayments.adapter = adapterPaymentPercent
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.textWatchers(
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment,
    totalPrice: Double
) {
    val bonusCheckBox = binding.cbBonus
    val bonusEditText = binding.bonus
    val firstPayEditText = binding.firstPay
    val payWithFirstPayTextView = binding.payWithFirstPay
    val payWithBonusTextView = binding.payWithBonus
    val payWithHandSale = binding.payWithHandSale
    val totalPriceTextView = binding.totalPrice
    val handSale = binding.handSaleCount

    if (totalPrice == 0.0) {
        firstPayEditText.setText("0")
        bonusEditText.setText("0")
        payWithFirstPayTextView.text = "0.00 UZS"
        payWithBonusTextView.text = "0.00 UZS"
        totalPriceTextView.text = "0.00 UZS"
        payWithHandSale.text = "0.00 UZS"
        handSale.text = "0.0"
        manualDiscount = 0.0
    } else {
        processNonZeroTotalPrice(binding, percent, totalPrice)
        updateDisplayedValues(binding)
    }
}

@SuppressLint("SetTextI18n")
private fun CalculatorFragmentV2.processNonZeroTotalPrice(
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment,
    totalPrice: Double
) {
    val maxValueBonus: Double = (totalPrice * percent.payment_bonus.toDouble()) / 100
    val minValueFirstPay: Double = (totalPrice * percent.first_pay.toDouble()) / 100

    var finalTotalPrice = totalPrice

    fun updateDisplayedValues() {
        val enteredBonus = binding.bonus.text.toString().toDoubleOrNull() ?: 0.0
        val enteredFirstPay =
            binding.firstPay.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0

        val enteredHandSale = if (manualDiscount > 0) {
            (finalTotalPrice * manualDiscount) / 100
        } else {
            0.0
        }

        val firstPayment = finalTotalPrice - enteredFirstPay
        val bonusPercentOfRemaining = (firstPayment * percent.payment_bonus.toDouble()) / 100

        val saleBonus = abs(enteredHandSale - firstPayment)
        val calculatedBonusPayment = if (enteredBonus > bonusPercentOfRemaining) {
            bonusPercentOfRemaining
        } else {
            enteredBonus
        }

        val calculatedNewTotalPrice = when {
            manualDiscount > 0 -> abs(saleBonus)
            enteredBonus > 0 -> abs(calculatedBonusPayment - firstPayment)
            else -> abs(firstPayment - enteredHandSale)
        }

        binding.totalPrice.text = "${formatNumber(calculatedNewTotalPrice)} UZS"
        binding.payWithBonus.text = "${formatNumber(calculatedBonusPayment)} UZS"
        binding.payWithFirstPay.text = "${formatNumber(enteredFirstPay)} UZS"
        binding.payWithHandSale.text = "${formatNumber(enteredHandSale)} UZS"

        adapterPaymentPercent.updateData(percent, calculatedNewTotalPrice)
    }

    val bonus = binding.bonus.text.toString().toDoubleOrNull() ?: 0.0

    val newTotalPrice = when {
        manualDiscount > 0 -> totalPrice - (finalTotalPrice * manualDiscount / 100)
        bonus > 0 -> totalPrice - bonus
        else -> totalPrice
    }

    val textWatcherForBonus =
        createTextWatcherForBonus(binding, percent, maxValueBonus) { updateDisplayedValues() }
    val textWatcherForFirstPay =
        createTextWatcherForFirstPay(binding, newTotalPrice) { updateDisplayedValues() }

    binding.bonus.addTextChangedListener(textWatcherForBonus)
    binding.firstPay.addTextChangedListener(textWatcherForFirstPay)

    updateDisplayedValues()
}


fun CalculatorFragmentV2.createTextWatcherForBonus(
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment,
    maxValueBonus: Double,
    updateDisplayedValues: () -> Unit
): TextWatcher {
    return object : TextWatcher {
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
}

fun CalculatorFragmentV2.createTextWatcherForFirstPay(
    binding: FragmentCalculatorV2Binding,
    totalPrice: Double,
    updateDisplayedValues: () -> Unit
): TextWatcher {
    return object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val newText = s.toString().trim()
            val currentMax = Cart.getTotalPrice().toDouble() * (1 - manualDiscount / 100)

            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDouble()
                if (currentValue > currentMax) {
                    binding.firstPay.setText(doubleFormat2(currentMax))
                }
            } else {
                binding.firstPay.setText("0")
            }
            updateDisplayedValues()
        }
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
            manualDiscount = 0.0

            val initialTotalPrice = Cart.getTotalPrice()
            totalPrice.text = "${formatNumber(initialTotalPrice)} UZS"
            payWithBonus.text = "0.00 UZS"
            payWithFirstPay.text = "0.00 UZS"
            payWithHandSale.text = "0.00 UZS"

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
            setupDiscountButtons(binding, percentInstallment)
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
                        setupDiscountButtons(binding, percentInstallment)
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
        getAllClientsFromApi()
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
            getAllClientsFromApi()
        }
    } catch (e: Exception) {
        Log.d("TAG", "coefficientPlan: ${e.message}")
    }
}

fun CalculatorFragmentV2.getAllClientsFromApi() {
    progressBar.visibility = VISIBLE
    try {
        val call = api.getApiService().getAllClients("Bearer ${sessionManager.fetchToken()}")
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
                    }
                }
                progressBar.visibility = GONE
            }

            override fun onFailure(call: Call<GetAllClientsResponse?>, t: Throwable) {
                Log.d("TAG", "onViewCreated fetchClientsFromApi: ${t.message}")
                progressBar.visibility = GONE
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "fetchClientsFromApi: ${e.message}")
        progressBar.visibility = GONE
    }
}

fun CalculatorFragmentV2.animation(binding: FragmentCalculatorV2Binding) {
    val constraintLayout = binding.constraintLayout
    val animationController =
        AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.rv_layout_anim)
    constraintLayout.layoutAnimation = animationController
    constraintLayout.scheduleLayoutAnimation()
}