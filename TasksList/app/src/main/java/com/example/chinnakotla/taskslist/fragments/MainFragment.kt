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
import com.example.chinnakotla.taskslist.*


class MainFragment : Fragment(), ListRecycleViewAdapter.ListSelectionRecyclerViewListener {

    override fun listItemClicked(list: TaskList) {
        listener?.let {
            it.onListItemClicked(list)
        }
    }


    private var listener: OnFragmentInteractionListener? = null
    lateinit var mRecyclerView: RecyclerView
    lateinit var listDataManager: ListDataManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            listDataManager = ListDataManager(context)
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {

        fun onListItemClicked(list: TaskList)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                MainFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val lists = listDataManager.readList()
        view?.let {

            mRecyclerView = it.findViewById(R.id.listRecyclerView)
            mRecyclerView.layoutManager = LinearLayoutManager(activity)
            mRecyclerView.adapter = ListRecycleViewAdapter(lists, this)
        }

    }

    fun addList(list: TaskList) {
        listDataManager?.let {
            it.saveList(list)
        }
        val recycleViewAdapter = mRecyclerView.adapter as ListRecycleViewAdapter
        recycleViewAdapter.addList(list)
    }

    fun saveList(list: TaskList){
        listDataManager.saveList(list)
        updateLists()
    }
    fun updateLists() {
        val lists = listDataManager?.readList()
        mRecyclerView.adapter = ListRecycleViewAdapter(lists, this)
    }

}
