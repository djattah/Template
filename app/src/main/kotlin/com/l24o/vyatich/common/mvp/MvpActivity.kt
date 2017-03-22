package com.l24o.vyatich.common.mvp

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.l24o.vyatich.R
import com.l24o.vyatich.VyatichApplication
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.toast

abstract class MvpActivity : AppCompatActivity(), IView {

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        beforeDestroy()
        progressDialog?.dismiss()
        super.onDestroy()
    }

    abstract fun beforeDestroy()

    override fun application(): VyatichApplication {
        return application as VyatichApplication
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun showMessage(messageResId: Int) {
        toast(messageResId)
    }

    override fun showProgressDialog(visibility: Boolean) {
        if (progressDialog == null) {
            progressDialog = progressDialog(R.string.pls_wait)
        }
        if (visibility) progressDialog?.show() else progressDialog?.dismiss()
    }


}
