package com.example.chinnakotla.taskslist.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chinnakotla.taskslist.ListDetailRecycleViewAdapter
import com.example.chinnakotla.taskslist.MainActivity

import com.example.chinnakotla.taskslist.R
import com.example.chinnakotla.taskslist.TaskList


class ListDetailFragment : Fragment() {

    lateinit var list: TaskList
    lateinit var taskDetailRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            list = it.getParcelable(MainActivity.INTENT_LIST_KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list_detail, container, false)
        view?.let {
            taskDetailRecyclerView = it.findViewById(R.id.listDetailRecyclerView)
            taskDetailRecyclerView.layoutManager = LinearLayoutManager(activity)
            taskDetailRecyclerView.adapter = ListDetailRecycleViewAdapter(list.taskList)
        }
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(list: TaskList) =
                ListDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(MainActivity.INTENT_LIST_KEY, list)
                    }
                }
    }

    fun addTask(task: String) {
        list?.let {
            it.taskList.add(task)
            val recylerViewAdapter = ListDetailRecycleViewAdapter(list.taskList)
            recylerViewAdapter.notifyItemChanged(list.taskList.size)
        }
    }
}
