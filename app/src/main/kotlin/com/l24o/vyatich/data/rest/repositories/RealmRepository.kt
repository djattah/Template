package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.realm.models.*
import com.l24o.vyatich.data.rest.models.Expedition
import com.l24o.vyatich.data.rest.models.Product
import com.l24o.vyatich.data.rest.models.Task
import com.l24o.vyatich.data.rest.models.TaskType
import io.realm.Realm
import rx.Observable

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

    fun saveTasks(tasks: List<Task>): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
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
            },
                    {
                        sub.onNext(true)
                    }, {
                error ->
                sub.onError(error)
            })
        }
    }

    fun saveTaskTypes(taskTypes: List<TaskType>): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
                it.copyToRealmOrUpdate(taskTypes.map {
                    RealmTaskType(
                            id = it.id,
                            name = it.name,
                            code = it.code
                    )
                })
            },
                    {
                        sub.onNext(true)
                    }, {
                error ->
                sub.onError(error)
            })
        }
    }

    fun updateTask(task: RealmTask): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
                it.copyToRealmOrUpdate(task)
            },
                    {
                        sub.onNext(true)
                    }, {
                error ->
                sub.onError(error)
            })
        }
    }

    fun updateTask(task: Task): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
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
            },
                    {
                        sub.onNext(true)
                    }, {
                error ->
                sub.onError(error)
            })
        }
    }

    fun saveExpeditions(expeditions: List<Expedition>): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
                it.copyToRealmOrUpdate(expeditions.map {
                    RealmExpedition(
                            id = it.id,
                            name = it.name,
                            code = it.code
                    )
                })
            },
                    {
                        sub.onNext(true)
                    }, {
                error ->
                sub.onError(error)
            })
        }
    }

    fun saveProducts(products: List<Product>): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
                it.copyToRealmOrUpdate(products.map {
                    RealmProduct(
                            id = it.id,
                            name = it.name,
                            unit = it.unit
                    )
                })
            },
                    {
                        sub.onNext(true)
                    }, {
                error ->
                sub.onError(error)
            })
        }
    }
}