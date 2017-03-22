package com.l24o.vyatich.modules.task

import com.l24o.vyatich.common.mvp.IListPresenter
import com.l24o.vyatich.common.mvp.IListView
import com.l24o.vyatich.data.realm.models.RealmTask

/**
 * @author Alexander Popov on 09/01/2017.
 */
interface ITaskListView : IListView<RealmTask> {
    fun navigateToTask(task: RealmTask)
    fun navigateToLogin()
    fun showTypes(types: List<String>, selectedItem: String?)
    fun showExps(map: List<String>, selectedType: String?)
}

interface ITaskListPresenter : IListPresenter<RealmTask, ITaskListView> {
    fun onClick(task: RealmTask)
    var showNewTasks: Boolean
    var showAllTasks: Boolean
    fun refreshList()
    fun onLogoutClick()
    fun onTypeWrapperClick()
    fun onTypeChosen(type: String?)
    fun onExpedWrapperClick()
    fun onExpChosen(exp: String?)
}