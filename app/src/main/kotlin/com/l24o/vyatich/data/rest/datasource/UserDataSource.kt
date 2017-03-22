package com.l24o.vyatich.data.rest.datasource

import com.l24o.vyatich.data.rest.models.User
import retrofit2.http.GET
import rx.Observable

/**
 * @author Alexander Popov on 23/01/2017.
 */
interface UserDataSource {

    @GET("user/getUser")
    fun getUser(): Observable<User>
}