package br.com.sttsoft.ticktzy.presentation.sale.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.extensions.setImageFromBase64
import br.com.sttsoft.ticktzy.extensions.toReal
import br.com.sttsoft.ticktzy.repository.local.product

class ProductAdapter(
    productList: List<product>,
    private val onTotalChanged: (Double) -> Unit
) : RecyclerView.Adapter<ProductViewHolder>() {

    private val originalList = productList.toList()
    private val filteredList = mutableListOf<product>().apply { addAll(productList) }

    private var selectedPosition: Int = RecyclerView.NO_POSITION
    private var lastClickTime = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        bindItem(holder, filteredList[position], position)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = filteredList[position]
        val isSelected = position == selectedPosition

        if (item.habilitado) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position)
            } else {
                // Atualiza badge
                holder.tvBadge.text = item.quantity.toString()
                holder.tvBadge.visibility = if (item.quantity > 0) View.VISIBLE else View.GONE

                // Atualiza visibilidade dos botões
                holder.btnPlus.visibility = if (isSelected) View.VISIBLE else View.GONE
                holder.btnMinus.visibility = if (isSelected) View.VISIBLE else View.GONE

                // Atualiza visual de seleção
                if (item.photo.isNotEmpty()) {
                    holder.ivProduct.alpha = if (isSelected) 0.5f else 1f
                } else {
                    holder.tvTitle.text = if (isSelected) item.name else item.price.toReal()
                    holder.tvName.visibility = if (isSelected) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun bindItem(holder: ProductViewHolder, item: product, position: Int) {
        val isSelected = position == selectedPosition

        holder.tvBadge.text = item.quantity.toString()
        holder.tvBadge.visibility = if (item.quantity > 0) View.VISIBLE else View.GONE

        holder.btnPlus.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.btnMinus.visibility = if (isSelected) View.VISIBLE else View.GONE

        if (item.photo.isEmpty()) {
            holder.tvName.text = item.name
            holder.tvName.visibility = if (isSelected) View.GONE else View.VISIBLE
            holder.ivProduct.visibility = View.GONE
            holder.tvTitle.text = if (isSelected) item.name else item.price.toReal()
        } else {
            holder.ivProduct.setImageFromBase64(item.photo)
            holder.ivProduct.visibility = View.VISIBLE
            holder.ivProduct.alpha = if (isSelected) 0.5f else 1f
            holder.tvName.visibility = View.GONE
            holder.tvTitle.text = item.price.toReal()
        }

        holder.itemView.setOnClickListener {
            val now = System.currentTimeMillis()
            if (now - lastClickTime < 300) return@setOnClickListener // Evita clique duplo
            lastClickTime = now

            item.quantity++
            val previous = selectedPosition
            selectedPosition = position

            if (previous != RecyclerView.NO_POSITION) notifyItemChanged(previous, "partial")
            notifyItemChanged(position, "partial")

            notifyTotalChanged()
        }

        holder.btnPlus.setOnClickListener {
            item.quantity++
            notifyItemChanged(position, "partial")
            notifyTotalChanged()
        }

        holder.btnMinus.setOnClickListener {
            if (item.quantity > 0) item.quantity--
            notifyItemChanged(position, "partial")
            notifyTotalChanged()
        }
    }

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(originalList)
        } else {
            val lower = query.lowercase()
            filteredList.addAll(originalList.filter { it.name.lowercase().contains(lower) })
        }
        notifyDataSetChanged()
        notifyTotalChanged()
    }

    private fun notifyTotalChanged() {
        onTotalChanged(getTotal())
    }

    fun getTotal(): Double {
        return filteredList.sumOf { it.price * it.quantity }
    }

    fun getSelectedProducts(): List<product> {
        return filteredList.filter { it.quantity > 0 }
    }
}