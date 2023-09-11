package com.fin_group.aslzar.ui.fragments.profile

import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.cipher.EncryptionManager
import com.fin_group.aslzar.databinding.FragmentProfileBinding
import com.fin_group.aslzar.ui.dialogs.SignOutProfileFragmentDialog
import com.fin_group.aslzar.ui.fragments.profile.functions.goToChangePasswordDialog
import com.fin_group.aslzar.ui.fragments.profile.functions.speedometerView
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.showBottomNav
import com.github.anastr.speedviewlib.Speedometer
import javax.crypto.spec.SecretKeySpec

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var speedometer: Speedometer
    var salesPlanNumber: Number? = 0

    lateinit var apiService: ApiClient
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        speedometer = binding.speedView
        setHasOptionsMenu(true)
        sessionManager = SessionManager(requireContext())
        apiService = ApiClient()
        apiService.init(sessionManager)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        swipeRefreshLayout.setOnRefreshListener {
//            getSalesPlan()
//        }

//        getSalesPlan()
        salesPlanNumber = sessionManager.fetchSalesPlan()
        val asd: Number? = salesPlanNumber
        binding.apply {
            editName.text = sessionManager.fetchName()
            editLogin.text = sessionManager.fetchLogin()
            editFilial.text = sessionManager.fetchLocation()

            val key = sessionManager.fetchKey()
            val keyBase64 = Base64.decode(key, Base64.DEFAULT)
            val encryptionKey = SecretKeySpec(keyBase64, "AES")
            val encryptionManager = EncryptionManager(encryptionKey)

            editLogin.text = encryptionManager.decryptData(sessionManager.fetchLogin()!!)
        }

        speedometerView(asd!!.toFloat())
        binding.btnChangePassword.setOnClickListener {
            goToChangePasswordDialog()
        }
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