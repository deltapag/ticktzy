package br.com.sttsoft.ticktzy.features.sale

import br.com.sttsoft.ticktzy.core.ui.UiState
import br.com.sttsoft.ticktzy.repository.local.product

data class SaleState(
    val loading: Boolean = false,
    val products: List<product> = emptyList(),
    val filtered: List<product> = emptyList(),
    val total: Double = 0.0,
    val query: String = "",
) : UiState
