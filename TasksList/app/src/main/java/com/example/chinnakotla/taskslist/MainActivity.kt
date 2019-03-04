package com.example.chinnakotla.taskslist

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import com.example.chinnakotla.taskslist.fragments.ListDetailFragment
import com.example.chinnakotla.taskslist.fragments.MainFragment

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainFragment.OnFragmentInteractionListener {


    private var mainFragment = MainFragment.newInstance()
    private var frameLayout: FrameLayout? = null
    private var listDetailFragment: ListDetailFragment? = null
    private var largeScreen = false


    override fun onListItemClicked(list: TaskList) {
        showListDetail(list)
    }


    companion object {
        const val INTENT_LIST_KEY = "list"
        const val INTENT_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            showCreateDialog()
        }

        frameLayout = findViewById(R.id.fragment_container)
        mainFragment = supportFragmentManager.findFragmentById(R.id.list_selection_fragment) as MainFragment
        largeScreen = frameLayout != null
        //supportFragmentManager.beginTransaction().add(R.id.fragment_container, mainFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCreateDialog() {
        val dialogTitle = getString(R.string.name_of_list)
        val positiveButtonTitle = getString(R.string.create)

        val builder = AlertDialog.Builder(this)
        val listTitleEditText = EditText(this)
        listTitleEditText.inputType = InputType.TYPE_CLASS_TEXT
        builder.setTitle(dialogTitle)
        builder.setView(listTitleEditText)
        builder.setPositiveButton(positiveButtonTitle, { dialog, which ->

            val list = TaskList(listTitleEditText.text.toString())
            mainFragment.addList(list)
            dialog.dismiss()
            showListDetail(list)
        })
        builder.create().show()
    }

    private fun showListDetail(list: TaskList) {

        if (!largeScreen) {
            val listDetailIntent = Intent(this, ListDetailActivity::class.java)
            listDetailIntent.putExtra(INTENT_LIST_KEY, list)
            startActivityForResult(listDetailIntent, INTENT_REQUEST_CODE)
        } else {
            title = list.taskName
            listDetailFragment = ListDetailFragment.newInstance(list)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, listDetailFragment, getString(R.string.list_fragment_tag))
                    .addToBackStack(null)
                    .commit()

            fab.setOnClickListener { view ->
                showCreateTaskDialog()
            }
        }

    }

    private fun showCreateTaskDialog() {
        val taskEditText = EditText(this)
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT
        AlertDialog.Builder(this)
                .setTitle(R.string.add_task_title)
                .setView(taskEditText)
                .setPositiveButton(R.string.add_task_button, { dialog, which ->
                    val task = taskEditText.text.toString()
                    listDetailFragment?.addTask(task)
                    dialog.dismiss()
                })
                .create()
                .show()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        title = resources.getString(R.string.app_name)
        listDetailFragment?.list?.let {
            mainFragment?.saveList(it)
        }
        if (listDetailFragment != null) {
            supportFragmentManager.beginTransaction().remove(listDetailFragment).commit()
            listDetailFragment = null

        }
        fab.setOnClickListener { view ->
            showCreateDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_REQUEST_CODE) {
            data?.let {
                mainFragment.saveList(it.getParcelableExtra(INTENT_LIST_KEY))
            }
        }
    }


}
