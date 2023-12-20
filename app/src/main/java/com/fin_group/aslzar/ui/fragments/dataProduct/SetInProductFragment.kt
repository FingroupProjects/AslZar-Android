package com.fin_group.aslzar.ui.fragments.dataProduct

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.ProductsAdapter
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentSetInProductBinding
import com.fin_group.aslzar.models.FilterModel
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.response.Count
import com.fin_group.aslzar.response.GetAllProductV2
import com.fin_group.aslzar.response.ResultX
import com.fin_group.aslzar.response.Type
import com.fin_group.aslzar.ui.activities.MainActivity
import com.fin_group.aslzar.ui.fragments.dataProduct.functions.showAddingToCartDialog
import com.fin_group.aslzar.util.AddingProduct
import com.fin_group.aslzar.util.BadgeManager
import com.fin_group.aslzar.util.FilialListener
import com.fin_group.aslzar.util.FilterViewModel
import com.fin_group.aslzar.util.ProductOnClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.viewmodel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class SetInProductFragment : Fragment(), ProductOnClickListener, AddingProduct, FilialListener {

    private var _binding: FragmentSetInProductBinding? = null
    private val binding get() = _binding!!

    val args by navArgs<SetInProductFragmentArgs>()

    val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var preferences: SharedPreferences

    lateinit var sessionManager: SessionManager
    lateinit var apiService: ApiClient

    private lateinit var mainActivity: MainActivity
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var badgeManager: BadgeManager

    lateinit var recyclerView: RecyclerView
    lateinit var myAdapter: ProductsAdapter
    var allProducts: List<ResultX> = emptyList()

    lateinit var filterViewModel: FilterViewModel
    var filterModel: FilterModel? = null
    var defaultFilterModel: FilterModel? = null

    lateinit var productId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetInProductBinding.inflate(inflater, container, false)
        productId = args.productId

        filterViewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]
        defaultFilterModel = filterViewModel.defaultFilterModel

        preferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)
        setHasOptionsMenu(true)

        recyclerView = binding.rvSetProduct
        myAdapter = ProductsAdapter(emptyList(), this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as? MainActivity ?: throw IllegalStateException("Activity is not MainActivity")

    }

    override fun addToCart(product: ResultX) {
        val newFilterModel = FilterModel(
            0,
            1000000000,
            0,
            10000,
            0,
            10000,
            Category("all", "Все")
        )
        showAddingToCartDialog(product, newFilterModel)
    }

    override fun inStock(product: ResultX) {
        TODO("Not yet implemented")
    }

    override fun getData(product: ResultX) {
        val action = SetInProductFragmentDirections.actionSetInProductFragmentToDataProductElseFragment2(product.id, product)
        findNavController().navigate(action)
    }

    override fun addProduct(product: ResultX, type: Type, count: Count) {
        TODO("Not yet implemented")
    }

    override fun addFilial(product: ResultX, type: Type, filial: Count) {
        TODO("Not yet implemented")
    }


    private fun getSetProduct(){
        val call = apiService.getApiService().getSetInProduct("Bearer ${sessionManager.fetchToken()}", productId)
        call.enqueue(object : Callback<GetAllProductV2?> {
            override fun onResponse(
                call: Call<GetAllProductV2?>,
                response: Response<GetAllProductV2?>
            ) {
                if (response.isSuccessful){
                    val setProductResponse = response.body()
                    if (setProductResponse != null){
                        allProducts = setProductResponse.result
                        myAdapter.updateProducts(allProducts)
                    }
                }
            }

            override fun onFailure(call: Call<GetAllProductV2?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    }
}