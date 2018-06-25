package com.anwesh.uiprojects.kotlinlinkedbreaklineview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linkedbreaklineview.LinkedBreakLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedBreakLineView.create(this)
    }
}
