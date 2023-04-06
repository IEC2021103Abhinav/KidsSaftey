package com.example.kidssaftey

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isUserLoggedIn=SharedPref.getBoolean(PrefConst.IS_USER_LOGGED_IN)
        if(isUserLoggedIn)
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else
        {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()

        }
    }


}