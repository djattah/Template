package com.l24o.vyatich.modules.task.taskinfo.activity

import com.google.gson.Gson
import com.l24o.vyatich.Constants
import com.l24o.vyatich.common.mvp.RxPresenter
import com.l24o.vyatich.data.realm.models.RealmProduct
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.rest.VyatichInterceptor
import com.l24o.vyatich.data.rest.datasource.TaskDataSource
import com.l24o.vyatich.data.rest.models.TaskUtils
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
import java.util.concurrent.TimeUnit

/**
 * @author Alexander Popov on 17/01/2017.
 */
class TaskActivityPresenter(activityView: ITaskActivityView) : RxPresenter<ITaskActivityView>(activityView), ITaskActivityPresenter {

    companion object {
        private val STACKERCPOMPOSITOR_TASK_TYPE_NAME = "StackerCompositor"
        private val STACKERSB_TASK_TYPE_NAME = "StackerSB"
        private val STACKERA_TASK_TYPE_NAME = "StackerA"
    }

    var taskRepo: TaskRepository
    var realmRepo: RealmRepository

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
        realmRepo = RealmRepository(Realm.getDefaultInstance())
    }

    override fun takeTask() {
        subscriptions += taskRepo.startTask(task.id, task.typeName, task.ident)
                .subscribe({
                    result ->
                    val user = realmRepo.fetchUser()
                    TaskUtils.startTask(task, user.login)
                    realmRepo.updateTask(task, view)
                    view?.navigateToTasks()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun finishTask() {
        /*subscriptions += taskRepo.endTask(task.id)
                .subscribe({
                    result ->
                    TaskUtils.endTask(task)
                    realmRepo.updateTask(task)
                    view?.navigateToTasks()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })*/
    }

    override fun cancelTask() {
        subscriptions += taskRepo.cancelTask(task.id, task.typeName, task.ident)
                .subscribe({
                    result ->
                    TaskUtils.cancelTask(task)
                    realmRepo.updateTask(task, view)
                    view?.navigateToTasks()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    /**
     * загружаем задачу, тип задачи и все продукты
     */
    override fun onViewAttached() {
        super.onViewAttached()

        realmRepo.fetchTaskById(taskId!!)
                .subscribe({
                    task ->
                    this.task = task
                    view?.fillTaskInfo(task)

                    if (!task.typeId.isNullOrEmpty())
                        realmRepo.fetchTaskTypes()
                                .subscribe({
                                    for (type in it)
                                        if (type.id == task.typeId)
                                            view?.fillTaskTypeInfo(type)
                                }, {
                                    error ->
                                    view?.showMessage(error.parsedMessage())
                                })

                    view?.fillTaskProductInfo(task.products)
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onViewDetached() {
        super.onViewDetached()
    }
}