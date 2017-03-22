package com.l24o.vyatich.modules.task

import com.google.gson.Gson
import com.l24o.vyatich.Constants
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
import java.util.concurrent.TimeUnit

/**
 * @author Alexander Popov on 11/01/2017.
 */
class TaskListPresenter(view: ITaskListView) : RxPresenter<ITaskListView>(view), ITaskListPresenter {

    var realmRep: RealmRepository = RealmRepository(Realm.getDefaultInstance())
    lateinit var taskRepo: TaskRepository

    override var showNewTasks: Boolean = true
    override var showAllTasks: Boolean = false
    var selectedType: String? = null
    var selectedExp: String? = null

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

    override fun onViewDetached() {
        realmRep.close()
        super.onViewDetached()
    }

    override fun onTypeChosen(type: String?) {
        selectedType = type
        refreshList()
    }

    override fun onExpChosen(exp: String?) {
        selectedExp = exp
        refreshList()
    }

    override fun onExpedWrapperClick() {
        subscriptions += realmRep.fetchExp()
                .subscribe({
                    exps ->
                    view?.showExps(exps.map { it.name }, selectedExp)
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun refreshList() {
        view?.setLoadingVisible(true)
        subscriptions += realmRep.fetchTasks(showNewTasks, showAllTasks, selectedType, selectedExp)
                .subscribe({
                    tasks ->
                    view?.setLoadingVisible(false)
                    view?.showData(tasks)
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onTypeWrapperClick() {
        subscriptions += realmRep.fetchTaskTypes()
                .subscribe({
                    types ->
                    view?.showTypes(types.map { it.name }, selectedType)
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onLogoutClick() {
        subscriptions += realmRep.clearAll()
                .subscribe({
                    result ->
                    view?.navigateToLogin()
                }, {
                    error ->
                    view?.navigateToLogin()
                })
    }

    override fun onViewAttached() {
        super.onViewAttached()
        fetchData()
    }

    fun fetchData() {
        view?.setLoadingVisible(true)
        subscriptions += taskRepo.getTypeAndProductsAndExp()
                .flatMap {
                    result ->
                    realmRep.saveTaskTypesExpProducts(result.types, result.exps, result.products)
                }
                .flatMap {
                    result ->
                    taskRepo.getTasks()
                }
                .flatMap {
                    tasks ->
                    realmRep.saveTasks(tasks)
                }
                .flatMap({
                    result ->
                    realmRep.fetchTasks(showNewTasks, showAllTasks, selectedType, selectedExp)
                })
                .subscribe({
                    tasks ->
                    view?.setLoadingVisible(false)
                    view?.showData(tasks)
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onClick(task: RealmTask) {
        subscriptions.clear()
        view?.navigateToTask(task)
    }

    override fun onSwipeToRefresh() {
        fetchData()
    }

}