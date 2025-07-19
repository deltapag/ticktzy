package br.com.sttsoft.ticktzy.repository.remote.response

import br.com.sttsoft.ticktzy.repository.local.product

data class ProductsResponse(
    val results: List<product>
)