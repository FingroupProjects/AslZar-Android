package com.fin_group.aslzar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fin_group.aslzar.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}