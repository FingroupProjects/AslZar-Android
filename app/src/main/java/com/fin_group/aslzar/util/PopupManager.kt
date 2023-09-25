package com.fin_group.aslzar.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.fin_group.aslzar.R

class CustomPopupView(private val context: Context) {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val popupView: View = inflater.inflate(R.layout.popup_layout, null)
    private val popupWindow = PopupWindow(
        popupView,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        true
    )

    private val detailMessageTextView: TextView = popupView.findViewById(R.id.popupMessage)
    private val closeButton: Button = popupView.findViewById(R.id.closeButton)

    init {
        closeButton.setOnClickListener { dismiss() }
    }

    fun showMessage(message: String, detailMessage: String) {
        detailMessageTextView.text = detailMessage
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    fun dismiss() {
        popupWindow.dismiss()
    }
}
