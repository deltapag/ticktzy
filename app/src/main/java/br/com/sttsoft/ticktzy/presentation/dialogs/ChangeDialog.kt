package br.com.sttsoft.ticktzy.presentation.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.sttsoft.ticktzy.databinding.DialogChangeBinding

class ChangeDialog (
    private val context: Context,
    private val chargeValue: Double,
    private val onConfirm: (value: Double, valueChange: Double, dialog: ChangeDialog) -> Unit,
    private val onCancel: (dialog: ChangeDialog) -> Unit
){

    private var input = ""
    private lateinit var dialog: Dialog
    private lateinit var binding: DialogChangeBinding

    fun show() {
        try {
            binding = DialogChangeBinding.inflate(LayoutInflater.from(context))
            dialog = Dialog(context)
            dialog.setContentView(binding.root)

            binding.tvChange.text = format(chargeValue)
            updateVale()

            val botoes = mapOf(
                binding.tvN1 to "1",
                binding.tvN2 to "2",
                binding.tvN3 to "3",
                binding.tvN4 to "4",
                binding.tvN5 to "5",
                binding.tvN6 to "6",
                binding.tvN7 to "7",
                binding.tvN8 to "8",
                binding.tvN9 to "9",
                binding.tvN0 to "0",
                binding.tvN00 to "00"
            )

            botoes.forEach { (view, digito) ->
                view.setOnClickListener {
                    input += digito
                    updateVale()
                }
            }

            binding.ivDelete.setOnClickListener {
                input = input.dropLast(1)
                updateVale()
            }

            binding.llConfirm.setOnClickListener {
                val value = getValue()
                val change = value - chargeValue
                onConfirm(value, change, this)
            }

            binding.llCancel.setOnClickListener {
                onCancel(this)
            }

            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            dialog.setCancelable(true)
            dialog.show()
        } catch (e: Exception) {
            println(e)
        }
    }

    private fun getValue(): Double {
        return input.toDoubleOrNull()?.div(100) ?: 0.0
    }

    private fun updateVale() {
        val value = getValue()
        val change = value - chargeValue

        binding.tvValor.text = format(value)
        binding.tvReturn.text = format(change)
    }

    private fun format(valor: Double): String {
        return "R$ %.2f".format(valor).replace('.', ',')
    }

    fun dismiss() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }

}