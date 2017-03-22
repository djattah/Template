package com.l24o.vyatich.modules.task.taskinfo.activity

import com.google.gson.Gson
import com.l24o.vyatich.Constants
import com.l24o.vyatich.common.VyatichConnectionManager
import com.l24o.vyatich.common.mvp.RxPresenter
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.rest.VyatichInterceptor
import com.l24o.vyatich.data.rest.datasource.TaskDataSource
import com.l24o.vyatich.data.rest.repositories.RealmRepository
import com.l24o.vyatich.data.rest.repositories.TaskRepository
import com.l24o.vyatich.extensions.parsedMessage
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.lang.kotlin.plusAssign
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Alexander Popov on 17/01/2017.
 */
class TaskActivityPresenter(activityView: ITaskActivityView) : RxPresenter<ITaskActivityView>(activityView), ITaskActivityPresenter {

    var realmRep: RealmRepository = RealmRepository(Realm.getDefaultInstance())
    var taskRepo: TaskRepository
    var connectionManager: VyatichConnectionManager = VyatichConnectionManager(activityView.application())

    override lateinit var task: RealmTask
    override var taskId: String? = null

    init {
        val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(VyatichInterceptor())
                .build()
        val adapter = Retrofit.Builder()
                .baseUrl(Constants.API_MAIN_ENDPOINT_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
        taskRepo = TaskRepository(adapter.create(
                TaskDataSource::class.java))

    }

    override fun takeTask() {
        subscriptions += taskRepo.startTask(task.id)
                .flatMap { task ->
                    realmRep.updateTask(task)
                }
                .subscribe({
                    result ->
                    view?.navigateToTasks()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun finishTask() {
        if (connectionManager.isConnected()) {
            subscriptions += taskRepo.endTask(task.id)
                    .flatMap { task ->
                        realmRep.updateTask(task)
                    }
                    .subscribe({
                        result ->
                        view?.navigateToTasks()
                    }, {
                        error ->
                        view?.showMessage(error.parsedMessage())
                    })
        } else {
            task.endDate = Date()
            task.needSync = true
            subscriptions += realmRep.updateTask(task)
                    .subscribe({
                        result ->
                        view?.navigateToTasks()
                    }, {
                        error ->
                        view?.showMessage(error.parsedMessage())
                    })
        }
    }

    override fun cancelTask() {
        subscriptions += taskRepo.cancelTask(task.id)
                .flatMap { task ->
                    realmRep.updateTask(task)
                }
                .subscribe({
                    result ->
                    view?.navigateToTasks()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onViewAttached() {
        super.onViewAttached()
        subscriptions += realmRep.fetchTaskById(taskId!!)
                .subscribe({
                    task ->
                    this.task = task
                    view?.fillInfo(task)
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onViewDetached() {
        realmRep.close()
        super.onViewDetached()
    }

}