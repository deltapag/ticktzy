package br.com.sttsoft.ticktzy.presentation.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.R

class PaymentTypeChooseDialog(
    private val onSelect: (String) -> Unit,
    private val moneyIsEnable: Boolean = false
) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_payment_type_choose, null)

        val btnDebito = view.findViewById<TextView>(R.id.btnDebito)
        val btnCredito = view.findViewById<TextView>(R.id.btnCredito)
        val btnCancelar = view.findViewById<TextView>(R.id.btnCancelar)
        val btnMoney = view.findViewById<TextView>(R.id.btnDinheiro)
        val btnSitefDireto = view.findViewById<TextView>(R.id.btnSitefDireto)

        if (!moneyIsEnable) {
            btnMoney.visibility = View.GONE
        }

        if (!BuildConfig.DEBUG) {
            btnSitefDireto.visibility = View.GONE
        }

        btnDebito.setOnClickListener {
            onSelect("debit")
            dismiss()
        }

        btnCredito.setOnClickListener {
            onSelect("credit")
            dismiss()
        }

        btnMoney.setOnClickListener {
            onSelect("money")
            dismiss()
        }

        btnSitefDireto.setOnClickListener {
            onSelect("sitef")
            dismiss()
        }

        btnCancelar.setOnClickListener {
            dismiss()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}