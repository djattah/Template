package com.l24o.vyatich.data.realm.models

import android.support.annotation.DrawableRes
import com.l24o.vyatich.R
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * @author Alexander Popov on 09/01/2017.
 */
open class RealmTask(
        @PrimaryKey
        open var realmId: Int = 0,
        open var id: String = "",
        open var ident: String = "",
        open var description: String = "",
        open var typeId: String = "",
        open var typeName: String = "",
        open var expeditionId: String = "",
        open var startDate: Date? = null,
        open var endDate: Date? = null,
        open var userId: String? = null,
        open var products: RealmList<RealmProduct> = RealmList(),
        open var isFinish: Boolean = false
) : RealmObject()

open class RealmTaskType(
        @PrimaryKey
        open var id: String = "",
        open var name: String = "",
        open var code: String = ""
) : RealmObject()

open class RealmExpedition(
        @PrimaryKey
        open var id: String = "",
        open var name: String = "",
        open var code: String = ""
) : RealmObject()

open class RealmProduct(
        @PrimaryKey
        open var realmId: Int = 0,
        open var id: String = "",
        open var name: String = "",
        open var unit: String = "",
        open var count: String = ""
) : RealmObject()

open class RealmProductForTake(
        open var productId: String = "",
        open var count: Int = 0
) : RealmObject()