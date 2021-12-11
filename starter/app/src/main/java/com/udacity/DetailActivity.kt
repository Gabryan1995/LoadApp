package com.udacity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = intent.getStringExtra(EXTRA_FILENAME)
        val filenameView = findViewById<TextView>(R.id.fileName).apply {
            text = fileName
        }

        val status = intent.getStringExtra(EXTRA_STATUS)
        val statusView = findViewById<TextView>(R.id.status).apply {
            text = status
        }

        ok_button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        coordinateMotion()
    }

    private fun coordinateMotion() {
        val motionLayout: MotionLayout = findViewById(R.id.motion_layout)
        motionLayout.transitionToEnd()
    }

}
