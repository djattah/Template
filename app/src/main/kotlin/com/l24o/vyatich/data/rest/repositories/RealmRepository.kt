package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.realm.models.*
import com.l24o.vyatich.data.rest.models.Expedition
import com.l24o.vyatich.data.rest.models.Product
import com.l24o.vyatich.data.rest.models.Task
import com.l24o.vyatich.data.rest.models.TaskType
import com.l24o.vyatich.modules.task.ITaskListView
import com.l24o.vyatich.modules.task.TaskListActivity
import io.realm.Realm
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

/**
 * @autor Gorodilov Nikita
 * @date 27.03.2017
 */
class RealmRepository(private val realm: Realm) : Repository() {

    fun close() {
        realm.close()
    }

    fun fetchTasks(): Observable<List<RealmTask>> {
        return realm
                .where(RealmTask::class.java)
                .findAll()
                .asObservable()
                .flatMap {
                    results ->
                    Observable.just(realm.copyFromRealm(results))
                }
    }

    fun fetchSyncTasks(): Observable<List<RealmTask>> {
        return realm
                .where(RealmTask::class.java)
                .equalTo("isNeedSync", true)
                .findAll()
                .asObservable()
                .flatMap {
                    results ->
                    Observable.just(realm.copyFromRealm(results))
                }
    }

    fun fetchTaskById(taskId: String): Observable<RealmTask> {
        return realm
                .where(RealmTask::class.java)
                .equalTo("id", taskId)
                .findFirst()
                .asObservable<RealmTask>()
                .flatMap {
                    results ->
                    Observable.just(realm.copyFromRealm(results))
                }
    }

    fun fetchProducts(): Observable<List<RealmProduct>> {
        return realm
                .where(RealmProduct::class.java)
                .findAll()
                .asObservable()
                .flatMap {
                    results ->
                    Observable.just(realm.copyFromRealm(results))
                }
    }

    fun fetchTaskTypes(): Observable<List<RealmTaskType>> {
        return realm
                .where(RealmTaskType::class.java)
                .findAll()
                .asObservable()
                .flatMap {
                    results ->
                    Observable.just(realm.copyFromRealm(results))
                }
    }

    fun fetchExp(): Observable<List<RealmExpedition>> {
        return realm
                .where(RealmExpedition::class.java)
                .findAll()
                .asObservable()
                .flatMap {
                    results ->
                    Observable.just(realm.copyFromRealm(results))
                }
    }

    fun fetchUser(): RealmUser {
        return realm
                .where(RealmUser::class.java)
                .findFirst()
    }

    fun fetchUsers(): List<RealmUser> {
        return realm
                .where(RealmUser::class.java)
                .findAll()
    }

    fun saveUser(user: RealmUser) {
        realm.executeTransactionAsync {
            it.copyToRealmOrUpdate(user)
        }
    }

    fun saveTasks(tasks: List<Task>) {
        realm.executeTransactionAsync{
            val list = tasks.map {
                task ->
                val products = task.products?.map {
                    productForTake ->
                    it.copyToRealm(RealmProductForTake(
                            productId = productForTake.productId,
                            count = productForTake.count
                    ))
                }
                val realmTask = RealmTask(
                        id = task.id,
                        description = task.description,
                        typeId = task.typeId,
                        expeditionId = task.expeditionId,
                        startDate = task.startDate,
                        endDate = task.endDate,
                        userId = task.userId,
                        isNeedSync = false
                )
                if (products != null) {
                    realmTask.products.addAll(products)
                }
                realmTask
            }
            it.copyToRealmOrUpdate(list)
        }
    }

    fun saveTaskTypes(taskTypes: List<TaskType>, view: ITaskListView?) {
        realm.executeTransactionAsync({
            it.copyToRealmOrUpdate(taskTypes.map {
                RealmTaskType(
                        id = it.id,
                        name = it.name,
                        code = it.code
                )
            })
        }, {
               error ->
            view?.showMessage("" + error.message)
        })
    }

    fun updateTask(task: RealmTask) {
        realm.executeTransactionAsync {
            it.copyToRealmOrUpdate(task)
        }
    }

    fun updateTask(task: Task) {
        realm.executeTransactionAsync {
            val products = task.products?.map {
                productForTake ->
                it.copyToRealm(RealmProductForTake(
                        productId = productForTake.productId,
                        count = productForTake.count
                ))
            }
            val realmTask = RealmTask(
                    id = task.id,
                    description = task.description,
                    typeId = task.typeId,
                    expeditionId = task.expeditionId,
                    startDate = task.startDate,
                    endDate = task.endDate,
                    userId = task.userId,
                    isNeedSync = false
            )
            if (products != null) {
                realmTask.products.addAll(products)
            }
            it.copyToRealmOrUpdate(realmTask)
        }
    }

    fun saveExpeditions(expeditions: List<Expedition>, view: ITaskListView?) {
        realm.executeTransactionAsync {
            it.copyToRealmOrUpdate(expeditions.map {
                RealmExpedition(
                        id = it.id,
                        name = it.name,
                        code = it.code
                )
            })
        }
    }

    fun saveProducts(products: List<Product>) {
        realm.executeTransactionAsync{
            it.copyToRealmOrUpdate(products.map {
                RealmProduct(
                        id = it.id,
                        name = it.name,
                        unit = it.unit,
                        count = it.count
                )
            })
        }
    }

    fun clearAllTaskData() {
        realm.executeTransactionAsync{
            it.delete(RealmProductForTake::class.java)
            it.delete(RealmTask::class.java)
            it.delete(RealmExpedition::class.java)
            it.delete(RealmProduct::class.java)
            it.delete(RealmTaskType::class.java)
        }
    }

    fun clearRealmUser() {
        realm.executeTransactionAsync {
            it.delete(RealmUser::class.java)
        }
    }
}