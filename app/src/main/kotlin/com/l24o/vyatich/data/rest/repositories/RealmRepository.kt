package com.l24o.vyatich.data.rest.repositories

import com.l24o.vyatich.data.realm.models.RealmProductForTake
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.rest.models.Task
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
                            userId = task.userId
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
}