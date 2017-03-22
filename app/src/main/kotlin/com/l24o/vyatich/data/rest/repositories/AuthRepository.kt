package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.rest.datasource.AuthDataSource
import okhttp3.ResponseBody
import rx.Observable


class AuthRepository(private val authDataSource: AuthDataSource) : Repository() {

    fun authenticate(user_id: String): Observable<ResponseBody> {
        return authDataSource
                .authenticate(user_id)
                .compose(this.applySchedulers<ResponseBody>())
    }
}