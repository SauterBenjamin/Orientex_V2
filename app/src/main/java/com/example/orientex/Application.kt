package com.example.orientex

import android.app.Application

class Orientex : Application() {

    override fun onCreate() {
        super.onCreate()

        Backend.initialize(applicationContext)
    }
}