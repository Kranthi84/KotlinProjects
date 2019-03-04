package com.android.chinnakotla.aboutme

import android.content.Context
import android.databinding.DataBindingUtil
import android.hardware.input.InputManager
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.android.chinnakotla.aboutme.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val myName = MyName("Kranthi Chinnakotla")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.name = myName

        binding.doneButton.setOnClickListener {
            addNickName(it)
        }
    }

    fun addNickName(view: View) {

        binding.apply {
            myName.myNickName = nicknameEditText.text.toString()
            invalidateAll()
            nicknameEditText.visibility = View.GONE
            nicknameText.visibility = View.VISIBLE
            doneButton.visibility = View.GONE
        }


        //Hide the keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
