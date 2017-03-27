package com.l24o.vyatich.common.mvp

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.util.SortedList
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.l24o.vyatich.R
import com.l24o.vyatich.data.rest.models.Task
import kotlinx.android.synthetic.main.layout_recycler_view.*
import org.jetbrains.anko.toast

abstract class BaseListActivity : MvpActivity(), IListView<Task> {

    protected var dataset: SortedList<Task> = SortedList<Task>(Task::class.java, object : SortedList.Callback<Task>() {
        override fun areItemsTheSame(item1: Task, item2: Task): Boolean {
            return item1.id == item2.id
        }

        override fun onChanged(position: Int, count: Int) {
            adapter?.notifyItemChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter?.notifyItemMoved(fromPosition, toPosition)
        }

        override fun compare(o1: Task, o2: Task): Int {
            return o1.id.compareTo(o2.id)
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun onInserted(position: Int, count: Int) {
            adapter?.notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter?.notifyItemRangeRemoved(position, count)
        }
    })
    protected var adapter: RecyclerView.Adapter<*>? = null

    protected var emptyMessageText: String = ""
        set(message) {
            emptyMessageTextView.text = message
        }

    protected var swipeToRefreshEnabled = false
        set (isEnabled) {
            swipeRefreshLayout.isEnabled = isEnabled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recycler_view)
    }

    override fun setLoadingVisible(isVisible: Boolean) {
        if (!swipeRefreshLayout.isRefreshing) {
            progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
        swipeRefreshLayout.isRefreshing = false
    }

    override fun showData(dataset: List<Task>) {
        this.dataset.clear()
        this.dataset.addAll(dataset)
        swipeRefreshLayout.isRefreshing = false
        setLoadingVisible(false)
        setEmptyViewVisible(this.dataset.size() == 0)
        adapter?.notifyDataSetChanged()
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun showMessage(messageResId: Int) {
        toast(messageResId)
    }

    open protected fun onSwipeToRefresh() {
    }

    protected fun setEmptyViewVisible(visible: Boolean) {
        emptyMessageTextView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    protected open fun initViews() {
        swipeRefreshLayout!!.setOnRefreshListener {
            onSwipeToRefresh()
        }

        val accentColorId = ContextCompat.getColor(this, R.color.colorPrimary)
        swipeRefreshLayout.setColorSchemeColors(accentColorId)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        adapter = createAdapter()
        if (adapter == null) {
            throw IllegalArgumentException("createAdapter() should return adapter instance.")
        }
        recyclerView.adapter = adapter
    }

    protected abstract fun createAdapter(): RecyclerView.Adapter<*>
}
