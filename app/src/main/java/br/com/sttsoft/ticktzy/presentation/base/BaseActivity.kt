package br.com.sttsoft.ticktzy.presentation.base

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import br.com.execucao.posmp_api.SmartPosHelper
import br.com.execucao.posmp_api.printer.PrinterService
import br.com.execucao.posmp_api.store.AppStatus
import br.com.sttsoft.ticktzy.core.device.PosmpReceiptController
import br.com.sttsoft.ticktzy.core.device.PosmpReceiptControllerImpl
import br.com.sttsoft.ticktzy.core.device.PrinterController
import br.com.sttsoft.ticktzy.core.device.PrinterControllerSunmi
import br.com.sttsoft.ticktzy.presentation.dialogs.LoadingDialogFragment
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService

abstract class BaseActivity: AppCompatActivity() {

    private var loadingDialog: DialogFragment? = null

    protected open val enablePrinterBinding: Boolean = false

    private var _sunmiPrinterService: SunmiPrinterService? = null
    protected val sunmiPrinterService: SunmiPrinterService?
        get() = _sunmiPrinterService

    protected val printerController: PrinterController by lazy {
        PrinterControllerSunmi { sunmiPrinterService }
    }

    protected open val enablePosPrinter: Boolean = false


    private var _posmpPrinterService: PrinterService? = null
    protected val posmpPrinterService: PrinterService? get() = _posmpPrinterService

    protected val posmpReceiptController: PosmpReceiptController by lazy {
        PosmpReceiptControllerImpl { posmpPrinterService }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {override fun handleOnBackPressed() {} })

        val nomeSimples = this::class.java.simpleName
        Log.d("indo_para", nomeSimples)
    }

    override fun onStart() {
        super.onStart()
        if (enablePrinterBinding) bindPrinter()
        if (enablePosPrinter) initPosPrinter()
    }

    override fun onStop() {
        if (enablePosPrinter) releasePosPrinter()
        super.onStop()
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

    fun showToast(message: String, isLongMessage: Boolean = false) {
        if (isLongMessage) {
            Toast.makeText(this@BaseActivity, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun bindPrinter() {
        InnerPrinterManager.getInstance().bindService(this, object : InnerPrinterCallback() {
            override fun onConnected(service: SunmiPrinterService) {
                _sunmiPrinterService = service
            }

            override fun onDisconnected() {
                _sunmiPrinterService = null
            }
        })
    }

    protected inline fun withPrinter(block: (SunmiPrinterService) -> Unit) {
        sunmiPrinterService?.let(block)
    }

    private fun initPosPrinter() {
        if (SmartPosHelper.getInstance() == null) {
            SmartPosHelper.init(applicationContext, AppStatus.ACTIVE)
        }
        _posmpPrinterService = SmartPosHelper.getInstance().printer
        _posmpPrinterService?.open()
    }


    private fun releasePosPrinter() {
        _posmpPrinterService = null
    }
}