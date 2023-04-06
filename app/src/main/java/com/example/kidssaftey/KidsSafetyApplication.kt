package com.example.kidssaftey

import android.app.Application

class KidsSafetyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPref.init(this)

//        ye yahan pe ek baar initailize ho gya
//        ab poore project mein initialize karne ki jarurat nahi
//        ye ek application ban gya  to android manifest mein jaa kar application ka naam dena padega
    }
}