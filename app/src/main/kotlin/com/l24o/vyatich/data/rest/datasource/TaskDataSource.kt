package com.l24o.vyatich.data.rest.datasource

import com.l24o.vyatich.data.rest.models.Expedition
import com.l24o.vyatich.data.rest.models.Product
import com.l24o.vyatich.data.rest.models.Task
import com.l24o.vyatich.data.rest.models.TaskType
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable


interface TaskDataSource {

    // new
    @GET("task/getTasks")
    fun getTasks(): Observable<List<Task>>

    @GET("task/startTask")
    fun startTask(@Field("document_id") document_id: Long): Observable<ResponseBody>

    @GET("task/cancelTask")
    fun cancelTask(@Field("document_id") document_id: Long): Observable<ResponseBody>

    @GET("task/endTask")
    fun endTask(@Field("document_id") document_id: Long): Observable<ResponseBody>

}