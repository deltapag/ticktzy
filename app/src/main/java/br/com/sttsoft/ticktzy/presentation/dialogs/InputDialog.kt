package br.com.sttsoft.ticktzy.presentation.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import br.com.sttsoft.ticktzy.R

class InputDialog(
    private val onConfirm: (password: String, dialog: InputDialog) -> Unit
) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input, null)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val edtInput = view.findViewById<EditText>(R.id.edt_input)
        val btnOk = view.findViewById<TextView>(R.id.btn_ok)

        btnOk.setOnClickListener {
            onConfirm(edtInput.text.toString(),this)
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