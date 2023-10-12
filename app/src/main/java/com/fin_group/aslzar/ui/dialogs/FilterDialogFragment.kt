package com.fin_group.aslzar.ui.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentFilterDialogBinding
import com.fin_group.aslzar.response.Category
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.CategoryClickListener
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.setWidthPercent


class FilterDialogFragment : BaseDialogFragment() {

    private var _binding: FragmentFilterDialogBinding? = null
    private val binding get() = _binding!!

    lateinit var apiService: ApiClient
    lateinit var sessionManager: SessionManager

    private lateinit var progressBar: ProgressBar

    private var categories: List<Category> = emptyList()
    private var categoryClickListener: CategoryClickListener? = null

    private lateinit var preferences: SharedPreferences

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

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories.map { it.name })



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(95)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}