package com.l24o.vyatich.modules.signin

import android.content.Context
import android.os.Bundle
import android.view.View
import com.l24o.vyatich.R
import com.l24o.vyatich.common.inDebugMode
import com.l24o.vyatich.common.mvp.MvpActivity
import com.l24o.vyatich.extensions.snackBar
import com.l24o.vyatich.modules.task.TaskListActivity
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import android.content.SharedPreferences
import android.content.Context.MODE_PRIVATE
import com.pixplicity.easyprefs.library.Prefs.getPreferences



class SignInActivity : MvpActivity(), ISignInView {

    var presenter: ISignInPresenter = SignInPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        initViews()
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }


    override fun showMessage(message: String) {
        snackBar(message)
    }

    override fun showMessage(messageResId: Int) {
        snackBar(messageResId)
    }

    override fun navigateToTasks() {
        startActivity<TaskListActivity>()
        //finish()
    }

    override fun setLoadingVisible(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        emailEditText.enabled = !isVisible
        forgotPasswordTextView.enabled = !isVisible
        loginButton.enabled = !isVisible
    }

    override fun saveLoginData(login: String) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.saving_user_login), login)
        editor.apply()
    }

    private fun initViews() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val defLogin = sharedPref.getString(getString(R.string.saving_user_login), "")
        emailEditText.setText(defLogin)

        loginButton.onClick {
            presenter.onSignInClick(emailEditText.text.toString())
        }
    }
}

