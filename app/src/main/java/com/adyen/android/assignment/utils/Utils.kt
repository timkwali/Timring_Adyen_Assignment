package com.adyen.android.assignment.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog

object Utils {
    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun showAlertDialog(
        activity: Activity,
        title: String,
        message: String,
        dialogHelper: DialogHelper
    ) {
        try {
            val builder: AlertDialog.Builder? = if (activity.parent != null) AlertDialog.Builder(activity.parent)
            else AlertDialog.Builder(activity)
            builder?.setCancelable(false)
            builder?.setTitle(title)
            builder?.setMessage(message)
            builder?.setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    Runtime.getRuntime().gc()
                    dialogHelper.onOkClick()
                })
            val msg = builder?.create()
            msg?.show()
        } catch (ex: Exception) {
            Log.d("dialogError", ex.message.toString())
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }
}