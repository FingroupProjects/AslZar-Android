package com.fin_group.aslzar.ui.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.adapter.ProductsInAddAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentPickCharacterProductDialogBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.util.AddingProduct
import com.fin_group.aslzar.util.BaseDialogFullFragment
import com.fin_group.aslzar.util.FilialListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.UnauthorizedDialogFragment
import com.fin_group.aslzar.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class PickCharacterProductDialogFragment : BaseDialogFullFragment(), FilialListener, AddingProduct {

    private var _binding: FragmentPickCharacterProductDialogBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var typeList: List<Type>
    private var sortedTypeList: MutableList<Type> = mutableListOf()
    private lateinit var myAdapter: ProductsInAddAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var listener: AddingProduct
    private lateinit var listener2: FilialListener

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    private lateinit var product: ResultX
    private lateinit var filterModel: FilterModel

    companion object {
        fun newInstance(product: ResultX, filterModel: FilterModel): PickCharacterProductDialogFragment {
            val dialog = PickCharacterProductDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, product)
            args.putSerializable(ARG_FILTERED_TYPE, filterModel)
            dialog.arguments = args
            return dialog
        }

        private const val ARG_PRODUCT = "addingProduct"
        private const val ARG_FILTERED_TYPE = "filteredTypes"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickCharacterProductDialogBinding.inflate(inflater, container, false)
        recyclerView = binding.rvCategories
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        arguments?.let {
            product = it.getParcelable(ARG_PRODUCT)!!
            filterModel = it.getParcelable(ARG_FILTERED_TYPE)!!
        }

        sessionManager = SessionManager(requireContext())
        apiClient = ApiClient()
        apiClient.init(sessionManager)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.btnRefresh.setOnClickListener {
            getTypesFromApi()
        }

        myAdapter = ProductsInAddAdapter(product, sortedTypeList, this, this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = myAdapter
        setData(product)
        filterCounts(product, filterModel)
    }

    fun setListeners(
        listenerFun: AddingProduct,
        listener2Fun: FilialListener
    ) {
        listener = listenerFun
        listener2 = listener2Fun
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setData(product: ResultX) {
        typeList = product.types
        sortedTypeList.clear()

        for (type in typeList) {
            if (type.counts.isNotEmpty()) {
                val filteredCounts = filterFilials(type.counts, filterModel)
                if (filteredCounts.isNotEmpty()) {
                    val sortedCounts = filteredCounts.sortedWith(compareByDescending<Count> { it.is_filial }.thenBy { count ->
                        count.price.toDouble()
                    })
                    sortedTypeList.add(type.copy(counts = sortedCounts))
                }
            }
        }

        sortedTypeList = sortedTypeList.sortedBy { type ->
            type.counts.minByOrNull { count -> count.price.toDouble() }?.price?.toDouble() ?: 0.0
        }.toMutableList()

        myAdapter.upgradeList(sortedTypeList)
        binding.productFullName.text = product.full_name
        binding.productName.text = product.name
    }


    private fun filterFilials(filials: List<Count>, filterModel: FilterModel): List<Count> {
        return filials.filter { filial ->
            filial.price.toDouble() >= filterModel.priceFrom.toDouble() &&
                    filial.price.toDouble() <= filterModel.priceTo.toDouble()
        }
    }

    private fun filterCounts(product: ResultX, filterModel: FilterModel) {
        val filteredList = sortedTypeList.filter {
            product.types.any { type ->
                (type.filter || type.size.toDouble() > 0.0) &&
                type.counts.any { count ->
                    count.price.toDouble() >= filterModel.priceFrom.toDouble() &&
                    count.price.toDouble() <= filterModel.priceTo.toDouble()
                }
            } &&
            product.types.any { type ->
                (type.filter || type.size.toDouble() >= filterModel.sizeFrom.toDouble()) &&
                (type.filter || type.size.toDouble() <= filterModel.sizeTo.toDouble()) &&
                type.weight.toDouble() >= filterModel.weightFrom.toDouble() &&
                type.weight.toDouble() <= filterModel.weightTo.toDouble()
            }
        }
        myAdapter.upgradeList(filteredList)
    }

    override fun addProduct(product: ResultX, type: Type, count: Count) {
        listener.addProduct(product, type, count)
    }

    override fun addFilial(product: ResultX, type: Type, filial: Count) {
        listener2.addFilial(product, type, filial)
    }

    private fun getTypesFromApi(){
        val call = apiClient.getApiService().getProductByID("Bearer ${sessionManager.fetchToken()}", product.id)
        call.enqueue(object : Callback<ResultX?> {
            override fun onResponse(call: Call<ResultX?>, response: Response<ResultX?>) {
                if (response.isSuccessful){
                    val productResponse = response.body()
                    if (productResponse != null){
                        product = productResponse
                        sortedTypeList = mutableListOf()
                        setData(product)
                    }
                } else {
                    when(response.code()){
                        401 -> {
                            UnauthorizedDialogFragment.showUnauthorizedError(
                                requireContext(),
                                sharedPreferences,
                                this@PickCharacterProductDialogFragment
                            )
                        }
                        500 -> {
                            Toast.makeText(requireContext(), "Проблемы с сервером, повторите попытку позже", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(requireContext(), "Произошла ошибка.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResultX?>, t: Throwable) {
                Toast.makeText(requireContext(), "Произошла ошибка, повторите попытку позже", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    }

}