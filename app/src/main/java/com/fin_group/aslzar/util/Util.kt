package com.fin_group.aslzar.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.InputType
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat

interface FunCallback {
    fun onSuccess(success: Boolean)
    fun onError(errorMessage: String)
}

interface OnFriendAddedListener {
    fun onFriendAddedSuccessfully(success: Boolean)
}

fun Fragment.showAction() {
    (activity as AppCompatActivity).supportActionBar?.show()
}

fun Fragment.redirectToChangeServerFragment(fragmentDirections: NavDirections) {
    val navOptions = NavOptions.Builder().setPopUpTo(findNavController().currentDestination!!.id, true).build()
    findNavController().navigate(fragmentDirections, navOptions)
}

fun doubleFormat(double: Double): String {
    val decimalFormat = DecimalFormat("#.0")
    return decimalFormat.format(double)
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
