package br.com.sttsoft.ticktzy.domain

import android.content.Context
import br.com.sttsoft.ticktzy.repository.local.product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ProductCacheUseCase(private val context: Context) {
    private val fileName = "produtos.json"
    private val gson = Gson()

    // Salva a lista de produtos como JSON
    fun salvarProdutos(produtos: List<product>) {
        val json = gson.toJson(produtos)
        File(context.filesDir, fileName).writeText(json)
    }

    // Lê e retorna a lista de produtos, ou lista vazia
    fun lerProdutos(): List<product> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()

        val json = file.readText()
        return gson.fromJson(json, object : TypeToken<List<product>>() {}.type)
    }

    // Opcional: apaga o cache
    fun limparCache() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) file.delete()
    }
}
