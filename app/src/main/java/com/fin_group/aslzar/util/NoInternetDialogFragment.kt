package com.fin_group.aslzar.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.fin_group.aslzar.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs

class NoInternetDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setTitle("Отсутствует подключение к интернету")
            .setMessage("Пожалуйста, подключитесь к интернету и повторите попытку.")
            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }

        val dialog = builder.create()

        dialog.setOnShowListener { dialogInterface ->
            val okButton = (dialogInterface as Dialog).findViewById(android.R.id.button1) as? Button
            okButton?.setTextColor(Color.BLACK)
        }
        return dialog
    }

    companion object {
//        fun showIfNoInternet(context: Context) {
//            val connectivityManager =
//                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val network = connectivityManager.activeNetwork
//            val capabilities = connectivityManager.getNetworkCapabilities(network)
//            if (capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
//                val dialog = NoInternetDialogFragment()
//                dialog.isCancelable = false
//                dialog.show((context as AppCompatActivity).supportFragmentManager, "NoInternetDialog")
//            }
//        }
        fun hasInternetConnection(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        fun showIfNoInternet(context: Context): Boolean {
            val hasInternet = hasInternetConnection(context)
            if (!hasInternet) {
                val dialog = NoInternetDialogFragment()
                dialog.isCancelable = false
                dialog.show((context as AppCompatActivity).supportFragmentManager, "NoInternetDialog")
            }
            return hasInternet
        }
    }
}