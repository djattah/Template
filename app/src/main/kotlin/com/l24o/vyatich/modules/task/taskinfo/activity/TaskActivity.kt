package com.l24o.vyatich.modules.task.taskinfo.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.l24o.vyatich.Constants
import com.l24o.vyatich.R
import com.l24o.vyatich.common.VyatichConnectionManager
import com.l24o.vyatich.common.delegates.extras
import com.l24o.vyatich.common.mvp.MvpActivity
import com.l24o.vyatich.data.realm.models.RealmProduct
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.realm.models.RealmTaskType
import com.l24o.vyatich.data.rest.models.Product
import com.l24o.vyatich.data.rest.models.ProductForTake
import com.l24o.vyatich.data.rest.models.Task
import com.l24o.vyatich.data.rest.models.TaskType
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isCancelTask
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isEndTask
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isStartTask
import kotlinx.android.synthetic.main.activity_task.*
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.onClick

/**
 * @author Alexander Popov on 17/01/2017.
 */
class TaskActivity : MvpActivity(), ITaskActivityView {

    lateinit var presenter: ITaskActivityPresenter
    private var taskId: String? by extras(Constants.KEY_TASK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = TaskActivityPresenter(this)
        setContentView(R.layout.activity_task)
        supportActionBar?.title = "Задача № ${taskId}"
        presenter.taskId = taskId
        presenter.onViewAttached()
        initViews()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun navigateToTasks() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun fillTaskTypeInfo(taskType: RealmTaskType) {
        type.text = taskType.name
    }

    override fun fillTaskProductInfo(products: List<RealmProduct>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProductAdapter(products)
        recyclerView.visibility = View.VISIBLE
    }

    override fun fillTaskInfo(task: RealmTask) {
        descriptions.text = task.description
        button.visibility = if (isStartTask(task) || isEndTask(task)) View.VISIBLE else View.GONE
        buttonCancel.visibility = if (isCancelTask(task)) View.VISIBLE else View.GONE
        button.setText(if (isStartTask(task)) R.string.task_take else R.string.task_done)
        button.onClick {
            if (isEndTask(task)) {
                presenter.finishTask()
            } else {
                presenter.takeTask()
            }
        }
        buttonCancel.onClick {
            if (isCancelTask(task))
                presenter.cancelTask()
        }
        taskTypeIcon.setImageResource(R.drawable.ic_assignment_late_white_48dp)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

}