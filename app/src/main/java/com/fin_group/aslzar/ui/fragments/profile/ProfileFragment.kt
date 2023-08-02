package com.fin_group.aslzar.ui.fragments.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentProfileBinding
import com.fin_group.aslzar.ui.activities.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

       // setHasOptionsMenu(true)




        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.profile_fragment_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.exit) {
//            val dialog = AlertDialog.Builder(requireContext())
//            dialog.setMessage("Вы уверены что хотите выйте?")
//            dialog.setPositiveButton("Да") { _, _ ->
//                val i = Intent(context, LoginActivity::class.java)
//                startActivity(i)
//                activity?.finish()
//            }
//            dialog.setNegativeButton("Нет", null)
//            dialog.setCancelable(true)
//            dialog.show()
//        }
//        return super.onOptionsItemSelected(item)
//    }

//
//    override fun onStart() {
//        super.onStart()
//        val bottomNavView =
//            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        bottomNavView.animate().translationY(bottomNavView.height.toFloat()).setDuration(100)
//            .withEndAction {
//                bottomNavView.visibility = View.GONE
//            }.start()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        val bottomNavView =
//            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        bottomNavView.visibility = View.VISIBLE
//        bottomNavView.animate().translationY(0f).setDuration(300).start()
//    }
}