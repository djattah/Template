package com.l24o.vyatich.data.rest.models

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author Alexander Popov on 11/01/2017.
 */
// new
data class Task (
        @SerializedName("user_id")
        val user_id: String,
        @SerializedName("user_name")
        val user_name: String,
        @SerializedName("document_id")
        val document_id: Long,
        @SerializedName("expidition")
        val expidition: Int,
        @SerializedName("type")
        val type: String,
        @SerializedName("driver")
        val driver: String,
        @SerializedName("car")
        val car: String,
        @SerializedName("products")
        val products: List<Product>?
)

data class Product(
        @SerializedName("id_n")
        val id_n: Long,
        @SerializedName("prod")
        val prod: String,
        @SerializedName("prod_s")
        val prod_s: String,
        @SerializedName("pranu")
        val pranu: String,
        @SerializedName("pranu_s")
        val pranu_s: String,
        @SerializedName("pranu2")
        val pranu2: String,
        @SerializedName("pranu2_s")
        val pranu2_s: String,
        @SerializedName("kolvo_m")
        val kolvo_m: Float,
        @SerializedName("kolvo_ot")
        val kolvo_ot: Float
)

