package com.fin_group.aslzar.util

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.fin_group.aslzar.R
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    private val KEY_ALIAS = "my_secret_key"
    private val hexArray = "0123456789ABCDEF".toCharArray()

    companion object {
        const val PREFS_KEY = "prefs"
        const val TOKEN = "user_token"
        const val KEY = "key_for_cipher"
        const val USER_FIO = "user_name"
        const val USER_LOCATION = "user_location"
        const val USER_LOGIN = "user_login"
        const val PASSWORD_PREF = "password_pref"
        const val IS_LOGGED_IN_KEY = "is_logged_in"
    }

    fun saveKey(key: SecretKeySpec){
        val editor = prefs.edit()
        editor.putString(KEY, key.toString())
        editor.apply()
    }

    fun saveToken(token: String){
        val editor = prefs.edit()
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun saveName(name: String) {
        val editor = prefs.edit()
        editor.putString(USER_FIO, name)
        editor.apply()
    }

    fun saveLogin(login: String) {
        val editor = prefs.edit()
        editor.putString(USER_LOGIN, login)
        editor.apply()
    }

    fun saveUserLocation(location: String){
        val editor = prefs.edit()
        editor.putString(USER_LOCATION, location)
        editor.apply()
    }

    fun savePassword(password: String) {
        val editor = prefs.edit()
        editor.putString(PASSWORD_PREF, password)
        editor.apply()
    }

    fun fetchKey(): String?{
        return prefs.getString(KEY, null)
    }

    fun fetchToken(): String? {
        return prefs.getString(TOKEN, null)
    }

    fun fetchName(): String? {
        return prefs.getString(USER_FIO, null)
    }

    fun fetchLogin(): String? {
        return prefs.getString(USER_LOGIN, null)
    }

    fun fetchPassword(): String? {
        return prefs.getString(PASSWORD_PREF, null)
    }



    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(USER_FIO)
        editor.remove(USER_LOGIN)
        editor.remove(PASSWORD_PREF)
        editor.putBoolean(IS_LOGGED_IN_KEY, false)
        editor.apply()
    }

    fun clearPasswordLogin(){
        val editor = prefs.edit()
        editor.remove(USER_LOGIN)
        editor.remove(PASSWORD_PREF)
        Log.d("TAG", "clearPasswordLogin: ${fetchPassword()} ${fetchLogin()}")
        editor.apply()
    }

    fun isLoginAndPasswordSaved(): Boolean {
        val login = fetchLogin()
        val password = fetchPassword()
        return !login.isNullOrEmpty() && !password.isNullOrEmpty()
    }
}
