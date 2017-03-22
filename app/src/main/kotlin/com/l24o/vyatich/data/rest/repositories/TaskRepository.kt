package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.rest.datasource.TaskDataSource
import com.l24o.vyatich.data.rest.models.*
import rx.Observable


class TaskRepository(private val taskDataSource: TaskDataSource) : Repository() {

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

    fun startTask(taskId: String): Observable<Task> {
        return taskDataSource
                .startTask(taskId)
                .compose(this.applySchedulers<Task>())
    }

    fun cancelTask(taskId: String): Observable<Task> {
        return taskDataSource
                .cancelTask(taskId)
                .compose(this.applySchedulers<Task>())
    }

    fun endTask(taskId: String): Observable<Task> {
        return taskDataSource
                .endTask(taskId)
                .compose(this.applySchedulers<Task>())
    }
}