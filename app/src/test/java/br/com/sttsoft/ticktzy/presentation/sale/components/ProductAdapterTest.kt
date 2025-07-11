package br.com.sttsoft.ticktzy.presentation.sale.components

import br.com.sttsoft.ticktzy.repository.local.product
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductAdapterTest {

    private fun createProduct(name: String, price: Double, quantity: Int = 1): product {
        return product(name = name, photo = "", price = price, quantity = quantity)
    }

    @Test
    fun filter_withEmptyQuery_returnsAllItemsAndNotifiesTotal() {
        val items = listOf(
            createProduct("Frango", 1.0),
            createProduct("Batata", 2.0)
        )
        var total = 0.0
        val adapter = ProductAdapter(items) { total = it }

        adapter.filter("")

        assertEquals(3.0, adapter.getTotal(), 0.001)
        assertEquals(3.0, total, 0.001)
    }

    @Test
    fun filter_withQuery_filtersItemsIgnoringCase() {
        val items = listOf(
            createProduct("Frango", 1.0),
            createProduct("Batata", 2.0),
            createProduct("Bebida", 3.0)
        )
        var total = 0.0
        val adapter = ProductAdapter(items) { total = it }

        adapter.filter("fr")

        assertEquals(1.0, adapter.getTotal(), 0.001)
        assertEquals(1.0, total, 0.001)
    }

    @Test
    fun filter_withNonMatchingQuery_returnsEmptyAndZeroTotal() {
        val items = listOf(
            createProduct("Frango", 1.0),
            createProduct("Batata", 2.0)
        )
        var total = 0.0
        val adapter = ProductAdapter(items) { total = it }

        adapter.filter("xyz")

        assertEquals(0.0, adapter.getTotal(), 0.001)
        assertEquals(0.0, total, 0.001)
    }

    @Test
    fun getTotal_reflectsItemQuantityChanges() {
        val items = listOf(
            createProduct("Frango", 1.0),
            createProduct("Batata", 2.0)
        )
        val adapter = ProductAdapter(items) {}

        // update quantities directly on items
        items[0].quantity = 3
        items[1].quantity = 1

        assertEquals(5.0, adapter.getTotal(), 0.001)
    }
}
