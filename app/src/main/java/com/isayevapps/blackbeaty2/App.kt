package com.isayevapps.blackbeaty2

import android.app.Application


class App : Application() {
    lateinit var viewModel: ViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = ViewModel(Model(this))
    }

}