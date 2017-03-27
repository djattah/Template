package com.l24o.vyatich.data.rest.models

import com.l24o.vyatich.data.realm.models.RealmTask
import java.util.*


/**
 * @autor Gorodilov Nikita
 * @date 24.03.2017
 */
class TaskUtils {

    companion object {
        /**
         * задачу можно взять в работу
         */
        fun isStartTask(task: RealmTask): Boolean {
            return task.userId.isNullOrEmpty();
        }

        /**
         * задачу можно отменить
         */
        fun isCancelTask(task: RealmTask): Boolean {
            return !task.userId.isNullOrEmpty() && task.endDate == null;
        }

        /**
         * задачу можно завершить
         */
        fun isEndTask(task: RealmTask): Boolean {
            return !task.userId.isNullOrEmpty();
        }

        /**
         * задачу не взята в работу никем
         */
        fun isNew(task: RealmTask): Boolean {
            return task.userId.isNullOrEmpty();
        }

        /**
         * задачу взята в работу
         */
        fun isProgress(task: RealmTask): Boolean {
            return !task.userId.isNullOrEmpty();
        }

        /**
         * задачу выполнена
         */
        fun isDone(task: RealmTask): Boolean {
            return task.endDate != null;
        }

        /**
         * Стартуем задачу
         */
        fun startTask(task: RealmTask, userId: String) {
            task.userId = userId
            task.startDate = Date(System.currentTimeMillis())
        }

        /**
         * Отменить задачу
         */
        fun cancelTask(task: RealmTask) {
            task.userId = ""
            task.startDate = null
        }

        /**
         * Закончить задачу
         */
        fun endTask(task: RealmTask) {
            task.endDate = Date(System.currentTimeMillis())
        }
    }

}