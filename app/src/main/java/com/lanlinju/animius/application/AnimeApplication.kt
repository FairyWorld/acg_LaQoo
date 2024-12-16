package com.laqoome.laqoo.application

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class laqooApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        _instance = this

    }

    companion object {
        private lateinit var _instance: Application

        fun getInstance(): Context {
            return _instance
        }
    }
}