package com.lenecoproekt.notes

import androidx.multidex.MultiDexApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin{
            modules(appModule, splashModule, mainModule, noteModule)
        }
    }
}