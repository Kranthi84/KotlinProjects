package com.example.chinnakotla.taskslist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.widget.EditText
import com.example.chinnakotla.taskslist.fragments.ListDetailFragment

class ListDetailActivity : AppCompatActivity() {

    lateinit var list: TaskList
    lateinit var addDetailTaskButton: FloatingActionButton
    lateinit private var listDetailFragment: ListDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail)
        list = intent.getParcelableExtra(MainActivity.INTENT_LIST_KEY)
        title = list.taskName

        listDetailFragment = ListDetailFragment.newInstance(list)

        addDetailTaskButton = findViewById(R.id.addButton)
        addDetailTaskButton.setOnClickListener { view ->
            showCreateTaskDialog()
        }


    }


    private fun showCreateTaskDialog() {

        val taskEditText = EditText(this)
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
                .setView(taskEditText)
                .setTitle(getString(R.string.add_task_title))
                .setPositiveButton(getString(R.string.add_task_button), { dialog, which ->

                    val task = taskEditText.text.toString()

                    listDetailFragment?.let {
                        it.addTask(task)
                    }

                    dialog.dismiss()
                }).create().show()
    }
}