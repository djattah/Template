package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.rest.datasource.UserDataSource
import com.l24o.vyatich.data.rest.models.User
import rx.Observable


class UserRepository(private val userDataSource: UserDataSource) : Repository() {

    fun getUser(): Observable<User> {
        return userDataSource
                .getUser()
                .compose(this.applySchedulers<User>())
    }
}