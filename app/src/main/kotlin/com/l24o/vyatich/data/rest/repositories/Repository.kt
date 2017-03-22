package com.l24o.vyatich.data.rest.repositories

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

abstract class Repository {
    protected fun <T> applySchedulers(): Observable.Transformer<T, T> {
        return Observable.Transformer<T, T> { observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }
}

