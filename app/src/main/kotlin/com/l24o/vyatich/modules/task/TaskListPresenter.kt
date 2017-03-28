package com.l24o.vyatich.modules.task

import com.google.gson.Gson
import com.l24o.vyatich.Constants
import com.l24o.vyatich.common.mvp.RxPresenter
import com.l24o.vyatich.data.realm.models.RealmExpedition
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.realm.models.RealmTaskType
import com.l24o.vyatich.data.rest.VyatichInterceptor
import com.l24o.vyatich.data.rest.datasource.TaskDataSource
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.endTask
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isDone
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isEndTask
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isNew
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isProgress
import com.l24o.vyatich.data.rest.repositories.RealmRepository
import com.l24o.vyatich.data.rest.repositories.TaskRepository
import com.l24o.vyatich.extensions.parsedMessage
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Alexander Popov on 11/01/2017.
 */
class TaskListPresenter(view: ITaskListView) : RxPresenter<ITaskListView>(view), ITaskListPresenter {

    var taskRepo: TaskRepository
    var realmRepo: RealmRepository

    override var showNewTasks: Boolean = true
    override var showAllTasks: Boolean = false
    var selectedType: String? = null
    var selectedExp: String? = null

    var mTypes: List<RealmTaskType>? = null;
    var mExps: List<RealmExpedition>? = null;

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

        // грузим типы задач и экспедиции
        // чтобы делать фильтр по ним
        val user = realmRepo.fetchUser()
        taskRepo.getAllData(user.login)
                .subscribe({
                    allData ->
                    realmRepo.clearAllTaskData()
                    realmRepo.saveTasks(allData.tasks)
                    realmRepo.saveExpeditions(allData.exps, view)
                    realmRepo.saveTaskTypes(allData.types, view)
                    realmRepo.saveProducts(allData.products)
                }, {
                    error ->
                    view.showMessage(error.parsedMessage())
                })
    }

    override fun onViewDetached() {
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
        realmRepo.fetchExp()
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

        fetchData()
        //subscriptions += realmRep.fetchTasks(showNewTasks, showAllTasks, selectedType, selectedExp)
    }

    override fun onTypeWrapperClick() {
        realmRepo.fetchTaskTypes()
                .subscribe({
                    types ->
                    view?.showTypes(types.map { it.name }, selectedType)
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onLogoutClick() {
        view?.navigateToLogin()
    }

    override fun onUpdateClick() {
        val user = realmRepo.fetchUser()
        taskRepo.getAllData(user.login)
                .subscribe({
                    allData ->
                    realmRepo.clearAllTaskData()
                    realmRepo.saveTasks(allData.tasks)
                    realmRepo.saveExpeditions(allData.exps, view)
                    realmRepo.saveTaskTypes(allData.types, view)
                    realmRepo.saveProducts(allData.products)
                    fetchData()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onViewAttached() {
        super.onViewAttached()
        fetchData()
    }

    fun fetchData() {
        view?.setLoadingVisible(true)
        realmRepo.fetchTasks()
                .subscribe({
                    tasks ->
                    view?.setLoadingVisible(false)
                    view?.showData(filteringTasks(tasks))
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                })

        realmRepo.fetchTaskTypes()
                .subscribe({
                    types ->
                    mTypes = types
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
        realmRepo.fetchExp()
                .subscribe({
                    exps ->
                    mExps = exps
                }, {
                    error ->
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

    private fun filteringTasks(tasks: List<RealmTask>): List<RealmTask> {
        if (showNewTasks) {
            var filterTasks = arrayListOf<RealmTask>()
            for (task in tasks) {
                if (isNew(task) && !isDone(task))
                    filterTasks.add(filteringTypeAndExps(task) ?: continue)
            }

            return filterTasks
        }

        if (showAllTasks) {
            var filterTasks = arrayListOf<RealmTask>()
            for (task in tasks) {
                if (isProgress(task) && !isDone(task))
                    filterTasks.add(filteringTypeAndExps(task) ?: continue)
            }

            return filterTasks
        }

        // filter isDone tasks
        var filterTasks = arrayListOf<RealmTask>()
        for (task in tasks) {
            if (!isDone(task))
                filterTasks.add(filteringTypeAndExps(task) ?: continue)
        }
        return filterTasks
    }

    private fun filteringTypeAndExps(task: RealmTask): RealmTask? {
        if (selectedType != null && mTypes != null) {
            var isType = false
            for (type in mTypes!!) {
                if (selectedType == type.name && task.typeId == type.id)
                    isType = true
            }
            if (!isType) return null
        }

        if (selectedExp != null && mExps != null) {
            var isEps = false
            for (exp in mExps!!) {
                if (selectedExp == exp.name && task.expeditionId == exp.id)
                    isEps = true
            }
            if (!isEps) return null
        }

        return task
    }

}