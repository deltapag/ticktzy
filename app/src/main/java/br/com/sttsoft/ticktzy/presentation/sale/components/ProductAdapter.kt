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
    private val onTotalChanged: (Double) -> Unit) : RecyclerView.Adapter<ProductViewHolder>()  {

    private val originalList = productList.toList()
    private val filteredList = mutableListOf<product>().apply { addAll(productList) }

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = filteredList[position]
        holder.tvTitle.text = item.price.toReal()

        holder.tvBadge.text = item.quantity.toString()
        holder.tvBadge.visibility = if (item.quantity > 0) View.VISIBLE else View.GONE

        val isSelected = holder.adapterPosition == selectedPosition
        holder.btnPlus.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.btnMinus.visibility = if (isSelected) View.VISIBLE else View.GONE

        if (item.photo.isEmpty()) {
            holder.tvName.text = item.name
            holder.tvName.visibility = View.VISIBLE
            holder.ivProduct.visibility = View.GONE

            holder.tvTitle.text = if (isSelected) item.name else item.price.toReal()
            holder.tvName.visibility = if (isSelected) View.GONE else View.VISIBLE

        } else {
            holder.ivProduct.setImageFromBase64(item.photo)
            holder.tvName.visibility = View.GONE
            holder.ivProduct.visibility = View.VISIBLE
            holder.ivProduct.alpha = if (isSelected) 0.5f else 1f
        }

        holder.itemView.setOnClickListener {
            item.quantity++
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            if (previousPosition != selectedPosition) {
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }

            notifyTotalChanged()
        }

        holder.btnPlus.setOnClickListener {
            item.quantity++
            notifyItemChanged(holder.adapterPosition)
            notifyTotalChanged()
        }

        holder.btnMinus.setOnClickListener {
            if (item.quantity == 0) item.quantity = 0 else item.quantity--
            notifyItemChanged(holder.adapterPosition)
            notifyTotalChanged()
        }
    }

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(originalList)
        } else {
            val lowercaseQuery = query.lowercase()
            filteredList.addAll(originalList.filter {
                it.name.lowercase().contains(lowercaseQuery)
            })
        }
        notifyDataSetChanged()
        notifyTotalChanged()
    }

    private fun notifyTotalChanged() {
        val total = filteredList.sumOf { it.price * it.quantity }
        onTotalChanged(total)
    }

    fun getTotal(): Double {
        return filteredList.sumOf { it.price * it.quantity }
    }
}