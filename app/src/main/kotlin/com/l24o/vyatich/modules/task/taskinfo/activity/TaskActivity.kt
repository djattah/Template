package com.l24o.vyatich.modules.task.taskinfo.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.l24o.vyatich.Constants
import com.l24o.vyatich.R
import com.l24o.vyatich.common.delegates.extras
import com.l24o.vyatich.common.mvp.MvpActivity
import com.l24o.vyatich.data.realm.models.RealmTask
import kotlinx.android.synthetic.main.activity_task.*
import org.jetbrains.anko.onClick

/**
 * @author Alexander Popov on 17/01/2017.
 */
class TaskActivity : MvpActivity(), ITaskActivityView {

    var presenter: ITaskActivityPresenter = TaskActivityPresenter(this)
    private var taskId: String? by extras(Constants.KEY_TASK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        supportActionBar?.title = "Задача №$taskId"
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

    override fun fillInfo(task: RealmTask) {
        type.text = task.type
        descriptions.text = "${task.document_id}"
        button.visibility = if (task.user_name.isEmpty()) View.GONE else View.VISIBLE
        buttonCancel.visibility = if (!task.user_name.isEmpty()) View.VISIBLE else View.GONE
        button.setText(if (!task.user_name.isEmpty()) R.string.task_done else R.string.task_take)
        button.onClick {
            if (!task.user_name.isEmpty()) {
                presenter.finishTask()
            } else {
                presenter.takeTask()
            }
        }
        buttonCancel.onClick {
            presenter.cancelTask()
        }
        //taskTypeIcon.setImageResource(task.type.code.toTaskType().resId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProductAdapter(task.products.toList())
        recyclerView.visibility = View.VISIBLE

    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

}