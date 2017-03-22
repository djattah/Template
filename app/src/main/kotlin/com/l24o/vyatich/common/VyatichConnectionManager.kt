package com.l24o.vyatich.common

import android.content.Context
import android.net.ConnectivityManager

/**
 * @author Alexander Popov on 26/01/2017.
 */
class VyatichConnectionManager(private val context: Context) {

    fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return (activeNetwork?.isConnectedOrConnecting ?: false)
    }

}