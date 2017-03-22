package com.l24o.vyatich.data.rest.models

import com.google.gson.annotations.SerializedName

/**
 * @author Alexander Popov on 23/01/2017.
 */
data class User(
        @SerializedName("id")
        val id: String,
        @SerializedName("fio")
        val fio: String,
        @SerializedName("phone")
        val phone: String
)