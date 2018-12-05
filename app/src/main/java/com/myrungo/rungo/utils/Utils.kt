package com.myrungo.rungo.utils

import android.content.Context
import android.support.v7.app.AlertDialog

fun Context.showAlertDialog(
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onClickedPositiveButton: () -> Unit = {},
    onClickedNegativeButton: () -> Unit = {},
    title: String? = null
) {
    AlertDialog.Builder(this).apply {
        if (title != null)
            setTitle(title)

        setMessage(message)

        setNegativeButton(negativeButtonText) { _, _ -> onClickedNegativeButton() }

        setPositiveButton(positiveButtonText) { _, _ -> onClickedPositiveButton() }
    }.create()!!.show()
}