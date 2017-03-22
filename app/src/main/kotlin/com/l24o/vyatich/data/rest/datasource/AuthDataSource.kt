package com.l24o.vyatich.data.rest.datasource

import okhttp3.ResponseBody
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable


interface AuthDataSource {

    @FormUrlEncoded
    @POST("login/login")
    fun authenticate(@FieldMap params: Map<String, String>): Observable<ResponseBody>
}