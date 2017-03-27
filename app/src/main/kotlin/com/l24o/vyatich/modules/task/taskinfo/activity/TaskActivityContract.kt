package com.l24o.vyatich.modules.task.taskinfo.activity

import com.l24o.vyatich.common.mvp.IPresenter
import com.l24o.vyatich.common.mvp.IView
import com.l24o.vyatich.data.realm.models.RealmProduct
import com.l24o.vyatich.data.realm.models.RealmProductForTake
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.realm.models.RealmTaskType

/**
 * @author Alexander Popov on 17/01/2017.
 */
interface ITaskActivityView : IView {
    fun navigateToTasks()

    fun fillTaskInfo(task: RealmTask)
    fun fillTaskTypeInfo(taskType: RealmTaskType)
    fun fillTaskProductInfo(products: List<RealmProduct>)
}

interface ITaskActivityPresenter : IPresenter<ITaskActivityView> {
    var task: RealmTask
    var taskId: String?
    fun takeTask()
    fun finishTask()
    fun cancelTask()
}