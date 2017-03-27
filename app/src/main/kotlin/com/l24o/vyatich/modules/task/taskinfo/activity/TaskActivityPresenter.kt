package com.l24o.vyatich.modules.task.taskinfo.activity

import com.google.gson.Gson
import com.l24o.vyatich.Constants
import com.l24o.vyatich.common.VyatichConnectionManager
import com.l24o.vyatich.common.mvp.RxPresenter
import com.l24o.vyatich.data.realm.models.RealmProduct
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.rest.VyatichInterceptor
import com.l24o.vyatich.data.rest.datasource.TaskDataSource
import com.l24o.vyatich.data.rest.models.Product
import com.l24o.vyatich.data.rest.models.Task
import com.l24o.vyatich.data.rest.models.TaskType
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
        /*ssubscriptions += taskRepo.startTask(task.id)
                .subscribe({
                    result ->
                    view?.navigateToTasks()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })*/
    }

    override fun finishTask() {
        /*subscriptions += taskRepo.endTask(task.id)
                .subscribe({
                    result ->
                    view?.navigateToTasks()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })*/
    }

    override fun cancelTask() {
        /*subscriptions += taskRepo.cancelTask(task.id)
                .subscribe({
                    result ->
                    view?.navigateToTasks()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })*/
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

                    if (task.products != null)
                        realmRepo.fetchProducts()
                                .subscribe({
                                    var products = arrayListOf<RealmProduct>()
                                    for (product in it)
                                        for (take in task.products) {
                                            if (take.productId == product.id)
                                                products.add(product)
                                        }
                                    view?.fillTaskProductInfo(products)
                                }, {
                                    error ->
                                    view?.showMessage(error.parsedMessage())
                                })
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onViewDetached() {
        super.onViewDetached()
    }

}