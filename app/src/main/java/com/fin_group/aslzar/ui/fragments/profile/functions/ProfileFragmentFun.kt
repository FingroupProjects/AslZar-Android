package com.fin_group.aslzar.ui.fragments.profile.functions

import android.graphics.Color
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fin_group.aslzar.ui.dialogs.ChangePasswordProfileFragmentDialog
import com.fin_group.aslzar.ui.fragments.profile.ProfileFragment
import com.github.anastr.speedviewlib.components.Style

fun ProfileFragment.speedometerView() {
    speedometer.speedTo(52.5f)
    speedometer.makeSections(3, Color.CYAN, Style.BUTT)
    speedometer.sections[0].color = Color.parseColor("#8e5234")
    speedometer.sections[1].color = Color.parseColor("#b66d32")
    speedometer.sections[2].color = Color.parseColor("#f2c1ad")
    speedometer.ticks = arrayListOf(0f, .25f, .5f, .75f, 1f)
}

fun ProfileFragment.goToChangePasswordDialog() {
    val changeDataPassword = ChangePasswordProfileFragmentDialog()
    val fragmentManager: FragmentManager? = activity?.supportFragmentManager
    fragmentManager?.let {
        val transaction: FragmentTransaction = it.beginTransaction()
        transaction.addToBackStack(null)
        changeDataPassword.show(transaction, "ChangePasswordProfileDialogFragment")
    }

}