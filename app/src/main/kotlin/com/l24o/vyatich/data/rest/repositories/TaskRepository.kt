package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.rest.datasource.TaskDataSource
import com.l24o.vyatich.data.rest.models.*
import rx.Observable


class TaskRepository(private val taskDataSource: TaskDataSource) : Repository() {

    // new
    fun getTasks(): Observable<List<Task>> {
        return taskDataSource
                .getTasks()
                .compose(this.applySchedulers<List<Task>>())
    }

    fun startTask(documentId: Long): Boolean {
        // здесь надо поглядеть, что вернул ResponseBody- ок или error
        taskDataSource.startTask(documentId)
        return true;
    }

    fun cancelTask(documentId: Long): Boolean {
        // здесь надо поглядеть, что вернул ResponseBody- ок или error
        taskDataSource.cancelTask(documentId)
        return true;
    }

    fun endTask(documentId: Long): Boolean {
        // здесь надо поглядеть, что вернул ResponseBody- ок или error
        taskDataSource.endTask(documentId)
        return true;
    }
}