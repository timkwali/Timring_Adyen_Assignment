package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.Coin
import org.junit.Assert.*
import org.junit.Test

class CashRegisterTest {
    private val insufficientChange = CashRegister.TransactionException("There's insufficient change in the cash register!")
    private val insufficientAmount = CashRegister.TransactionException("Amount paid is less than price of item.")
    private val insufficientBills = CashRegister.TransactionException("There are insufficient bills in register!")

    @Test
    fun testTransaction_sufficientChange_returnChange() {
        val changeInRegister = Change()
        changeInRegister.add(Bill.TWENTY_EURO, 5)
        changeInRegister.add(Bill.TEN_EURO, 10)
        changeInRegister.add(Bill.FIVE_EURO, 5)
        val cashRegister = CashRegister(changeInRegister)

        val amountPaid = Change()
        amountPaid.add(Bill.ONE_HUNDRED_EURO, 1)
        amountPaid.add(Bill.TEN_EURO, 2)

        val transaction = cashRegister.performTransaction(5_00, amountPaid)
        val changeToCustomer = Change()
        changeToCustomer.add(Bill.TWENTY_EURO, 5)
        changeToCustomer.add(Bill.TEN_EURO, 1)
        changeToCustomer.add(Bill.FIVE_EURO, 1)

        assertEquals(changeToCustomer, transaction)
    }

    @Test
    fun testTransaction_insufficientChange_throwException() {
        val changeInRegister = Change()
        changeInRegister.add(Bill.TWENTY_EURO, 5)
        val cashRegister = CashRegister(changeInRegister)

        val amountPaid = Change()
        amountPaid.add(Bill.ONE_HUNDRED_EURO, 1)
        amountPaid.add(Bill.TEN_EURO, 2)

        try {
            cashRegister.performTransaction(5_00, amountPaid)
        } catch (exception: CashRegister.TransactionException) {
            assert(exception.message == insufficientChange.message)
        }
    }

    @Test
    fun testTransaction_insufficientAmount_throwException() {
        val changeInRegister = Change()
        changeInRegister.add(Bill.TWENTY_EURO, 5)
        changeInRegister.add(Bill.TEN_EURO, 10)
        changeInRegister.add(Bill.FIVE_EURO, 5)
        val cashRegister = CashRegister(changeInRegister)

        val amountPaid = Change()
        amountPaid.add(Coin.FIFTY_CENT, 1)

        try {
            cashRegister.performTransaction(5_00, amountPaid)
        } catch (exception: CashRegister.TransactionException) {
            assert(exception.message == insufficientAmount.message)
        }
    }

    @Test
    fun testTransaction_insufficientBills_throwException() {
        val changeInRegister = Change()
        changeInRegister.add(Bill.TWENTY_EURO, 5)
        changeInRegister.add(Bill.TEN_EURO, 10)
        val cashRegister = CashRegister(changeInRegister)

        val amountPaid = Change()
        amountPaid.add(Bill.ONE_HUNDRED_EURO, 1)
        amountPaid.add(Bill.TEN_EURO, 2)

        try {
            cashRegister.performTransaction(5_00, amountPaid)
        } catch (exception: CashRegister.TransactionException) {
            assert(exception.message == insufficientBills.message)
        }
    }
}
