package com.fin_group.aslzar.util

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.fin_group.aslzar.R

open class BaseDialogFragment : DialogFragment() {

    override fun getTheme(): Int = R.style.DialogFragmentTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Dialog(requireContext(), theme)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
    }

    private fun setupView() {
        setStyle(STYLE_NO_TITLE, R.style.DialogFragmentStyle)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun setDialogWidthPercent(percentage: Int) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        dialog?.window?.setLayout(percentWidth.toInt(), dialog?.window?.attributes?.height ?: WindowManager.LayoutParams.WRAP_CONTENT)
    }
}