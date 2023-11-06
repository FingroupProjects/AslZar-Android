package com.fin_group.aslzar.ui.dialogs

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.CategoryAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentFilterDialogBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.paymentClient
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.printPercent
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions.textWatchers
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.formatNumber
import com.fin_group.aslzar.util.hideKeyboard
import com.fin_group.aslzar.util.setWidthPercent
import com.fin_group.aslzar.util.setupKeyboardScrolling
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log


class FilterDialogFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentFilterDialogBinding? = null
    private val binding get() = _binding!!

    lateinit var apiService: ApiClient
    lateinit var sessionManager: SessionManager

    private lateinit var progressBar: ProgressBar

    private var categories: List<Category> = emptyList()
    private lateinit var selectedCategory: Category

    private lateinit var arrayAdapterCategories: ArrayAdapter<String>
    private var categoryClickListener: CategoryClickListener? = null
    private lateinit var categoriesRV: RecyclerView
//    private lateinit var categoriesSpinner: Spinner

    private lateinit var preferences: SharedPreferences

    private var showCategories: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterDialogBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        progressBar = binding.progressLinearDeterminate
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        categoriesSpinner = binding.filterCategory
        categoriesRV = binding.categoriesRv

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogHeightPercent(95)

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnRefresh.setOnClickListener {
            getAllCategoriesFromApi()
        }

        getAllCategoriesPrefs()
//        fetchSpinner(categories, categoriesSpinner)


//        categoriesSpinner.setOnItemClickListener { parent, view, position, id ->
//            Toast.makeText(requireContext(), "${categories[position]}", Toast.LENGTH_SHORT).show()
//        }

        binding.filterCategory.setOnClickListener {
            if (!showCategories){
                categoriesRV.visibility = VISIBLE
                showCategories = true
            } else {
                categoriesRV.visibility = GONE
                showCategories = false
            }
        }

        binding.btnSetFilter.setOnClickListener{

//            val cat: String = categoriesSpinner.selectedItem as String
//            selectedCategory = categories.find { it.name == cat }!!
//            categoryClickListener?.onCategorySelected(selectedCategory)
//
//            Log.d("TAG", "onViewCreated: $cat")
            Log.d("TAG", "onViewCreated: $selectedCategory")

        }
    }

    @SuppressLint("SetTextI18n")
    fun fetchRv(categoryList: List<Category>){
        categoriesRV.layoutManager = LinearLayoutManager(requireContext())
        categoriesRV.adapter = CategoryAdapter(categoryList) { selectCategory ->
//            categoryClickListener?.onCategorySelected(selectedCategory)
            selectedCategory = categories.find { it.name == selectCategory.name }!!
            binding.filterCategory.text = "Выбранная категория: ${selectCategory.name}"
        }
    }

    fun fetchSpinner(categoryList: List<Category>, spinner: Spinner){
        val categoryNames = categoryList.map { it.name }.toTypedArray()
        arrayAdapterCategories = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryNames)

        spinner.adapter = arrayAdapterCategories
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = categoryList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ничего не выбрано
            }
        }
    }

    fun setCategoryClickListener(listener: CategoryClickListener) {
        categoryClickListener = listener
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getAllCategoriesPrefs() {
        try {
            val categoriesListJson = preferences.getString("categoryList", null)
            if (categoriesListJson != null){
                val categoryListType = object : TypeToken<List<Category>>() {}.type
                val categoryList = Gson().fromJson<List<Category>>(categoriesListJson, categoryListType)
                categories = categoryList
                binding.view.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
//                fetchSpinner(categories, categoriesSpinner)
                fetchRv(categories)
            } else {
                getAllCategoriesFromApi()
            }
        }catch (e: Exception){
            Log.d("TAG", "getAllCategoriesPrefs: ${e.message}")
        }
    }

    private fun getAllCategoriesFromApi(){
        binding.view.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        try {
            val call = apiService.getApiService().getAllCategories("Bearer ${sessionManager.fetchToken()}")
            call.enqueue(object : Callback<GetAllCategoriesResponse?> {
                override fun onResponse(
                    call: Call<GetAllCategoriesResponse?>,
                    response: Response<GetAllCategoriesResponse?>
                ) {
                    progressBar.visibility = View.GONE
                    binding.view.visibility = View.VISIBLE
                    if (response.isSuccessful){
                        val categoryList = response.body()?.result
                        if (categoryList != null) {
                            val firstCategory = Category("all", "Все")
                            categories = categoryList
                            categories = mutableListOf(firstCategory).plus(categories)
//                            fetchSpinner(categories, categoriesSpinner)
                            fetchRv(categories)

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
                    progressBar.visibility = View.GONE
                    binding.view.visibility = View.VISIBLE
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        } catch (e: Exception){
            Log.d("TAG", "getAllCategories: ${e.message}")
            progressBar.visibility = View.GONE
            binding.view.visibility = View.VISIBLE
        }
    }

}