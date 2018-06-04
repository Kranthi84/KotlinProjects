package com.raywenderlich.timefighter.timefighter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tapButton: Button
    private lateinit var mCountDownTimer: CountDownTimer
    private var mScore = 0
    private val initialCountDown: Long = 60000
    private val mCountInterval: Long = 1000
    private var gameStarted = false
    internal var timeLeftOnTimer: Long = 60000

    companion object {
        private val score_key = "SCORE KEY"
        private val timeLeft_key = "TIME LEFT KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tvScore = findViewById(R.id.tvScore)
        tvTimer = findViewById(R.id.tvCount)
        tapButton = findViewById(R.id.button_tapme)


        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(score_key)
            timeLeftOnTimer = savedInstanceState.getLong(timeLeft_key)
            restoreGame()

        } else {
            resetGame()
        }



        tapButton.setOnClickListener{view ->

            val bounceAnimation = AnimationUtils.loadAnimation(this,R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()

        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(score_key, mScore)
        outState?.putLong(timeLeft_key, timeLeftOnTimer)
        mCountDownTimer.cancel()

    }

    fun restoreGame() {
        tvScore.text = getString(R.string.textview, mScore.toString())
        tvTimer.text = getString(R.string.timer_count_down, (timeLeftOnTimer / 1000).toString())
        mCountDownTimer = object : CountDownTimer(timeLeftOnTimer, mCountInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                var leftTime = millisUntilFinished / 1000
                tvTimer.text = getString(R.string.timer_count_down, leftTime.toString())

            }

            override fun onFinish() {
                endGame()
            }
        }

    }

    fun resetGame() {
        mScore = 0
        tvScore.text = getString(R.string.textview, mScore.toString())
        tvTimer.text = getString(R.string.timer_count_down, (initialCountDown / 1000).toString())

        mCountDownTimer = object : CountDownTimer(initialCountDown, mCountInterval) {
            override fun onFinish() {
                endGame()
            }

            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                tvTimer.text = getString(R.string.timer_count_down, (millisUntilFinished / 1000).toString())

            }
        }

        gameStarted = false
    }

    fun endGame() {
        Toast.makeText(this, getString(R.string.game_over, mScore.toString()), Toast.LENGTH_LONG).show()
        resetGame()
    }

    fun startGame() {
        mCountDownTimer.start()
        gameStarted = true
    }

    fun incrementScore() {

        if (!gameStarted) {
            startGame()
        }


        mScore += 1
        tvScore.text = getString(R.string.textview, mScore.toString())
        val blinkAnimation = AnimationUtils.loadAnimation(this,R.anim.blink)
        tvScore.startAnimation(blinkAnimation)

    }
}
