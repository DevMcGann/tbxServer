package com.telecom.android.tserver.data


import android.content.Context
import android.content.SharedPreferences

class SharedPreference(val context: Context) {
    private val PREFS_NAME = "Telecom"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    fun saveToPrefs(KEY_NAME: String, text: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, text)
        editor!!.commit()
    }

    fun screenInfoToPreference(appName:String, appVersion:String, appFecha:String){
        val editor : SharedPreferences.Editor = sharedPref.edit()
        val info = appName!! + " : " + appVersion!! + " -  " +  "fecha: " + appFecha!!
        editor.putString("info", info)
        editor!!.commit()
    }

    fun getScreenInfoPreference(): String? {
        return sharedPref.getString("info", null)
    }

    fun getFromPrefs(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }


    fun getAll () : MutableMap<String, *>? {
        return sharedPref.all
    }

    fun clearSharedPreference() {
        val settings = context.getSharedPreferences("Telecom", Context.MODE_PRIVATE)
        settings.edit().clear().commit()

    }

    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.commit()
    }


}