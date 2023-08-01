package com.fin_group.aslzar.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fin_group.aslzar.ui.fragments.login.FragmentLogin
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentLogin, FragmentLogin())
            .addToBackStack(null)
            .commit()


    }
}