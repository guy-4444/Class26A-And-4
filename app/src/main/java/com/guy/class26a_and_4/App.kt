package com.guy.class26a_and_4

import android.app.Application

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        MSPV3.init(this)
        MySignal.init(this)
    }
}