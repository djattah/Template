package com.l24o.vyatich.common.mvp

import com.l24o.vyatich.VyatichApplication

interface IView {
    fun application(): VyatichApplication
    fun showProgressDialog(visibility: Boolean)
    fun showMessage(message: String)
    fun showMessage(messageResId: Int)
}

interface IPresenter<in V : IView> {
    fun takeView(view: V)
    fun onViewAttached()
    fun dropView()
    fun onViewDetached()
}

interface IListView<M> : IView {
    fun setLoadingVisible(isVisible: Boolean)
    fun showData(dataset: List<M>)
}

interface IListPresenter<M, V : IListView<*>> : IPresenter<V> {
    fun onSwipeToRefresh()
}