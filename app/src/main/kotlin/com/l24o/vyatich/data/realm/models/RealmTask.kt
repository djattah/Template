package com.l24o.vyatich.data.realm.models

import android.support.annotation.DrawableRes
import com.l24o.vyatich.R
import com.l24o.vyatich.data.rest.models.Product
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*
import kotlin.collections.List

/**
 * @author Alexander Popov on 09/01/2017.
 */
// new
open class RealmTask (
        open var user_id: String = "",
        open var user_name: String = "",
        open var document_id: Long = 0,
        open var expidition: Int = 0,
        open var type: String = "",
        open var driver: String = "",
        open var car: String = "",
        open var products: MutableList<RealmProduct> = ArrayList<RealmProduct>()
) : RealmObject()

open class RealmProduct(
        open var id_n: Long = 0,
        open var prod: String = "",
        open var prod_s: String = "",
        open var pranu: String = "",
        open var pranu_s: String = "",
        open var pranu2: String = "",
        open var pranu2_s: String = "",
        open var kolvo_m: Float = 0f,
        open var kolvo_ot: Float = 0f
) : RealmObject()

// prev
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