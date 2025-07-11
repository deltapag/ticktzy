package br.com.sttsoft.ticktzy.presentation.sale.components

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.sttsoft.ticktzy.R


class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val tvBadge: TextView = itemView.findViewById(R.id.tv_badge)
    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val ivProduct: ImageView = itemView.findViewById(R.id.iv_product)
    val btnMinus: ImageView = itemView.findViewById(R.id.btn_minus)
    val btnPlus: ImageView = itemView.findViewById(R.id.btn_plus)
    val tvName: TextView = itemView.findViewById(R.id.tv_name)
}