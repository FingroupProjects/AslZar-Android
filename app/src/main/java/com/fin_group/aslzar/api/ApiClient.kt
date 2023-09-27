package com.fin_group.aslzar.api

import android.annotation.SuppressLint
import android.view.View
import com.fin_group.aslzar.util.SessionManager
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@SuppressLint("StaticFieldLeak")
class ApiClient {

    private lateinit var apiService: ApiService
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var sessionManager: SessionManager

    fun init(sessionManager: SessionManager) {
        this.sessionManager = sessionManager
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

    fun getApiServiceForgotPassword(): ApiService {
        val credentials = Credentials.basic("Admin1", "2023")
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

    fun clearPassLogin() {
        sessionManager.clearPasswordLogin()
    }
}