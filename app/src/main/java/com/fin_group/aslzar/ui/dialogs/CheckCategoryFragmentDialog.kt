package com.fin_group.aslzar.ui.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.adapter.CategoryAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentDialogCheckCategoryBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.ui.fragments.main.MainFragment
import com.fin_group.aslzar.ui.fragments.main.functions.getAllCategoriesFromApi
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.setWidthPercent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CheckCategoryFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentDialogCheckCategoryBinding? = null
    private val binding get() = _binding!!

    lateinit var apiService: ApiClient
    lateinit var sessionManager: SessionManager
    lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var categories: List<Category> = emptyList()
    private var categoryClickListener: CategoryClickListener? = null

    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogCheckCategoryBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        recyclerView = binding.rvCategories
        progressBar = binding.progressLinearDeterminate
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())




//        categories = listOf(
//            Category("all", "Все"),
//            Category("00001", "Кольца"),
//            Category("00002", "Серьги"),
//            Category("00003", "Ожерелья"),
//            Category("00004", "Браслеты"),
//            Category("00005", "Подвески"),
//            Category("00006", "Часы"),
//        )
        getAllCategoriesFromApi()

        fetchRV(categories)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(80)

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnRefresh.setOnClickListener {
            getAllCategoriesFromApi()
        }
    }

    private fun fetchRV(categoryList: List<Category>){
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CategoryAdapter(categoryList) { selectedCategory ->
            categoryClickListener?.onCategorySelected(selectedCategory)
            dismiss()
        }
    }

    fun setCategoryClickListener(listener: CategoryClickListener) {
        categoryClickListener = listener
    }

//    private fun getAllCategoriesFromPrefs(){
//        binding.view.visibility = INVISIBLE
//        progressBar.visibility = VISIBLE
//        try {
//
//        }
//    }

    fun getAllCategoriesPrefs() {
        try {
            val categoriesListJson = preferences.getString("categoryList", null)
            if (categoriesListJson != null){
                val categoryListType = object : TypeToken<List<Category>>() {}.type
                val categoryList = Gson().fromJson<List<Category>>(categoriesListJson, categoryListType)
                val firstCategory = Category("all", "Все")
                categories = categoryList
                categories = mutableListOf(firstCategory).plus(categories)
                fetchRV(categories)
            } else {
                getAllCategoriesFromApi()
            }
        }catch (e: Exception){
            Log.d("TAG", "getAllCategoriesPrefs: ${e.message}")
        }
    }

    private fun getAllCategoriesFromApi(){
        binding.view.visibility = INVISIBLE
        progressBar.visibility = VISIBLE
        try {
            val call = apiService.getApiService().getAllCategories("Bearer ${sessionManager.fetchToken()}")
            call.enqueue(object : Callback<GetAllCategoriesResponse?> {
                override fun onResponse(
                    call: Call<GetAllCategoriesResponse?>,
                    response: Response<GetAllCategoriesResponse?>
                ) {
                    progressBar.visibility = GONE
                    binding.view.visibility = VISIBLE
                    if (response.isSuccessful){
                        val categoryList = response.body()?.result
                        if (categoryList != null) {
                            val firstCategory = Category("all", "Все")
                            categories = categoryList
                            categories = mutableListOf(firstCategory).plus(categories)
                            fetchRV(categories)

                            val categoryListJson = Gson().toJson(categories)
                            preferences.edit().putString("categoryList", categoryListJson).apply()
                        } else {
                            Toast.makeText(requireContext(), "Категории не найдены", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Ошибка, повторите попытку", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<GetAllCategoriesResponse?>, t: Throwable) {
                    progressBar.visibility = GONE
                    binding.view.visibility = VISIBLE
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        }catch (e: Exception){
            Log.d("TAG", "getAllCategories: ${e.message}")
            progressBar.visibility = GONE
            binding.view.visibility = VISIBLE
        }
    }
}