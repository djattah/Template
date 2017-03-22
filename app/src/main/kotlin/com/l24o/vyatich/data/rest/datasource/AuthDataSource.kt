package com.l24o.vyatich.data.rest.datasource

import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable


interface AuthDataSource {

    // new
    @GET("login/login")
    fun authenticate(@Field("user_id") user_id: String): Observable<ResponseBody>

    @GET("login/logout")
    fun logout(): Observable<ResponseBody>
}