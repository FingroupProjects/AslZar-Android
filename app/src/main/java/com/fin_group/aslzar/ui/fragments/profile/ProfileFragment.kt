package com.fin_group.aslzar.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentManager
import com.fin_group.aslzar.R


import com.fin_group.aslzar.databinding.FragmentProfileBinding
import com.fin_group.aslzar.ui.activities.LoginActivity
import com.fin_group.aslzar.ui.dialogs.ChangeDataProfileDialogFragment
import com.fin_group.aslzar.ui.dialogs.ChangePasswordProfileFragmentDialog
import com.fin_group.aslzar.ui.dialogs.SignOutProfileFragmentDialog
import com.fin_group.aslzar.util.hideBottomNav
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        goToChangePasswordDialog()
        
        return binding.root
    }

    private fun goToChangePasswordDialog() {
        binding.btnChangePassword.setOnClickListener {
            val changeDataPassword = ChangePasswordProfileFragmentDialog()
            val fragmentManager: FragmentManager? = activity?.supportFragmentManager
            fragmentManager?.let {
                val transaction: FragmentTransaction = it.beginTransaction()
                transaction.addToBackStack(null)
                changeDataPassword.show(transaction, "ChangePasswordProfileDialogFragment")
            }
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
}