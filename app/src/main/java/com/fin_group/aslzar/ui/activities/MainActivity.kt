package com.fin_group.aslzar.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.ui.fragments.main.functions.savingAndFetchingCategory
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
    lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!

        Log.d("TAG", "onCreate: ${preferences.getString("selectedCategory", "hello")}")
        preferences.edit()?.putString("selectedCategory", "all")?.apply()

        val isFirstRun = preferences.getBoolean("isFirstRun", true)
        Log.d("TAG", "onCreate firstRun: $isFirstRun")

        if (isFirstRun) {
            Log.d("TAG", "Before setting isFirstRun to false: $isFirstRun")
            val mainFragment = supportFragmentManager.findFragmentById(R.id.fragmentMain) as? MainFragment
            mainFragment?.hideCategoryView()

            preferences.edit()?.putBoolean("isFirstRun", false)?.apply()

            val isFirstRunAfterChange = preferences.getBoolean("isFirstRun", true)
            Log.d("TAG", "After setting isFirstRun to false: $isFirstRunAfterChange")
        }

        Log.d("TAG", "onCreate firstRun: $isFirstRun")

        setContentView(binding.root)
        toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        badgeManager = BadgeManager(this)

        bottomNavBar = binding.bottomNavigationView
        val navController =findNavController(R.id.fragmentMain)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment, R.id.mainCartFragment))

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
        preferences.edit()?.putBoolean("first_run", true)?.apply()


        Log.d("TAG", "onCreate: ${preferences.getString("selectedCategory", "hello")}")
        preferences.edit()?.putString("selectedCategory", "all")?.apply()
    }

    @SuppressLint("ResourceAsColor", "CommitPrefEdits")
    override fun onDestroy() {
        super.onDestroy()
        Cart.saveCartToPrefs(this)

        val badge = bottomNavBar.getOrCreateBadge(R.id.mainCartFragment)
        badgeManager.saveBadgeCount(badge.number)
        preferences.edit()?.putString("selectedCategory", "all")?.apply()
        preferences.edit()?.putBoolean("first_run", false)?.apply()

//        val preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val editor = preferences.edit()
//        editor.remove("selectedCategory")
//        editor.apply()
    }

//    override fun onProductAddedToCart(product: ProductInCart) {
//        val fragment = supportFragmentManager.findFragmentById(R.id.cartFragment)
//        if (fragment is CartFragment) {
//            fragment.onProductAddedToCart(product)
//        }
//    }
}