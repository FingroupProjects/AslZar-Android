package com.fin_group.aslzar.ui.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.api.ApiService
import com.fin_group.aslzar.databinding.FragmentProfileBinding
import com.fin_group.aslzar.response.SalesPlan
import com.fin_group.aslzar.ui.dialogs.SignOutProfileFragmentDialog
import com.fin_group.aslzar.ui.fragments.profile.functions.getSalesPlan
import com.fin_group.aslzar.ui.fragments.profile.functions.goToChangePasswordDialog
import com.fin_group.aslzar.ui.fragments.profile.functions.speedometerView
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import com.github.anastr.speedviewlib.Speedometer

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var speedometer: Speedometer
    var salesPlanNumber: Number? = 0

    lateinit var progressBar: ProgressBar

    lateinit var apiService: ApiClient
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        speedometer = binding.speedView
        progressBar = binding.progressBar5
        setHasOptionsMenu(true)
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager, binding.root)

        getSalesPlan()

        val asd: Number? = salesPlanNumber

        speedometerView(asd!!.toFloat())
        binding.btnChangePassword.setOnClickListener {
            goToChangePasswordDialog()
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exit) {
            val signOutDialog = SignOutProfileFragmentDialog()
            signOutDialog.show(childFragmentManager, "sign_out_dialog")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNav()
    }
}