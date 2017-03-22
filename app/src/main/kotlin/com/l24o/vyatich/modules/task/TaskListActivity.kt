package com.l24o.vyatich.modules.task

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.l24o.vyatich.Constants
import com.l24o.vyatich.R
import com.l24o.vyatich.common.mvp.BaseListActivity
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.extensions.materialDialog
import com.l24o.vyatich.modules.signin.SignInActivity
import com.l24o.vyatich.modules.task.taskinfo.activity.TaskActivity
import kotlinx.android.synthetic.main.activity_task_list.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * @author Alexander Popov on 09/01/2017.
 */
class TaskListActivity : BaseListActivity(), ITaskListView {

    var presenter: ITaskListPresenter = TaskListPresenter(this)

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToTask(task: RealmTask) {
        startActivity<TaskActivity>(Constants.KEY_TASK to task.id)
    }

    override fun onResume() {
        super.onResume()
        presenter.refreshList()
    }

    override fun navigateToLogin() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showTypes(types: List<String>, selectedItem: String?) {
        materialDialog {
            title(R.string.choose_type)
            items(types)
            itemsCallbackSingleChoice(
                    types.indexOfFirst { it == selectedItem },
                    { materialDialog, view, index, charSequence -> true }
            )
            positiveText(R.string.logout_ok)
            negativeText(R.string.logout_no)
            neutralText(R.string.clear)
            onPositive { materialDialog, dialogAction ->
                val type = types[materialDialog.selectedIndex]
                typeFilter.text = type
                presenter.onTypeChosen(type)
            }

            onNegative { materialDialog, dialogAction ->

            }
            onNeutral { materialDialog, dialogAction ->
                typeFilter.text = getString(R.string.not_chose)
                presenter.onTypeChosen(null)
            }
        }.show()
    }

    override fun onBackPressed() {}

    override fun showExps(exps: List<String>, selectedItem: String?) {
        materialDialog {
            title(R.string.choose_type)
            items(exps)
            itemsCallbackSingleChoice(
                    exps.indexOfFirst { it == selectedItem },
                    { materialDialog, view, index, charSequence -> true }
            )
            positiveText(R.string.logout_ok)
            neutralText(R.string.clear)
            negativeText(R.string.logout_no)
            onPositive { materialDialog, dialogAction ->
                val exp = exps[materialDialog.selectedIndex]
                expFilter.text = exp
                presenter.onExpChosen(exp)
            }
            onNegative { materialDialog, dialogAction ->

            }

            onNeutral { materialDialog, dialogAction ->
                expFilter.text = getString(R.string.not_chose)
                presenter.onExpChosen(null)
            }
        }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        initViews()
        presenter.onViewAttached()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_exit -> {
                materialDialog {
                    title(R.string.logout_title)
                    content(R.string.logout_content)
                    positiveText(R.string.logout_ok)
                    negativeText(R.string.logout_no)
                    onPositive { materialDialog, dialogAction ->
                        presenter.onLogoutClick()
                    }
                    onNegative { materialDialog, dialogAction ->

                    }
                }.show()
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    override fun initViews() {
        super.initViews()
        val sActionBar = supportActionBar
        if (sActionBar != null) {
            sActionBar.navigationMode = ActionBar.NAVIGATION_MODE_TABS
            val tabListener = object : ActionBar.TabListener {
                override fun onTabReselected(tab: ActionBar.Tab, ft: android.support.v4.app.FragmentTransaction) {
                }

                override fun onTabUnselected(tab: ActionBar.Tab, ft: android.support.v4.app.FragmentTransaction) {
                    presenter.showNewTasks = tab.text != (getString(R.string.new_tasks))
                    presenter.refreshList()
                    checkbox.visibility = if (presenter.showNewTasks) View.GONE else View.VISIBLE
                    taskAndExpeditionFilter.visibility = if (presenter.showNewTasks) View.VISIBLE else View.GONE
                }

                override fun onTabSelected(tab: ActionBar.Tab, ft: android.support.v4.app.FragmentTransaction) {

                }
            }

            sActionBar.addTab(sActionBar.newTab()
                    .setText(R.string.new_tasks)
                    .setTabListener(tabListener))
            sActionBar.addTab(sActionBar.newTab()
                    .setText(R.string.my_tasks)
                    .setTabListener(tabListener))
        }
        emptyMessageText = getString(R.string.task_list_empty_message)
        checkbox.setOnCheckedChangeListener { compoundButton, b ->
            presenter.showAllTasks = b
            presenter.refreshList()
        }
        typeWrapper.onClick {
            presenter.onTypeWrapperClick()
        }
        expedWrapper.onClick {
            presenter.onExpedWrapperClick()
        }
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return TaskListAdapter(dataset, {
            item ->
            presenter.onClick(item)
        })
    }


}