package br.com.sttsoft.ticktzy.presentation.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import br.com.sttsoft.ticktzy.R

class ConfirmDialog(
    private val onChoose: (String) -> Unit,
    private val title: String,
    private val body: String
) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm, null)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvBody = view.findViewById<TextView>(R.id.tv_body)
        val btnYes = view.findViewById<TextView>(R.id.btn_yes)
        val btnNo = view.findViewById<TextView>(R.id.btn_no)

        tvTitle.text = title
        tvBody.text = body

        btnYes.setOnClickListener {
            onChoose("yes")
            dismiss()
        }

        btnNo.setOnClickListener {
            onChoose("no")
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