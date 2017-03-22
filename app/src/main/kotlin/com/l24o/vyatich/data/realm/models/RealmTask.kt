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
        open var id: String = "",
        open var description: String = "",
        open var type: RealmTaskType = RealmTaskType(),
        open var expedition: RealmExpedition = RealmExpedition(),
        open var startDate: Date? = null,
        open var endDate: Date? = null,
        open var userId: String? = null,
        open var products: RealmList<RealmProductForTake> = RealmList(),
        open var needSync: Boolean = false
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
        open var id: String = "",
        open var name: String = "",
        open var unit: String = ""
) : RealmObject()

open class RealmProductForTake(
        open var product: RealmProduct = RealmProduct(),
        open var count: Int = 0
) : RealmObject()

enum class LocalTaskType(@DrawableRes val resId: Int) {
    TRANSFER1(R.drawable.ic_assignment_late_white_48dp),
    TRANSFER2(R.drawable.ic_assignment_turned_in_white_48dp),
    TRANSFER3(R.drawable.ic_assignment_return_white_48dp),
    TAKE(R.drawable.ic_assignment_white_48dp)
}


fun String.toTaskType(): LocalTaskType {
    when (this) {
        "transfer1" -> return LocalTaskType.TRANSFER1
        "transfer2" -> return LocalTaskType.TRANSFER2
        "transfer3" -> return LocalTaskType.TRANSFER3
        "take" -> return LocalTaskType.TAKE
        else -> return LocalTaskType.TRANSFER1
    }
}