package com.fin_group.aslzar.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.widget.Toolbar
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavBar: BottomNavigationView

    private val badgeMap: MutableMap<Int, BadgeDrawable> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar

        setSupportActionBar(toolbar)

        bottomNavBar = binding.bottomNavigationView
        val navController =findNavController(R.id.fragmentMain)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainFragment, R.id.mainCartFragment))

        setupActionBarWithNavController(navController,appBarConfiguration)
        bottomNavBar.setupWithNavController(navController)

        //addBadgeToBottomNavigationItem(R.id.mainCartFragment, 5)
    }

    fun addBadgeToBottomNavigationItem(menuItemId: Int, badgeCount: Int) {
        val badgeDrawable = bottomNavBar.getOrCreateBadge(menuItemId)
        badgeDrawable.isVisible = true
        badgeDrawable.number = badgeCount
        badgeMap[menuItemId] = badgeDrawable
    }

    fun updateBadgeInBottomNavigation(menuItemId: Int, badgeCount: Int) {
        val badgeDrawable = bottomNavBar.getOrCreateBadge(menuItemId)
        badgeDrawable.isVisible = true
        badgeDrawable.number = badgeCount
        badgeMap[menuItemId] = badgeDrawable
    }
    fun removeBadgeFromBottomNavigationItem(menuItemId: Int) {
         badgeMap.remove(menuItemId)?.isVisible = false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentMain)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}