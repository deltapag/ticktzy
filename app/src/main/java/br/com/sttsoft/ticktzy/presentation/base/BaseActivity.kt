package br.com.sttsoft.ticktzy.presentation.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

abstract class BaseActivity: AppCompatActivity() {

    private var loadingDialog: DialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nomeSimples = this::class.java.simpleName
        Log.d("indo_para", nomeSimples)
    }

    fun showLoading() {
        if (loadingDialog?.isAdded == true) return // Já está sendo mostrado

        loadingDialog = LoadingDialogFragment()
        loadingDialog?.show(supportFragmentManager, "loading_dialog")
    }

    fun hideLoading() {
        loadingDialog?.dismissAllowingStateLoss()
        loadingDialog = null
    }

    fun isLoadingVisible(): Boolean {
        return loadingDialog?.isVisible == true
    }
}