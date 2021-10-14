package com.adyen.android.assignment.utils

interface ClickListener<T> {
    fun onItemClick(item: T, position: Int)
}