package com.fin_group.aslzar.ui.dialogs

import android.annotation.SuppressLint
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.adapter.CategoryAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentFilterDialogBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.GetAllCategoriesResponse
import com.fin_group.aslzar.util.BaseBottomSheetDialogFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.FilterDialogListener
import com.fin_group.aslzar.util.FilterViewModel
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.returnNumber
import com.fin_group.aslzar.util.setMaxValueET
import com.fin_group.aslzar.util.setMinMaxValueET
import com.fin_group.aslzar.util.setupEditTextBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class FilterDialogFragment : BaseBottomSheetDialogFragment() {
    private lateinit var filterViewModel: FilterViewModel

    private var _binding: FragmentFilterDialogBinding? = null
    private val binding get() = _binding!!

    lateinit var apiService: ApiClient
    lateinit var sessionManager: SessionManager

    private lateinit var progressBar: ProgressBar

    private var categories: List<Category> = emptyList()
    private lateinit var selectedCategory: Category

    private lateinit var arrayAdapterCategories: ArrayAdapter<String>
    private var categoryClickListener: CategoryClickListener? = null
    private var filterDialogListener: FilterDialogListener? = null
    private lateinit var categoriesRV: RecyclerView

    private lateinit var preferences: SharedPreferences

    private var showCategories: Boolean = true

    private lateinit var filter: FilterModel


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
        categoriesRV = binding.categoriesRv
        filterViewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogHeightPercent(95)
        val filterModel = filterViewModel.filterModel
        val filterModel2 = filterViewModel.defaultFilterModel
        if (filterModel != null) {
            setDataFilter(filterModel)
        } else {
            if (filterModel2 != null) {
                setDataFilter(filterModel2)
            }
        }
        if (filterModel2 != null){
            setDataFilter2(filterModel2)
        }
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnRefresh.setOnClickListener {
            getAllCategoriesFromApi()
        }

        getAllCategoriesPrefs()

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
            filterViewModel.filterModel = newFilterModel(binding)
            filterViewModel.filterChangeListener.postValue(filterViewModel.filterModel)

            setDataFilter(filterViewModel.filterModel!!)
            dismiss()
        }

        binding.btnClearFilter.setOnClickListener {
            filterViewModel.filterModel = filterViewModel.defaultFilterModel!!
            filterViewModel.filterChangeListener.postValue(filterViewModel.defaultFilterModel)

            setDataFilter(filterViewModel.defaultFilterModel!!)
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    fun fetchRv(categoryList: List<Category>){
        categoriesRV.layoutManager = LinearLayoutManager(requireContext())
        categoriesRV.adapter = CategoryAdapter(categoryList) { selectCategory ->
            selectedCategory = categories.find { it.name == selectCategory.name }!!
            binding.filterCategory.text = "Выбранная категория: ${selectCategory.name}"
        }
    }

    fun setFilterListener(listener: FilterDialogListener){
        filterDialogListener = listener
    }

    @SuppressLint("SetTextI18n")
    fun setDataFilter(filterModel: FilterModel){
        binding.apply {
            filterModel.apply {
                rangePriceFrom.setText(priceFrom.toString())
                rangePriceTo.setText(priceTo.toString())

                rangeSizeFrom.setText(sizeFrom.toString())
                rangeSizeTo.setText(sizeTo.toString())

                rangeWeightFrom.setText(weightFrom.toString())
                rangeWeightTo.setText(weightTo.toString())

                selectedCategory = category
                binding.filterCategory.text = "Выбранная категория: ${selectedCategory.name}"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun setDataFilter2(filterModel: FilterModel){
        binding.apply {
            filterModel.apply {
                rangePriceFrom.hint = priceFrom.toString()
                rangePriceTo.hint = priceTo.toString()
                setMinMaxValueET(rangePriceFrom, priceFrom, priceTo)
                setMinMaxValueET(rangePriceTo, priceFrom, priceTo)

                rangeSizeFrom.hint = sizeFrom.toString()
                rangeSizeTo.hint = sizeTo.toString()
                setMinMaxValueET(rangeSizeFrom, sizeFrom, sizeTo)
                setMinMaxValueET(rangeSizeTo, sizeFrom, sizeTo)

                rangeWeightFrom.hint = weightFrom.toString()
                rangeWeightTo.hint = weightTo.toString()
                setMinMaxValueET(rangeWeightFrom, weightFrom, weightTo)
                setMinMaxValueET(rangeWeightTo, weightFrom, weightTo)
                setupEditTextBehavior(rangePriceFrom, rangePriceTo, rangeSizeFrom, rangeSizeTo, rangeWeightFrom, rangeWeightTo)

                filterPriceRange.text = "от $priceFrom / до $priceTo"
                filterSizeRange.text = "от $sizeFrom / до $sizeTo"
                filterWeightRange.text = "от $weightFrom / до $weightTo"
            }
        }
    }
    private fun newFilterModel(binding: FragmentFilterDialogBinding): FilterModel {
        return FilterModel(
            returnNumber(binding.rangePriceFrom.text.toString()),
            returnNumber(binding.rangePriceTo.text.toString()),
            returnNumber(binding.rangeSizeFrom.text.toString()),
            returnNumber(binding.rangeSizeTo.text.toString()),
            returnNumber(binding.rangeWeightFrom.text.toString()),
            returnNumber(binding.rangeWeightTo.text.toString()),
            selectedCategory,
        )
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
                binding.view.visibility = VISIBLE
                progressBar.visibility = INVISIBLE
                fetchRv(categories)
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
                    progressBar.visibility = View.GONE
                    binding.view.visibility = View.VISIBLE
                    if (response.isSuccessful){
                        val categoryList = response.body()?.result
                        if (categoryList != null) {
                            val firstCategory = Category("all", "Все")
                            categories = categoryList
                            categories = mutableListOf(firstCategory).plus(categories)
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
                    progressBar.visibility = GONE
                    binding.view.visibility = VISIBLE
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        } catch (e: Exception){
            Log.d("TAG", "getAllCategories: ${e.message}")
            progressBar.visibility = GONE
            binding.view.visibility = VISIBLE
        }
    }

}