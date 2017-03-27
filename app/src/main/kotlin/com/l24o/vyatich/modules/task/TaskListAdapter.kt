package com.l24o.vyatich.modules.task

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.l24o.vyatich.R
import com.l24o.vyatich.data.realm.models.RealmTask
import com.l24o.vyatich.data.rest.models.Task
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isCancelTask
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isDone
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isNew
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isProgress
import com.l24o.vyatich.data.rest.models.TaskUtils.Companion.isStartTask
import kotlinx.android.synthetic.main.item_task.view.*
import org.jetbrains.anko.onClick

/**
 * @author Alexander Popov on 11/01/2017.
 */
class TaskListAdapter(val data: SortedList<RealmTask>, val itemClick: (RealmTask) -> Unit) : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    var context: Context? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return data.size()
    }

    class ViewHolder(itemView: View, val itemClick: (RealmTask) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: RealmTask) {
            itemView.apply {
                item_task_icon.setImageResource(R.drawable.ic_assignment_late_white_48dp)
                descriptions.text = item.description.replace("\n", " ")
                type.text = item.typeId
                var statusRes = R.string.task_status_new
                var statusColor = R.color.colorPrimary
                when {
                    isNew(item) -> {
                        statusRes = R.string.task_status_new
                        statusColor = R.color.colorPrimary
                    }
                    isProgress(item) -> {
                        statusColor = R.color.colorYellow
                        statusRes = R.string.task_status_in_progress
                    }
                    isDone(item) -> {
                        statusColor = R.color.colorGreen
                        statusRes = R.string.task_status_done
                    }
                }
                statusView.setBackgroundColor(ContextCompat.getColor(context!!, statusColor))
                status.setText(statusRes)
                status.visibility = if (item.startDate != null) View.VISIBLE else View.GONE
                onClick { itemClick(item) }
            }
        }
    }

}