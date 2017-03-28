package com.l24o.vyatich.data.realm.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @autor Gorodilov Nikita
 * @date 28.03.2017
 */
open class RealmUser(
        @PrimaryKey
        open var login: String = "",
        open var password: String = ""
): RealmObject()