package com.mahesch.trymyui.viewmodelfactory

import LoginActivityViewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginViewModelFactory(private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {

   /* override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//modelClass.getConstructor(Application::class.java).newInstance(application)
        return LoginActivityViewModel(application)
    }*/

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginActivityViewModel(application) as T
    }

}