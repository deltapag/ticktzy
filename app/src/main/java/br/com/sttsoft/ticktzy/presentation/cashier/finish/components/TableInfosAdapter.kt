package br.com.sttsoft.ticktzy.presentation.cashier.finish.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.sttsoft.ticktzy.databinding.ComponentTableInfoRowBinding
import br.com.sttsoft.ticktzy.databinding.ComponentTableInfoSectionBinding

class TableInfosAdapter(private val rows: List<tableInfos>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_ROW = 0
        private const val TYPE_SECTION = 1
    }

    inner class TableRowViewHolder(val binding: ComponentTableInfoRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class TableSectionViewHolder(val binding: ComponentTableInfoSectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (rows[position]) {
            is tableInfos.tableRow -> TYPE_ROW
            is tableInfos.tableSection -> TYPE_SECTION
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ROW -> {
                val binding =
                    ComponentTableInfoRowBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                TableRowViewHolder(binding)
            }

            TYPE_SECTION -> {
                val binding =
                    ComponentTableInfoSectionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                TableSectionViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Tipo desconhecido")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = rows[position]) {
            is tableInfos.tableRow -> {
                val binding = (holder as TableRowViewHolder).binding
                binding.tvChave.text = item.chave
                binding.tvValor.text = item.valor

                val proximo = rows.getOrNull(position + 1)
                val deveMostrarSeparador = proximo is tableInfos.tableRow
                binding.separador.visibility = if (deveMostrarSeparador) View.VISIBLE else View.GONE
            }

            is tableInfos.tableSection -> {
                val binding = (holder as TableSectionViewHolder).binding
                binding.tvSecao.text = item.titulo
            }
        }
    }

    override fun getItemCount(): Int = rows.size
}
