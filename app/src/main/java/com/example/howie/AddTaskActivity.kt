package com.example.howie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }
}
