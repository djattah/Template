package com.l24o.vyatich.modules.signin

import com.l24o.vyatich.common.mvp.IPresenter
import com.l24o.vyatich.common.mvp.IView


interface ISignInView : IView {
    fun setLoadingVisible(isVisible: Boolean)
    fun navigateToTasks()
    fun saveLoginData(login: String, password: String)
}

interface ISignInPresenter : IPresenter<ISignInView> {
    fun onSignInClick(login: String, password: String)
}

