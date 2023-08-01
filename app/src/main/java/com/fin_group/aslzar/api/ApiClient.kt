package com.fin_group.aslzar.api

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Toast
import com.fin_group.aslzar.util.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import javax.crypto.SecretKey


@SuppressLint("StaticFieldLeak")
object ApiClient {
//
//    private lateinit var apiService: ApiService
//    private lateinit var okHttpClient: OkHttpClient
//    private lateinit var sessionManager: SessionManager
//
//    private var serverAvailable: Boolean = false
//    private lateinit var rootView: View
//
//    fun init(sessionManager: SessionManager, view: View) {
//        this.sessionManager = sessionManager
//        this.rootView = view
//
//        createHttpClient()
//        createRetrofit()
////        checkServerAvailability()
//    }
//
//    private fun createHttpClient() {
//        val username = sessionManager.fetchLogin() ?: ""
//        val password = sessionManager.fetchPassword() ?: ""
//        val credentials = Credentials.basic(username, password)
//        okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
//                val originalRequest = chain.request()
//                val authenticatedRequest = originalRequest.newBuilder().header("Authorization", credentials).build()
//                chain.proceed(authenticatedRequest)
//            }.build()
//    }
//
//    private fun createRetrofit() {
//        val gson = GsonBuilder().setLenient().create()
//
//        val retrofit = Retrofit.Builder().baseUrl(ApiConfig.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build()
//
//        apiService = retrofit.create(Api::class.java)
//    }
//
//    fun getApiService(): Api {
//        val username = sessionManager.fetchLogin() ?: ""
//        val password = sessionManager.fetchPassword() ?: ""
//        val credentials = Credentials.basic(username, password)
//        okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
//            val originalRequest = chain.request()
//            val authenticatedRequest =
//                originalRequest.newBuilder().header("Authorization", credentials).build()
//            chain.proceed(authenticatedRequest)
//        }.build()
//
//        val gson = GsonBuilder().setLenient().create()
//
//        if (!::apiService.isInitialized){
//            val retrofit = Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(okHttpClient)
//                .build()
//            apiService = retrofit.create(Api::class.java)
//        }
//
//        return apiService
//    }
//
//    fun getApiServiceLogin(login: String, password: String): Api {
//        val credentials = Credentials.basic(login, password)
//        val updatedClient = okHttpClient.newBuilder()
//            .addInterceptor { chain ->
//                val originalRequest = chain.request()
//                val authenticatedRequest = originalRequest.newBuilder()
//                    .header("Authorization", credentials)
//                    .build()
//                chain.proceed(authenticatedRequest)
//            }
//            .build()
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(ApiConfig.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(updatedClient)
//            .build()
//
//        return retrofit.create(Api::class.java)
//    }
//
//    fun updateCredentials(login: String, password: String) {
//        sessionManager.saveLogin(login)
//        sessionManager.savePassword(password)
//    }
//
//    fun clearPassLogin() {
//        sessionManager.clearPasswordLogin()
//    }
//
//    fun isServerAvailable(): Boolean {
//        return serverAvailable
//    }
}


class ServerUnavailableException(message: String) : Exception(message)
