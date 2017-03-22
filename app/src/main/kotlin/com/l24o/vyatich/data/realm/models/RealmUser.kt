package com.l24o.vyatich.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Alexander Popov on 23/01/2017.
 */
open class RealmUser(
        @PrimaryKey
        open var id: String = "",
        open var fio: String = "",
        open var phone: String = ""
) : RealmObject()