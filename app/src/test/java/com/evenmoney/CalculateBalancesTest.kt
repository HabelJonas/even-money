package com.evenmoney

import org.junit.Test

import org.junit.Assert.*
import com.evenmoney.models.Expense
import com.evenmoney.screens.calculateBalances
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CalculateBalancesTest {

    @Test
    fun calculateBalances_emptyExpensesList_returnsEmptyMap() = runBlocking {
        // Given
        val expenses = emptyList<Expense>()

        // When
        val balances = calculateBalances(expenses)

        // Then
        assertTrue(balances.isEmpty())
    }

    @Test
    fun calculateBalances_singleExpenseTwoPeople_returnsCorrectBalances() = runBlocking {
        // Given
        val expenses = listOf(
            Expense(
                title = "Dinner",
                paidBy = "Alice",
                amount = 100.0,
                distributions = mapOf("Alice" to 50.0, "Bob" to 50.0)
            )
        )

        // When
        val balances = calculateBalances(expenses)

        // Then
        assertEquals(2, balances.size)
        assertEquals(50.0, balances["Alice"])  // 100 - 50 = 50
        assertEquals(-50.0, balances["Bob"])   // 0 - 50 = -50
    }

    @Test
    fun calculateBalances_multipleExpensesSamePayer_returnsCorrectBalances() = runBlocking {
        // Given
        val expenses = listOf(
            Expense(
                title = "Dinner",
                paidBy = "Alice",
                amount = 100.0,
                distributions = mapOf("Alice" to 50.0, "Bob" to 50.0)
            ),
            Expense(
                title = "Movie",
                paidBy = "Alice",
                amount = 60.0,
                distributions = mapOf("Alice" to 30.0, "Bob" to 30.0)
            )
        )

        // When
        val balances = calculateBalances(expenses)

        // Then
        assertEquals(2, balances.size)
        assertEquals(80.0, balances["Alice"])  // (100 - 50) + (60 - 30) = 80
        assertEquals(-80.0, balances["Bob"])   // (0 - 50) + (0 - 30) = -80
    }

    @Test
    fun calculateBalances_multipleExpensesDifferentPayers_returnsCorrectBalances() = runBlocking {
        // Given
        val expenses = listOf(
            Expense(
                title = "Dinner",
                paidBy = "Alice",
                amount = 100.0,
                distributions = mapOf("Alice" to 50.0, "Bob" to 50.0)
            ),
            Expense(
                title = "Movie",
                paidBy = "Bob",
                amount = 60.0,
                distributions = mapOf("Alice" to 30.0, "Bob" to 30.0)
            )
        )

        // When
        val balances = calculateBalances(expenses)

        // Then
        assertEquals(2, balances.size)
        assertEquals(20.0, balances["Alice"])  // (100 - 50) + (0 - 30) = 20
        assertEquals(-20.0, balances["Bob"])   // (0 - 50) + (60 - 30) = -20
    }

    @Test
    fun calculateBalances_complexScenario_returnsCorrectBalances() = runBlocking {
        // Given
        val expenses = listOf(
            Expense(
                title = "Dinner",
                paidBy = "Alice",
                amount = 100.0,
                distributions = mapOf("Alice" to 25.0, "Bob" to 25.0, "Charlie" to 25.0, "David" to 25.0)
            ),
            Expense(
                title = "Movie",
                paidBy = "Bob",
                amount = 80.0,
                distributions = mapOf("Alice" to 20.0, "Bob" to 20.0, "Charlie" to 20.0, "David" to 20.0)
            ),
            Expense(
                title = "Taxi",
                paidBy = "Charlie",
                amount = 40.0,
                distributions = mapOf("Alice" to 10.0, "Bob" to 10.0, "Charlie" to 10.0, "David" to 10.0)
            )
        )

        // When
        val balances = calculateBalances(expenses)

        // Then
        assertEquals(4, balances.size)
        assertEquals(45.0, balances["Alice"])    // (100 - 25) + (0 - 20) + (0 - 10) = 45
        assertEquals(25.0, balances["Bob"])      // (0 - 25) + (80 - 20) + (0 - 10) = 25
        assertEquals(-15.0, balances["Charlie"]) // (0 - 25) + (0 - 20) + (40 - 10) = -15
        assertEquals(-55.0, balances["David"])   // (0 - 25) + (0 - 20) + (0 - 10) = -55
    }
}