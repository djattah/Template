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
    fun startTask(@Field("document_id") document_id: Int): Observable<ResponseBody>

    @GET("task/cancelTask")
    fun cancelTask(@Field("document_id") document_id: Int): Observable<ResponseBody>

    @GET("task/endTask")
    fun endTask(@Field("document_id") document_id: Int): Observable<ResponseBody>



    // prev
    //@GET("task/getTasks")
    //fun getTasks(): Observable<List<Task>>

    @GET("task/getTaskTypes")
    fun getTaskTypes(): Observable<List<TaskType>>

    @GET("task/getExpeditions")
    fun getExpeditions(): Observable<List<Expedition>>

    @GET("task/getProducts")
    fun getProducts(): Observable<List<Product>>

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