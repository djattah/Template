package com.l24o.vyatich.modules.signin

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
        finish()
    }

    override fun setLoadingVisible(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        emailEditText.enabled = !isVisible
        passwordEditText.enabled = !isVisible
        forgotPasswordTextView.enabled = !isVisible
        loginButton.enabled = !isVisible
    }

    private fun initViews() {
        inDebugMode {
            emailEditText.setText("7777")
            passwordEditText.setText("q1w2e3r4")
        }

        loginButton.onClick {
            presenter.onSignInClick(emailEditText.text.toString(), passwordEditText.text.toString())
        }
    }
}

