package com.fin_group.aslzar.ui.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentManager


import com.fin_group.aslzar.databinding.FragmentProfileBinding
import com.fin_group.aslzar.ui.dialogs.ChangeDataProfileDialogFragment
import com.fin_group.aslzar.ui.dialogs.ChangePasswordProfileFragmentDialog

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

        goToChangePasswordDialog()



        return binding.root
    }


    private fun goToChangePasswordDialog() {
//        binding.btnChangeData.setOnClickListener {
//            val changeDataDialog = ChangeDataProfileDialogFragment()
//            val fragmentManager: FragmentManager? = activity?.supportFragmentManager
//            fragmentManager?.let {
//                val transaction: FragmentTransaction = it.beginTransaction()
//                transaction.addToBackStack(null) // Это добавит текущий фрагмент в стек обратного перехода
//                changeDataDialog.show(transaction, "ChangeDataProfileDialogFragment")
//            }
//        }

        binding.btnChangePassword.setOnClickListener {
            val changeDataPassword = ChangePasswordProfileFragmentDialog()
            val fragmentManager: FragmentManager? = activity?.supportFragmentManager
            fragmentManager?.let {
                val transaction: FragmentTransaction = it.beginTransaction()
                transaction.addToBackStack(null) // Это добавит текущий фрагмент в стек обратного перехода
                changeDataPassword.show(transaction, "ChangePasswordProfileDialogFragment")
            }
        }
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