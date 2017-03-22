package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.realm.models.*
import com.l24o.vyatich.data.rest.models.*
import io.realm.Realm
import rx.Observable

/**
 * @author Alexander Popov on 11/01/2017.
 * Сохранение в локальной бд - Realm db
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

    fun fetchSyncTasks(): Observable<List<RealmTask>> {
        return realm
                .where(RealmTask::class.java)
                .equalTo("needSync", true)
                .findAll()
                .asObservable()
                .flatMap {
                    results ->
                    Observable.just(realm.copyFromRealm(results))
                }
    }

    fun fetchTasks(showNewTasks: Boolean, showAllTasks: Boolean, selectedType: String?, selectedExp: String?): Observable<List<RealmTask>> {
        val user = realm.where(RealmUser::class.java)
                .findFirst()
        return when {
            !selectedType.isNullOrEmpty() && !selectedExp.isNullOrEmpty() -> {
                realm
                        .where(RealmTask::class.java)
                        .equalTo("type.name", selectedType)
                        .equalTo("expedition.name", selectedExp)
                        .findAll()
                        .asObservable()
                        .flatMap {
                            results ->
                            Observable.just(realm.copyFromRealm(results.filter {
                                when {
                                    showNewTasks -> it.userId == null
                                    !showNewTasks && showAllTasks -> it.userId == user.id
                                    else -> it.userId == user.id && it.endDate == null
                                }
                            }))
                        }
            }
            !selectedType.isNullOrEmpty() -> {
                realm
                        .where(RealmTask::class.java)
                        .equalTo("type.name", selectedType)
                        .findAll()
                        .asObservable()
                        .flatMap {
                            results ->
                            Observable.just(realm.copyFromRealm(results.filter {
                                when {
                                    showNewTasks -> it.userId == null
                                    !showNewTasks && showAllTasks -> it.userId == user.id
                                    else -> it.userId == user.id && it.endDate == null
                                }
                            }))
                        }
            }
            !selectedExp.isNullOrEmpty() -> {
                realm
                        .where(RealmTask::class.java)
                        .equalTo("expedition.name", selectedExp)
                        .findAll()
                        .asObservable()
                        .flatMap {
                            results ->
                            Observable.just(realm.copyFromRealm(results.filter {
                                when {
                                    showNewTasks -> it.userId == null
                                    !showNewTasks && showAllTasks -> it.userId == user.id
                                    else -> it.userId == user.id && it.endDate == null
                                }
                            }))
                        }
            }
            else -> {
                realm
                        .where(RealmTask::class.java)
                        .findAll()
                        .asObservable()
                        .flatMap {
                            results ->
                            Observable.just(realm.copyFromRealm(results.filter {
                                when {
                                    showNewTasks -> it.userId == null
                                    !showNewTasks && showAllTasks -> it.userId == user.id
                                    else -> it.userId == user.id && it.endDate == null
                                }
                            }))
                        }
            }
        }
    }

    fun fetchTaskById(id: String): Observable<RealmTask> {
        return Observable.just(realm.copyFromRealm(
                realm
                        .where(RealmTask::class.java)
                        .equalTo("id", id)
                        .findFirst()
        ))
    }

    fun fetchUser(): Observable<RealmUser> {
        return Observable.just(realm.copyFromRealm(
                realm
                        .where(RealmUser::class.java)
                        .findFirst()
        ))
    }

    fun hasUser(): Observable<Long> {
        return Observable.just(
                realm.where(RealmUser::class.java)
                        .count()
        )
    }

    fun saveTasks(tasks: List<Task>): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
                val list = tasks.map {
                    task ->
                    val products = task.products?.map {
                        product ->
                        it.copyToRealm(RealmProduct(
                                id_n = product.id_n,
                                prod = product.prod,
                                prod_s = product.prod_s,
                                pranu = product.pranu,
                                pranu_s = product.pranu_s,
                                pranu2 = product.pranu2,
                                pranu2_s = product.pranu2_s,
                                kolvo_m = product.kolvo_m,
                                kolvo_ot = product.kolvo_ot
                        ))
                    }
                    val realmTask = RealmTask(
                            user_id = task.user_id,
                            user_name = task.user_name,
                            document_id = task.document_id,
                            expidition = task.expidition,
                            type = task.type,
                            driver = task.driver,
                            car = task.car
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

    fun saveTaskTypesExpProducts(types: List<TaskType>, expeditions: List<Expedition>, products: List<Product>): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
                it.copyToRealmOrUpdate(types.map {
                    RealmTaskType(
                            id = it.id,
                            name = it.name,
                            code = it.code
                    )
                })
                it.copyToRealmOrUpdate(expeditions.map {
                    RealmExpedition(
                            id = it.id,
                            name = it.name,
                            code = it.code
                    )
                })
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

    fun saveUser(user: User): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync({
                it.delete(RealmUser::class.java)
                it.copyToRealm(
                        RealmUser(
                                id = user.id,
                                fio = user.fio,
                                phone = user.phone
                        )
                )
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
                            product = it.where(RealmProduct::class.java)
                                    .equalTo("id", productForTake.productId)
                                    .findFirst(),
                            count = productForTake.count
                    ))
                }
                val realmTask = RealmTask(
                        id = task.id,
                        description = task.description,
                        type = it.where(RealmTaskType::class.java)
                                .equalTo("id", task.typeId)
                                .findFirst(),
                        expedition = it.where(RealmExpedition::class.java)
                                .equalTo("id", task.expeditionId)
                                .findFirst(),
                        startDate = task.startDate,
                        endDate = task.endDate,
                        userId = task.userId
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

    fun clearAll(): Observable<Boolean> {
        return Observable.create<Boolean> {
            sub ->
            realm.executeTransactionAsync({
                it.delete(RealmUser::class.java)
                it.delete(RealmTask::class.java)
                it.delete(RealmTaskType::class.java)
            }, {
                sub.onNext(true)
            }, {
                error ->
                sub.onError(error)
            })
        }

    }
}