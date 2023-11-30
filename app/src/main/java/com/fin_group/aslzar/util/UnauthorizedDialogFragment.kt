package com.fin_group.aslzar.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.fin_group.aslzar.ui.activities.LoginActivity
import com.fin_group.aslzar.util.SessionManager.Companion.IS_LOGGED_IN_KEY
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UnauthorizedDialogFragment(
    private val sharedPreferences: SharedPreferences,
    private val fragment: Fragment
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setTitle("Ошибка 401")
            .setMessage("Ваш сеанс устарел, необходима авторизация. Пожалуйста, войдите в приложение заново.")
            .setPositiveButton("ОК") { dialog: DialogInterface, _: Int ->
                redirectToLogin()
                dialog.dismiss()
            }

        val dialog = builder.create()

        dialog.setOnShowListener { dialogInterface ->
            val okButton = (dialogInterface as Dialog).findViewById(android.R.id.button1) as? Button
            okButton?.setTextColor(Color.BLACK)
        }
        return dialog
    }

    private fun redirectToLogin() {
        val sessionManager = SessionManager(requireContext())
        sessionManager.clearSession()
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(IS_LOGGED_IN_KEY, false)
        editor.clear()
        editor.apply()
        val i = Intent(context, LoginActivity::class.java)
        startActivity(i)
        activity?.finish()
        dismiss()
    }

    companion object {
        fun showUnauthorizedError(
            context: Context,
            sharedPreferences: SharedPreferences,
            fragment: Fragment
        ) {
            val dialog = UnauthorizedDialogFragment(sharedPreferences, fragment)
            dialog.isCancelable = false
            dialog.show((context as AppCompatActivity).supportFragmentManager, "UnauthorizedErrorDialog")
        }
    }
}