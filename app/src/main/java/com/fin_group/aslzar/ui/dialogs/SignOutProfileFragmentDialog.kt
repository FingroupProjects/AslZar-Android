package com.fin_group.aslzar.ui.dialogs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentSignOutProfileDialogBinding
import com.fin_group.aslzar.ui.activities.LoginActivity
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.SessionManager
import com.fin_group.aslzar.util.SessionManager.Companion.PREFS_KEY
import com.fin_group.aslzar.util.setWidthPercent

class SignOutProfileFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentSignOutProfileDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignOutProfileDialogBinding.inflate(inflater,container, false)
        sessionManager = SessionManager(requireContext())
        sharedPreferences = context?.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(90)

        binding.apply {
            actionCloseProfile.setOnClickListener { dismiss() }
            if (Cart.isCartEmpty()){
                textView2.text = "Вы уверены, что хотите выйти из своего аккаунта?"
                actionYesBtn.setOnClickListener {
                    sessionManager.clearSession()
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean(SessionManager.IS_LOGGED_IN_KEY, false)
                    editor.clear()
                    editor.remove("productList")
                    editor.apply()
                    val i = Intent(context, LoginActivity::class.java)
                    startActivity(i)
                    activity?.finish()
                    dismiss()
                }
            } else {
                textView2.text = "Вы уверены, что хотите выйти из своего аккаунта, в вашей корзине остались товары, она будет очищена если выёдите."
                actionYesBtn.setOnClickListener {
                    Cart.clearAllProducts(requireContext())
                    sessionManager.clearSession()
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean(SessionManager.IS_LOGGED_IN_KEY, false)
                    editor.clear()
                    editor.remove("productList")
                    editor.apply()
                    val i = Intent(context, LoginActivity::class.java)
                    startActivity(i)
                    activity?.finish()
                    dismiss()
                }
            }

            actionNoBtn.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}