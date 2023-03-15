package com.isayevapps.blackbeaty2

import android.app.Application
import com.isayevapps.blackbeaty2.models.Model
import com.isayevapps.blackbeaty2.viewmodels.ViewModel


class App : Application() {
    lateinit var viewModel: ViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = ViewModel(Model(this))
    }

}