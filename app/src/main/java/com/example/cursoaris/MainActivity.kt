package com.example.cursoaris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    private var isMaleSelected:Boolean=true
    private var isFemaleSelected:Boolean=false

    private lateinit var viewMale:CardView
    private lateinit var viewFemale:CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initListeners()
    }


    private fun initComponents() {
        viewMale = findViewById(R.id.viewMale)
        viewFemale = findViewById(R.id.viewFemale)
    }
    private fun initListeners() {
        viewMale.setOnClickListener{setGenderColor()}
        viewFemale.setOnClickListener{setGenderColor()}
    }

    private fun setGenderColor(isViewSelected:Boolean){

    }
}