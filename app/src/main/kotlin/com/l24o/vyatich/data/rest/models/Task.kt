package com.l24o.vyatich.data.rest.models

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author Alexander Popov on 11/01/2017.
 */
data class Task(
        @SerializedName("id")
        val id: String,
        @SerializedName("ident")
        val ident: String = "",
        @SerializedName("description")
        val description: String,
        @SerializedName("typeId")
        val typeId: String,
        @SerializedName("typeName")
        val typeName: String,
        @SerializedName("expeditionId")
        val expeditionId: String,
        @SerializedName("startDate")
        val startDate: Date?,
        @SerializedName("endDate")
        val endDate: Date?,
        @SerializedName("userId")
        val userId: String?,
        @SerializedName("products")
        val products: List<Product>?,
        @SerializedName("isFinish")
        val isFinish: Boolean
)

data class TaskType(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("code")
        val code: String
)

data class Expedition(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("code")
        val code: String
)

data class Product(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("unit")
        val unit: String,
        @SerializedName("count")
        val count: String
)

data class ProductForTake(
        @SerializedName("productId")
        val productId: String,
        @SerializedName("name")
        val count: Int
)

open class AllData(
        val tasks: List<Task>,
        val types: List<TaskType>,
        val products: List<Product>,
        val exps: List<Expedition>
)

data class TypeAndProductsAndExp(
        val types: List<TaskType>,
        val products: List<Product>,
        val exps: List<Expedition>
)

