package com.l24o.vyatich.data.rest.models

import com.l24o.vyatich.data.realm.models.RealmTask

/**
 * @autor Gorodilov Nikita
 * @date 24.03.2017
 */
class TaskUtils {

    companion object {
        /**
         * задачу можно взять в работу
         */
        fun isStartTask(task: Task): Boolean {
            return task.userId.isNullOrEmpty();
        }
        fun isStartTask(task: RealmTask): Boolean {
            return task.userId.isNullOrEmpty();
        }

        /**
         * задачу можно отменить
         */
        fun isCancelTask(task: Task): Boolean {
            return !task.userId.isNullOrEmpty() && task.endDate == null;
        }
        fun isCancelTask(task: RealmTask): Boolean {
            return !task.userId.isNullOrEmpty() && task.endDate == null;
        }

        /**
         * задачу можно завершить
         */
        fun isEndTask(task: Task): Boolean {
            return !task.userId.isNullOrEmpty();
        }
        fun isEndTask(task: RealmTask): Boolean {
            return !task.userId.isNullOrEmpty();
        }

        /**
         * задачу не взята в работу никем
         */
        fun isNew(task: Task): Boolean {
            return task.userId.isNullOrEmpty();
        }
        fun isNew(task: RealmTask): Boolean {
            return task.userId.isNullOrEmpty();
        }

        /**
         * задачу взята в работу
         */
        fun isProgress(task: Task): Boolean {
            return !task.userId.isNullOrEmpty();
        }
        fun isProgress(task: RealmTask): Boolean {
            return !task.userId.isNullOrEmpty();
        }

        /**
         * задачу выполнена
         */
        fun isDone(task: Task): Boolean {
            return task.endDate != null;
        }
        fun isDone(task: RealmTask): Boolean {
            return task.endDate != null;
        }
    }

}