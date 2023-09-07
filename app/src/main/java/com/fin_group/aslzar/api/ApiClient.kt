package com.fin_group.aslzar.api

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.fin_group.aslzar.cipher.EncryptionManager
import com.fin_group.aslzar.util.SessionManager
import com.google.android.gms.common.api.Api
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


@SuppressLint("StaticFieldLeak")
class ApiClient {

    private lateinit var apiService: ApiService
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var sessionManager: SessionManager

    private var serverAvailable: Boolean = false
    private lateinit var rootView: View

    fun init(sessionManager: SessionManager, view: View) {
        this.sessionManager = sessionManager
        this.rootView = view

//        createHttpClient()
//        createRetrofit()
    }
//
//    private fun createHttpClient() {
//        val username = sessionManager.fetchLogin() ?: ""
//        val password = sessionManager.fetchPassword() ?: ""
//        val credentials = Credentials.basic(username, password)
//        okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
//            val originalRequest = chain.request()
//            val authenticatedRequest = originalRequest.newBuilder().header("Authorization", credentials).build()
//            chain.proceed(authenticatedRequest)
//        }.build()
//    }

    private fun createRetrofit() {
        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }


//    fun getApiService(): ApiService {
////        val username = sessionManager.fetchLogin() ?: ""
////        val password = sessionManager.fetchPassword() ?: ""
//        val key = sessionManager.fetchKey()
//        val keyBase64 = Base64.decode(key, Base64.DEFAULT)
//        val encryptionKey = SecretKeySpec(keyBase64, "AES")
//        val encryptionManager = EncryptionManager(encryptionKey)
//
//        val login = encryptionManager.decryptData(sessionManager.fetchLogin()!!)
//        val password = encryptionManager.decryptData(sessionManager.fetchPassword()!!)
//
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
//            .baseUrl(Constants.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(updatedClient)
//            .build()
//
//        return retrofit.create(ApiService::class.java)
//    }

    fun getApiService(): ApiService {
        val logging = HttpLoggingInterceptor()
        logging.level = (HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)

        val gson = GsonBuilder()
            .setLenient()
            .create()

        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.build())
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }


    fun getApiServiceLogin(login: String, password: String): ApiService {
        val credentials = Credentials.basic(login, password)
        okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val originalRequest = chain.request()
            val authenticatedRequest =
                originalRequest.newBuilder().header("Authorization", credentials).build()
            chain.proceed(authenticatedRequest)
        }.build()
        val updatedClient = okHttpClient.newBuilder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", credentials)
                    .build()
                chain.proceed(authenticatedRequest)
            }
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(updatedClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    fun updateCredentials(login: String, password: String) {
        sessionManager.saveLogin(login)
        sessionManager.savePassword(password)
    }

    fun clearPassLogin() {
        sessionManager.clearPasswordLogin()
    }

    fun isServerAvailable(): Boolean {
        return serverAvailable
    }
}