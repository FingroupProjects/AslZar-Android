package com.fin_group.aslzar.ui.fragments.cartMain.functions

import com.fin_group.aslzar.R
import com.fin_group.aslzar.ui.fragments.cartMain.MainCartFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

fun MainCartFragment.removeBadges(bottomNavView: BottomNavigationView){
    bottomNavView.removeBadge(R.id.mainCartFragment)
}