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

    /**
     * достаём таски и лок бд большому количеству условий
     */
    /*fun fetchTasks(showNewTasks: Boolean, showAllTasks: Boolean, selectedType: String?, selectedExp: String?): Observable<List<RealmTask>> {
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
    }*/

    fun fetchTaskByUserId(userId: String): Observable<RealmTask> {
        return Observable.just(realm.copyFromRealm(
                realm
                        .where(RealmTask::class.java)
                        .equalTo("user_id", userId)
                        .findFirst()
        ))
    }

    fun fetchTaskByDocumentId(documentId: Long): Observable<RealmTask> {
        return Observable.just(realm.copyFromRealm(
                realm
                        .where(RealmTask::class.java)
                        .equalTo("document_id", documentId)
                        .findFirst()
        ))
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
                    product ->
                    it.copyToRealm(RealmProduct(
                            id_n = product.id_n,
                            prod = product . prod,
                            prod_s = product . prod_s,
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
                it.delete(RealmTask::class.java)
                it.delete(RealmProduct::class.java)
            }, {
                sub.onNext(true)
            }, {
                error ->
                sub.onError(error)
            })
        }

    }
}