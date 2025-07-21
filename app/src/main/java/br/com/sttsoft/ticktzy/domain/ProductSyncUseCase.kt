package br.com.sttsoft.ticktzy.domain

import br.com.sttsoft.ticktzy.repository.local.product

class ProductSyncUseCase(
    private val productCache: ProductCacheUseCase,
    private val getProducts: GetProductsUseCase
) {
    suspend fun sync(cnpj: String): List<product> {
        val response = getProducts.invoke(cnpj)
        val lista = response?.results ?: emptyList()
        productCache.salvarProdutos(lista)
        return lista
    }
}