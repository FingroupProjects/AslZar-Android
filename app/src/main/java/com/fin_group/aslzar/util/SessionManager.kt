package com.fin_group.aslzar.util

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.fin_group.aslzar.R
import com.fin_group.aslzar.response.SalesPlan
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class   SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    private val KEY_ALIAS = "my_secret_key"
    private val hexArray = "0123456789ABCDEF".toCharArray()

    companion object {
        const val PREFS_KEY = "prefs"
        const val TOKEN = "user_token"
        const val KEY = "key_for_cipher"
        const val USER_FIO = "user_name"
        const val USER_LOCATION = "user_location"
        const val USER_LOCATION_ID = "user_location_id"
        const val USER_CHECK = "user_check"
        const val USER_LOGIN = "user_login"
        const val USER_SALES_PLAN = "user_sales_plan"
        const val PASSWORD_PREF = "password_pref"
        const val USER_EMAIL = "user_email"
        const val USER_NUMBER = "user_number_phone"
        const val IS_LOGGED_IN_KEY = "is_logged_in"
    }

    fun saveKey(key: String){
        val editor = prefs.edit()
        editor.putString(KEY, key)
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

    fun saveSalesPlan(salesPlan: Number){
        val editor = prefs.edit()
        editor.putFloat(USER_SALES_PLAN, salesPlan.toFloat())
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

    fun saveEmail(email: String){
        val editor = prefs.edit()
        editor.putString(USER_EMAIL, email)
        editor.apply()
    }

    fun saveNumberPhone(number: String){
        val editor = prefs.edit()
        editor.putString(USER_NUMBER, number)
        editor.apply()
    }

    fun saveLocationId(locationId: String){
        val editor = prefs.edit()
        editor.putString(USER_LOCATION_ID, locationId)
        editor.apply()
    }

    fun saveCheck(check: Number){
        val editor = prefs.edit()
        editor.putFloat(USER_CHECK, check.toFloat())
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

    fun fetchEmail(): String? {
        return prefs.getString(USER_EMAIL, null)
    }

    fun fetchNumberPhone(): String? {
        return prefs.getString(USER_NUMBER, null)
    }

    fun fetchLocation(): String? {
        return prefs.getString(USER_LOCATION, null)
    }

    fun fetchLogin(): String? {
        return prefs.getString(USER_LOGIN, null)
    }

    fun fetchPassword(): String? {
        return prefs.getString(PASSWORD_PREF, null)
    }

    fun fetchSalesPlan(): Number {
        return prefs.getFloat(USER_SALES_PLAN, 0f)
    }

    fun fetchCheck(): Number {
        return prefs.getFloat(USER_SALES_PLAN, 0f)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(TOKEN)
        editor.remove(KEY)
        editor.remove(USER_FIO)
        editor.remove(USER_LOGIN)
        editor.remove(USER_LOCATION)
        editor.remove(USER_LOCATION_ID)
        editor.remove(PASSWORD_PREF)
        editor.remove(USER_CHECK)
        editor.remove(USER_SALES_PLAN)
        editor.remove(USER_EMAIL)
        editor.remove(USER_NUMBER)
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
