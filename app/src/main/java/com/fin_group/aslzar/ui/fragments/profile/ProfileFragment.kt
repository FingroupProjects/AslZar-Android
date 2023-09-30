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
import com.fin_group.aslzar.ui.fragments.profile.functions.changePassword
import com.fin_group.aslzar.ui.fragments.profile.functions.goToChangePasswordDialog
import com.fin_group.aslzar.ui.fragments.profile.functions.speedometerView
import com.fin_group.aslzar.util.NoInternetDialogFragment
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

    lateinit var key: String
    lateinit var keyBase64: ByteArray
    lateinit var encryptionKey: SecretKeySpec
    lateinit var encryptionManager: EncryptionManager

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
        NoInternetDialogFragment.showIfNoInternet(requireContext())

        salesPlanNumber = sessionManager.fetchSalesPlan()
        val asd: Number? = salesPlanNumber
        binding.apply {
            tvName.text = sessionManager.fetchName()
            tvUserLogin.text = sessionManager.fetchLogin()
            tvSubsidiary.text = sessionManager.fetchLocation()

            if (sessionManager.fetchEmail()?.isEmpty() == true){
                tvMail.text = "Почта не указана!"
            } else {
                tvMail.text = sessionManager.fetchEmail()
            }

            if (sessionManager.fetchNumberPhone()?.isEmpty() == true || sessionManager.fetchNumberPhone()?.toInt() == 0 ){
                tvNumberPhone.text = "Номер телефона не указан!"
            } else {
                tvNumberPhone.text = sessionManager.fetchNumberPhone()
            }

            key = sessionManager.fetchKey()!!
            keyBase64 = Base64.decode(key, Base64.DEFAULT)
            encryptionKey = SecretKeySpec(keyBase64, "AES")
            encryptionManager = EncryptionManager(encryptionKey)

            tvUserLogin.text = encryptionManager.decryptData(sessionManager.fetchLogin()!!)
        }

        speedometerView(asd!!.toFloat())
        binding.btnChangePassword.setOnClickListener {
            changePassword(binding)
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