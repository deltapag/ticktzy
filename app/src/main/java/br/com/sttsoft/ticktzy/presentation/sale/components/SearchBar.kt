package br.com.sttsoft.ticktzy.presentation.sale.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import br.com.sttsoft.ticktzy.R

class SearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val searchInput: EditText
    val searchIcon: ImageView

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.component_search_bar, this, true)

        searchInput = findViewById(R.id.search_input)
        searchIcon = findViewById(R.id.search_icon)
    }

    fun getQuery(): String {
        return searchInput.text.toString()
    }

    fun setOnSearchClickListener(onClick: () -> Unit) {
        searchIcon.setOnClickListener { onClick() }
    }

    fun addOnTextChangedListener(onTextChanged: (String) -> Unit) {
        searchInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }
}