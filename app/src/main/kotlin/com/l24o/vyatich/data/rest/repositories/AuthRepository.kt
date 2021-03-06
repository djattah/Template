package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.rest.datasource.AuthDataSource
import okhttp3.ResponseBody
import rx.Observable


class AuthRepository(private val authDataSource: AuthDataSource) : Repository() {

    fun authenticate(login: String, password: String): Observable<ResponseBody> {
        return authDataSource
                .authenticate(
                        mapOf(
                                "phone" to login,
                                "password" to password
                        )
                )
                .compose(this.applySchedulers<ResponseBody>())
    }
}