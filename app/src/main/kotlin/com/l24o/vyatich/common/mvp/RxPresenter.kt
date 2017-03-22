package com.l24o.vyatich.common.mvp

import android.content.Context
import android.net.ConnectivityManager
import com.l24o.vyatich.VyatichApplication
import rx.Subscription
import rx.lang.kotlin.plusAssign
import rx.subscriptions.CompositeSubscription

abstract class RxPresenter<V : IView>(view: V) : BasePresenter<V>(view) {

    protected val subscriptions: CompositeSubscription

    init {
        subscriptions = CompositeSubscription()
    }

    override fun onViewDetached() {
        subscriptions.clear()
        super.onViewDetached()
    }

    protected fun registerSubscription(subscription: Subscription) {
        subscriptions += subscription
    }

    protected fun removeSubscription(subscription: Subscription) {
        subscriptions.remove(subscription)
    }

}