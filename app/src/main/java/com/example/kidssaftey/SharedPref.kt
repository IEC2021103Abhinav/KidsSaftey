package com.example.kidssaftey

import android.content.Context
import android.content.SharedPreferences

object SharedPref {

    private const val NAME = "KidsSafetySharedPref"
    private const val MODE = Context.MODE_PRIVATE
    private  lateinit var preferences:SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    fun putBoolean(key:String,value:Boolean)
    {
        preferences.edit().putBoolean(key,value).apply()
    }
    fun getBoolean(key: String):Boolean{
//        key pass karenge wo key se related boolean pass kar degas
        return preferences.getBoolean(key,false)
    }

}