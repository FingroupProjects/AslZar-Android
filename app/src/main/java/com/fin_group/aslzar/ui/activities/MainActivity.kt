package com.fin_group.aslzar.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fin_group.aslzar.R
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.ActivityMainBinding
import com.fin_group.aslzar.models.ProductInCart
import com.fin_group.aslzar.ui.fragments.cartMain.cart.CartFragment
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.OnProductAddedToCartListener
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavBar: BottomNavigationView

    private val badgeMap: MutableMap<Int, BadgeDrawable> = mutableMapOf()
    private lateinit var badgeManager: BadgeManager

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        badgeManager = BadgeManager(this)

        bottomNavBar = binding.bottomNavigationView
        val navController =findNavController(R.id.fragmentMain)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainFragment, R.id.mainCartFragment))

        setupActionBarWithNavController(navController,appBarConfiguration)
        bottomNavBar.setupWithNavController(navController)

        //addBadgeToBottomNavigationItem(R.id.mainCartFragment, 5)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentMain)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        Cart.loadCartFromPrefs(this)
    }

    @SuppressLint("ResourceAsColor")
    override fun onDestroy() {
        super.onDestroy()
        Cart.saveCartToPrefs(this)

        val badge = bottomNavBar.getOrCreateBadge(R.id.mainCartFragment)
        badge.backgroundColor = R.color.background_2
        badgeManager.saveBadgeCount(badge.number)
    }

//    override fun onProductAddedToCart(product: ProductInCart) {
//        val fragment = supportFragmentManager.findFragmentById(R.id.cartFragment)
//        if (fragment is CartFragment) {
//            fragment.onProductAddedToCart(product)
//        }
//    }
}