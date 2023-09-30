package com.fin_group.aslzar.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.ActivityMainBinding
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.response.GetAllClientsResponse
import com.fin_group.aslzar.response.GetAllProductsResponse
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.ui.fragments.dataProduct.DataProductFragment
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavBar: BottomNavigationView

    private val badgeMap: MutableMap<Int, BadgeDrawable> = mutableMapOf()
    private lateinit var badgeManager: BadgeManager

    private val sharedViewModel: SharedViewModel by viewModels()
    lateinit var prefs: SharedPreferences

    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager

    private var doubleBackToExitPressedOnce = false

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.init(sessionManager)

        prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!

        prefs.edit()?.putString("selectedCategory", "all")?.apply()
        val isFirstRun = prefs.getBoolean("isFirstRun", true)

//        if (isFirstRun) {
//            val mainFragment =
//                supportFragmentManager.findFragmentById(R.id.fragmentMain) as? MainFragment
//            mainFragment?.hideCategoryView()
//            prefs.edit()?.putBoolean("isFirstRun", false)?.apply()
//        }

        checkAndFetchData()

        if (savedInstanceState == null) {
            clearSavedPreferences()
        }

        setContentView(binding.root)
        toolbar = binding.toolbar

        setSupportActionBar(toolbar)
        badgeManager = BadgeManager(this, "badge_cart_prefs")

        bottomNavBar = binding.bottomNavigationView
        val navController = findNavController(R.id.fragmentMain)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.newProductsFragment,
                R.id.salesAndPromotionsFragment,
                R.id.mainFragment,
                R.id.mainCartFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavBar.setupWithNavController(navController)

        //addBadgeToBottomNavigationItem(R.id.mainCartFragment, 5)
    }

    private fun clearSavedPreferences() {
        with(prefs.edit()) {
            remove("productList")
            remove("newProductList")
            remove("productListSales")
            remove("clientList")
            remove("categoryList")
            remove("requestList")
            remove("coefficientPlan")
            apply()
        }
    }

    private fun checkAndFetchData() {
        val productListJson = prefs.getString("productList", null)
        val clientListJson = prefs.getString("clientList", null)
        val categoriesJson = prefs.getString("categoryList", null)
        val coefficientPlanJson = prefs.getString("coefficientPlan", null)

        val fetchDataList = mutableListOf<Pair<String, () -> Unit>>()

        if (productListJson == null) {
            fetchDataList.add("productList" to ::fetchProductsFromApi)
        }
        if (categoriesJson == null) {
            fetchDataList.add("categoryList" to ::fetchCategoriesFromApi)
        }
        if (clientListJson == null) {
            fetchDataList.add("clientList" to ::fetchClientsFromApi)
        }
        if (coefficientPlanJson == null) {
            fetchDataList.add("coefficientPlan" to ::fetchCoefficientPlanFromApi)
        }

        fetchDataList.forEach { (key, fetchMethod) ->
            fetchMethod.invoke()
        }
    }

    private fun fetchProductsFromApi() {
        try {
            val call =
                apiClient.getApiService().getAllProducts("Bearer ${sessionManager.fetchToken()}")
            call.enqueue(object : Callback<GetAllProductsResponse?> {
                override fun onResponse(
                    call: Call<GetAllProductsResponse?>,
                    response: Response<GetAllProductsResponse?>
                ) {
                    if (response.isSuccessful) {
                        val productList = response.body()
                        if (productList != null) {
                            val productListJson = Gson().toJson(productList.result)
                            prefs.edit().putString("productList", productListJson).apply()
                        }
                    }
                }

                override fun onFailure(call: Call<GetAllProductsResponse?>, t: Throwable) {
                    Log.d("TAG", "onViewCreated fetchProductsFromApi: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("TAG", "fetchProductsFromApi: ${e.message}")
        }
    }

    private fun fetchClientsFromApi() {
        try {
            val call =
                apiClient.getApiService().getAllClients("Bearer ${sessionManager.fetchToken()}")
            call.enqueue(object : Callback<GetAllClientsResponse?> {
                override fun onResponse(
                    call: Call<GetAllClientsResponse?>,
                    response: Response<GetAllClientsResponse?>
                ) {
                    if (response.isSuccessful) {
                        val clientList = response.body()
                        if (clientList != null) {
                            val clientListJson = Gson().toJson(clientList.result)
                            prefs.edit().putString("clientList", clientListJson).apply()
                        }
                    }
                }

                override fun onFailure(call: Call<GetAllClientsResponse?>, t: Throwable) {
                    Log.d("TAG", "onViewCreated fetchClientsFromApi: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("TAG", "fetchClientsFromApi: ${e.message}")
        }
    }

    private fun fetchCategoriesFromApi() {
        try {
            val call =
                apiClient.getApiService().getAllCategories("Bearer ${sessionManager.fetchToken()}")
            call.enqueue(object : Callback<GetAllCategoriesResponse?> {
                override fun onResponse(
                    call: Call<GetAllCategoriesResponse?>,
                    response: Response<GetAllCategoriesResponse?>
                ) {
                    if (response.isSuccessful) {
                        val categoryList = response.body()
                        if (categoryList != null) {
                            val categoryListJson = Gson().toJson(categoryList.result)
                            prefs.edit().putString("categoryList", categoryListJson).apply()
                        }
                    }
                }

                override fun onFailure(call: Call<GetAllCategoriesResponse?>, t: Throwable) {
                    Log.d("TAG", "onFailure fetchCategoriesFromApi: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("TAG", "fetchCategoriesFromApi: ${e.message}")
        }
    }

    private fun fetchCoefficientPlanFromApi() {
        try {
            val call = apiClient.getApiService()
                .getPercentAndMonth("Bearer ${sessionManager.fetchToken()}")
            call.enqueue(object : Callback<PercentInstallment?> {
                override fun onResponse(
                    call: Call<PercentInstallment?>,
                    response: Response<PercentInstallment?>
                ) {
                    if (response.isSuccessful) {
                        val coefficientPlanList = response.body()
                        if (coefficientPlanList != null) {
                            val coefficientPlanJson = Gson().toJson(coefficientPlanList.result)
                            prefs.edit().putString("coefficientPlan", coefficientPlanJson).apply()
                        }
                    }
                }

                override fun onFailure(call: Call<PercentInstallment?>, t: Throwable) {
                    Log.d("TAG", "onFailure fetchCategoriesFromApi: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("TAG", "fetchCategoriesFromApi: ${e.message}")
        }
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
        prefs.edit()?.putBoolean("first_run", true)?.apply()
        prefs.edit()?.putString("selectedCategory", "all")?.apply()
    }

    @SuppressLint("ResourceAsColor", "CommitPrefEdits")
    override fun onDestroy() {
        super.onDestroy()
        Cart.saveCartToPrefs(this)


        val badge = bottomNavBar.getOrCreateBadge(R.id.mainCartFragment)
        badgeManager.saveBadgeCount(badge.number)
        prefs.edit()?.putString("selectedCategory", "all")?.apply()
        prefs.edit()?.putBoolean("first_run", false)?.apply()
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Нажмите еще раз чтобы выйти", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

//    override fun onBackPressed() {
//        val navController = findNavController(R.id.fragmentMain)
//
//        // Если навигация возможна назад, выполните навигацию
//        if (navController.currentDestination?.id != R.id.barCodeScannerFragment) {
//            super.onBackPressed()
//        }
//    }
}