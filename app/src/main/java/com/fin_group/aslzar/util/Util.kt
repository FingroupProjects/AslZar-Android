package com.fin_group.aslzar.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Base64
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.fin_group.aslzar.R
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.SimilarProduct
import com.fin_group.aslzar.response.Type
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

interface FunCallback {
    fun onSuccess(success: Boolean)
    fun onError(errorMessage: String)
}

interface AddingProduct {
    fun addProduct(product: ResultX, type: Type, count: Count)
}

interface FilialListener {
    fun addFilial(product: ResultX, type: Type, filial: Count)
}

interface OnFriendAddedListener {
    fun onFriendAddedSuccessfully(success: Boolean)
}

interface CalculatorResetListener {
    fun resetCalculator()
}

interface OnProductClickListener {
    fun setProduct(product: ResultX)
}

interface OnImageClickListener {
    fun setImage(image: String)
}

interface OnProductCharacteristicClickListener{
    fun clickCharacteristic(characteristic: Type)
    fun showProductDialog(product: Type)
}

interface EditProductInCart {
    fun plusProductInCart(productInCart: ProductInCart)
    fun minusProductInCart(productInCart: ProductInCart)

    fun openDialogDataProduct(productInCart: ProductInCart)

    fun onCartCleared()
}

interface OnProductAddedToCartListener {
    fun onProductAddedToCart(product: ProductInCart)
}


interface OnAlikeProductClickListener {
    fun callBottomDialog(product: SimilarProduct)
}

interface FilterDialogListener {
    fun onFilterApplied(updatedFilterModel: FilterModel)
}


fun Fragment.showAction() {
    (activity as AppCompatActivity).supportActionBar?.show()
}

fun Fragment.redirectToChangeServerFragment(fragmentDirections: NavDirections) {
    val navOptions =
        NavOptions.Builder().setPopUpTo(findNavController().currentDestination!!.id, true).build()
    findNavController().navigate(fragmentDirections, navOptions)
}

fun doubleFormat(double: Number): String {
    val decimalFormat = DecimalFormat("#.00")
    return decimalFormat.format(double)
}

interface CartObserver {
    fun onCartChanged(
        totalPriceWithoutSale: Number,
        totalPriceWithSale: Number,
        totalCount: Int,
        totalPrice: Number
    )
}


fun nextEt(editText: TextInputEditText) {
    editText.imeOptions = EditorInfo.IME_ACTION_DONE
    editText.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
}

fun decodeBase64AndCreateImage(encodedImage: String): Bitmap? {
    val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun Fragment.hideAction() {
    (activity as AppCompatActivity).supportActionBar?.hide()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideToolBar() {
    val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
    val hideAnim: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
    toolbar.startAnimation(hideAnim)
    toolbar.visibility = View.GONE
}

fun Fragment.showToolBar() {
    val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
    val showAnim: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in)
    toolbar.startAnimation(showAnim)
    toolbar.visibility = View.VISIBLE
}

fun Fragment.hideBottomNav() {
    val bottomNavBar =
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    val hideAnim: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
    bottomNavBar.startAnimation(hideAnim)
    bottomNavBar.visibility = View.GONE
}

fun Fragment.showBottomNav() {
    val bottomNavBar =
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    val showAnim: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
    bottomNavBar.startAnimation(showAnim)
    bottomNavBar.visibility = View.VISIBLE
}

fun formatNumber(number: Number): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    if (numberFormat is DecimalFormat) {
        val symbols = numberFormat.decimalFormatSymbols
        symbols.groupingSeparator = ' ' // Установите пробел в качестве разделителя разрядов
        numberFormat.decimalFormatSymbols = symbols
        numberFormat.applyPattern("#,##0.00")
    }
    return numberFormat.format(number)
}


fun Fragment.setupKeyboardScrolling() {
    view?.setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    // Scroll up
                    view?.scrollBy(0, -10) // Adjust the scroll amount as needed
                    return@setOnKeyListener true
                }

                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    // Scroll down
                    view?.scrollBy(0, 10) // Adjust the scroll amount as needed
                    return@setOnKeyListener true
                }
            }
        }
        return@setOnKeyListener false
    }
    view?.isFocusableInTouchMode = true
    view?.requestFocus()
}

fun Fragment.backPressed(backPressedTime: Long, TIME_INTERVAL: Long, view: View, action: Int) {
    var backPressedTimeVar = backPressedTime
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTimeVar + TIME_INTERVAL > System.currentTimeMillis()) {
                    findNavController().navigate(action)
                } else {
                    val snack = Snackbar.make(
                        view,
                        "Нажмите еще раз для выхода в главное меню",
                        Snackbar.LENGTH_SHORT
                    )
                    snack.setAction("Отмена", View.OnClickListener {
                        // executed when DISMISS is clicked
                        backPressedTimeVar = 0
                    })
                    snack.show()
                }
                backPressedTimeVar = System.currentTimeMillis()
            }
        })
}

fun DialogFragment.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent

    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    dialog?.window?.setGravity(Gravity.CENTER)
}

fun BottomSheetDialogFragment.setDialogHeightPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentHeight = rect.height() * percent
    dialog?.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        percentHeight.toInt()
    )
}

fun Fragment.backPressed(action: Int) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(action)
            }
        })
}

fun returnNumber(str: String): Number {
    return if (!str.isNullOrEmpty()) {
        if (str.contains('.')) {
            str.toDouble()
        } else {
            str.toInt()
        }
    } else {
        0
    }
}


fun setMaxValueET(inputET: EditText, maxValue: Number) {
    val hello = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val newText = s.toString().trim()
            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDoubleOrNull()
                if (currentValue != null){
                    if (currentValue > maxValue.toDouble()) {
                        inputET.setText(maxValue.toString())
                        inputET.requestFocus()
                        inputET.setSelection(maxValue.toString().length)
                    }
                }
            }
        }
    }
    inputET.addTextChangedListener(hello)
}

fun setMinMaxValueET(inputET: EditText, minValue: Number, maxValue: Number, delayMillis: Long = 1500L) {
    val handler = Handler()

    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                val newText = s.toString().trim()
                if (!newText.isNullOrEmpty()) {
                    val currentValue = newText.replace(',', '.').toDoubleOrNull()
                    if (currentValue != null) {
                        if (currentValue < minValue.toDouble()) {
                            inputET.setText(minValue.toString())
                            inputET.setSelection(minValue.toString().length)
                        }
                        else if (currentValue > maxValue.toDouble()) {
                            inputET.setText(maxValue.toString())
                            inputET.setSelection(maxValue.toString().length)
                        }
                    }
                }
            }, delayMillis)
        }
    }

    inputET.addTextChangedListener(textWatcher)
}

fun Fragment.setupEditTextBehavior(vararg editTexts: EditText) {
    for (editText in editTexts) {
        editText.setOnClickListener {
            editText.selectAll()
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}

fun viewChecked(view: ConstraintLayout): Boolean {
    return view.visibility != View.VISIBLE
}
