package com.l24o.vyatich.data.rest.datasource

import com.l24o.vyatich.data.rest.models.Expedition
import com.l24o.vyatich.data.rest.models.Product
import com.l24o.vyatich.data.rest.models.Task
import com.l24o.vyatich.data.rest.models.TaskType
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable


interface TaskDataSource {

    @GET("task/getTasks")
    fun getTasks(): Observable<List<Task>>

    @FormUrlEncoded
    @POST("task/getTasksByUserId")
    fun getTasksByUserId(@Field("userId") userId: String): Observable<List<Task>>

    @GET("task/getTaskTypes")
    fun getTaskTypes(): Observable<List<TaskType>>

    @GET("task/getExpeditions")
    fun getExpeditions(): Observable<List<Expedition>>

    @GET("task/getProducts")
    fun getProducts(): Observable<List<Product>>

    @FormUrlEncoded
    @POST("task/getTaskById")
    fun getTaskById(@Field("taskId") taskId: String): Observable<Task>

    @FormUrlEncoded
    @POST("task/startTask")
    fun startTask(@Field("taskId") taskId: String): Observable<Task>

    @FormUrlEncoded
    @POST("task/cancelTask")
    fun cancelTask(@Field("taskId") taskId: String): Observable<Task>

    @FormUrlEncoded
    @POST("task/endTask")
    fun endTask(@Field("taskId") taskId: String): Observable<Task>
}