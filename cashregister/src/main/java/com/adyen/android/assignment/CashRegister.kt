package com.adyen.android.assignment

import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.MonetaryElement

/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param change The change that the CashRegister is holding.
 */
class CashRegister(private val change: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */

    private var denominations = change.getElements().toList().asReversed();
    var denominationsCount = change.getElements().size

    fun performTransaction(price: Long, amountPaid: Change): Change {
        val amountPaidTotal = amountPaid.total
        val balance = amountPaidTotal - price

        when {
            balance == 0L -> return Change.none()
            balance < 0 -> throw TransactionException("Amount paid is less than price of item.")
            balance > change.total -> throw TransactionException("There's insufficient change in the cash register!")
        }

        var remainingBalance: Long = balance
        val denominationsToRemove: MutableList<Long> = arrayListOf()
        for(index in 0 until denominationsCount) {
            denominationsToRemove.add(index, 0)
        }
        var biggestDenominationIndex = -1
        var counter = 0

        while (counter < denominationsCount) {
            val denominations = change.getElements().toList().reversed()
            val currentDenomination = denominations[counter]

            if (biggestDenominationIndex == counter) {
                var denominationsQty = denominationsToRemove[counter]
                if (denominationsQty == 0L) {
                    biggestDenominationIndex = -1
                } else {
                    denominationsQty -= 1
                    remainingBalance += currentDenomination.minorValue * 1
                }
                denominationsToRemove[counter] = denominationsQty

            } else {
                var denominationAmount = remainingBalance / currentDenomination.minorValue
                if (denominationAmount > change.getCount(currentDenomination)) {
                    denominationAmount = 0
                }
                if (denominationAmount > 0) {
                    if (biggestDenominationIndex == -1) {
                        biggestDenominationIndex = counter
                    }
                    remainingBalance -= (currentDenomination.minorValue * denominationAmount)
                }
                denominationsToRemove[counter] = denominationAmount
            }

            if (counter == denominations.size -1) {
                if (remainingBalance != 0L && biggestDenominationIndex != -1 && biggestDenominationIndex != counter) {
                    counter = biggestDenominationIndex -1
                }
            }
            counter++
        }

        if (denominationsToRemove.sum() == 0L) {
            throw TransactionException("There are insufficient bills in register!")
        }
        removeDenominations(denominationsToRemove)
        return getChange(denominations, denominationsToRemove)
    }

    private fun removeDenominations(denominationsToRemove: List<Long>) {
        if (denominationsToRemove.size != denominations.size) {
            throw TransactionException("Invalid set of denomination quantities to remove.")
        }
        for ((index, value) in denominationsToRemove.withIndex()) {
            val denomination: MonetaryElement = denominations.elementAt(index)
            if (change.getCount(denomination) < value) {
                throw TransactionException("There are not enough \$${denomination.minorValue} bills to remove")
            }
            change.remove(denomination, value.toInt())
        }
    }

    private fun getChange(denominations: List<MonetaryElement>, moniesToRemove: MutableList<Long>): Change{
        val change = Change()
        for (index in denominations.indices) {
            change.add(denominations[index], moniesToRemove[index].toInt())
        }
        return change
    }

    class TransactionException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
