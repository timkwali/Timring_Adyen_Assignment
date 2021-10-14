package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change

fun main() {
    val cashRegisterChange = Change()
    cashRegisterChange.add(Bill.TWENTY_EURO, 5)
    cashRegisterChange.add(Bill.TEN_EURO, 10)
    cashRegisterChange.add(Bill.FIVE_EURO, 5)
    val cashRegister = CashRegister(cashRegisterChange)

    val amountPaid = Change()
    amountPaid.add(Bill.ONE_HUNDRED_EURO, 1)
    amountPaid.add(Bill.TEN_EURO, 2)
    println(cashRegister.performTransaction(5_00, amountPaid))

//    val moniesToRemove: MutableList<Long> = arrayListOf()
////    repeat(5) { num -> moniesToRemove.add(num, 0) }
//    for(index in 0 until 5) {
//        moniesToRemove.add(index, 0)
//    }
//    println(moniesToRemove.toList())


}