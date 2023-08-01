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
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_LOGIN = "user_login"
        const val USER_ROLE = "user_role"
        const val USER_SCLAD = "user_sclad"
        const val PASSWORD_PREF = "password_pref"
        const val IS_LOGGED_IN_KEY = "is_logged_in"
    }

    fun saveId(id: String) {
        val editor = prefs.edit()
        editor.putString(USER_ID, id)
        editor.apply()
    }

    fun saveRole(role: String){
        val editor = prefs.edit()
        editor.putString(USER_ROLE, role)
        editor.apply()
    }

    fun saveName(name: String) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, name)
        editor.apply()
    }

    fun saveSclad(sclad: String) {
        val editor = prefs.edit()
        editor.putString(USER_SCLAD, sclad)
        editor.apply()
    }

    fun saveLogin(login: String) {
        val editor = prefs.edit()
        editor.putString(USER_LOGIN, login)
        editor.apply()
    }

    fun savePassword(password: String) {
        val editor = prefs.edit()
        editor.putString(PASSWORD_PREF, password)
        editor.apply()
    }

    fun fetchName(): String? {
        return prefs.getString(USER_NAME, null)
    }

    fun fetchId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun fetchSclad(): String? {
        return prefs.getString(USER_SCLAD, null)
    }

    fun fetchRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    fun fetchLogin(): String? {
        return prefs.getString(USER_LOGIN, null)
    }

    fun fetchPassword(): String? {
        return prefs.getString(PASSWORD_PREF, null)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(USER_ID)
        editor.remove(USER_NAME)
        editor.remove(USER_LOGIN)
        editor.remove(PASSWORD_PREF)
        editor.remove(USER_ROLE)
        editor.remove(USER_SCLAD)
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
