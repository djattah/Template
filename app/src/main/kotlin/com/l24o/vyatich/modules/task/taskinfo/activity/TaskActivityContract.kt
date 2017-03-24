package com.l24o.vyatich.modules.task.taskinfo.activity

import com.l24o.vyatich.common.mvp.IPresenter
import com.l24o.vyatich.common.mvp.IView
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.rest.models.Task

/**
 * @author Alexander Popov on 17/01/2017.
 */
interface ITaskActivityView : IView {
    fun navigateToTasks()
    fun fillInfo(task: Task)
}

interface ITaskActivityPresenter : IPresenter<ITaskActivityView> {
    var task: Task
    var taskId: String?
    fun takeTask()
    fun finishTask()
    fun cancelTask()
}