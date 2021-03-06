package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.rest.datasource.TaskDataSource
import com.l24o.vyatich.data.rest.models.*
import okhttp3.ResponseBody
import rx.Observable


class TaskRepository(private val taskDataSource: TaskDataSource) : Repository() {

    fun getTaskById(taskId: String): Observable<Task> {
        return taskDataSource
                .getTaskById(taskId)
                .compose(this.applySchedulers<Task>())
    }

    fun getTasks(): Observable<List<Task>> {
        return taskDataSource
                .getTasks()
                .compose(this.applySchedulers<List<Task>>())
    }

    fun getTaskTypes(): Observable<List<TaskType>> {
        return taskDataSource
                .getTaskTypes()
                .compose(this.applySchedulers<List<TaskType>>())
    }

    fun getTypeAndProductsAndExp(): Observable<TypeAndProductsAndExp> {
        return Observable.zip(taskDataSource.getTaskTypes(), taskDataSource.getExpeditions(), taskDataSource.getProducts(), {
            types, exp, products ->
            TypeAndProductsAndExp(types, products, exp)
        })
                .compose(this.applySchedulers<TypeAndProductsAndExp>())
    }

    fun getAllData(userId: String): Observable<AllData> {
        return Observable.zip(taskDataSource.getTasksByUserId(userId), taskDataSource.getTaskTypes(),
                taskDataSource.getExpeditions(), taskDataSource.getProducts(), {
            tasks, types, exp, products ->
            AllData(tasks, types, products, exp)
        })
                .compose(this.applySchedulers<AllData>())
    }

    fun getExpeditions(): Observable<List<Expedition>> {
        return taskDataSource
                .getExpeditions()
                .compose(this.applySchedulers<List<Expedition>>())
    }

    fun getProducts(): Observable<List<Product>> {
        return taskDataSource
                .getProducts()
                .compose(this.applySchedulers<List<Product>>())
    }

    fun startTask(taskId: String, typeName: String, ident: String): Observable<ResponseBody> {
        return taskDataSource
                .startTask(taskId, typeName, ident)
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun cancelTask(taskId: String, typeName: String, ident: String): Observable<ResponseBody> {
        return taskDataSource
                .cancelTask(taskId, typeName, ident)
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun endStackerTask(taskId: String, typeName: String, ident: String): Observable<ResponseBody> {
        return taskDataSource
                .endStackerTask(taskId, typeName, ident)
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun endCompositorTask(taskId: String, typeName: String, numberPallets: String): Observable<ResponseBody> {
        return taskDataSource
                .endCompositorTask(taskId, typeName, numberPallets)
                .compose(this.applySchedulers<ResponseBody>())
    }
}