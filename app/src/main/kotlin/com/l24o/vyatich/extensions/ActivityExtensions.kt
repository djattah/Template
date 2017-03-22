package com.l24o.vyatich.extensions

import android.app.Activity
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.l24o.vyatich.R
import org.jetbrains.anko.contentView
import org.jetbrains.anko.textColor

inline fun Activity.materialDialog(setupBlock: (MaterialDialog.Builder.() -> (MaterialDialog.Builder))): MaterialDialog.Builder {
    return setupBlock.invoke(MaterialDialog.Builder(this))
}

fun Activity.snackBar(text: String,
                      duration: Int = Snackbar.LENGTH_INDEFINITE,
                      actionText: String = getString(R.string.ok),
                      action: ((Snackbar) -> Unit)? = { it.dismiss() }) {

    contentView?.let {
        val snackBar = Snackbar.make(it, text, duration)
        val textView = snackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        if (action != null) {
            snackBar.setAction(actionText) { action.invoke(snackBar) }
        }
        snackBar.show()
    }
}

fun Activity.snackBar(@StringRes text: Int,
                      duration: Int = Snackbar.LENGTH_INDEFINITE,
                      actionText: String = getString(R.string.ok),
                      action: ((Snackbar) -> Unit)? = { it.dismiss() }) {

    contentView?.let {
        val snackBar = Snackbar.make(it, text, duration)
        val textView = snackBar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        if (action != null) {
            snackBar.setAction(actionText) { action.invoke(snackBar) }
        }
        snackBar.show()
    }
}