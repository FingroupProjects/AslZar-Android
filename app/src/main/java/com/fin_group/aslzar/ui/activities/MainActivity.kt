package com.fin_group.aslzar.ui.activities

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
import com.fin_group.aslzar.util.OnProductAddedToCartListener
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), OnProductAddedToCartListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavBar: BottomNavigationView

    private val badgeMap: MutableMap<Int, BadgeDrawable> = mutableMapOf()

    private val sharedViewModel: SharedViewModel by viewModels()

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

    override fun onStart() {
        super.onStart()
        Cart.loadCartFromPrefs(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Cart.saveCartToPrefs(this)
    }

    override fun onProductAddedToCart(product: ProductInCart) {
//        val cartFragment = CartFragment()
//        cartFragment.onProductAddedToCart(product)
        val fragment = supportFragmentManager.findFragmentById(R.id.cartFragment)
        if (fragment is CartFragment) {
            // Взаимодействие с фрагментом
            fragment.onProductAddedToCart(product)
            Log.d("TAG", "onProductAddedToCart: a11 $product")
            Log.d("TAG", "onProductAddedToCart: a22 $fragment")
        }


//        val cartFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.cartFragment)
        //val cartFragment = supportFragmentManager.findFragmentById(R.id.cartFragment) as? CartFragment
        //cartFragment?.onProductAddedToCart(product)
        Log.d("TAG", "onProductAddedToCart: a1 $product")
        Log.d("TAG", "onProductAddedToCart: a2 $fragment")
//        Log.d("TAG", "onProductAddedToCart: a3 ${cartFragment?.onProductAddedToCart(product)}")
        //Log.d("TAG", "onProductAddedToCart: a3 ${cartFragment?.onProductAddedToCart(product)}")
    }
}