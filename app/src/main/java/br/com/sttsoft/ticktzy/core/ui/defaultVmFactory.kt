package br.com.sttsoft.ticktzy.core.ui

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified VM : ViewModel> ComponentActivity.defaultVmFactory(crossinline factory: () -> VM) =
    lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return factory() as T
                }
            },
        )[VM::class.java]
    }
