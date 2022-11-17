package com.bignerdranch.android.travelbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    
    var baseLength = "Kilometers"
    var convertedToLength = "Miles"
    private lateinit var et_firstLengthConversion: EditText
    private lateinit var et_secondLengthConversion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinnerSetup()
        textChangedLength()
    }

    private fun textChangedLength() {
        et_firstLengthConversion = findViewById<EditText>(R.id.et_firstLengthConversion) //find by reference
        et_firstLengthConversion.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "On Text Changed")
            }
            override fun afterTextChanged(s: Editable?) {
                try {
                    getLengthResult()
                } catch (e: Exception) {
                    Log.e("Main", "$e")
                }
            }
        })
    }

    private fun getLengthResult() {
        et_firstLengthConversion = findViewById<EditText>(R.id.et_firstLengthConversion) //find by reference
        et_secondLengthConversion = findViewById<EditText>(R.id.et_secondLengthConversion)
        if(et_firstLengthConversion != null
            && et_firstLengthConversion.text.isNotEmpty() && et_firstLengthConversion.text.isNotBlank()) {
            if(baseLength == "Kilometers" && convertedToLength == "Miles"){
                val df = DecimalFormat("#.###")
                df.roundingMode = RoundingMode.CEILING
                val text = df.format((et_firstLengthConversion.text.toString().toFloat() *  0.621371)).toString()
                et_secondLengthConversion?.setText(text)
            }

            else if(baseLength == "Miles" && convertedToLength == "Kilometers"){
                val df = DecimalFormat("#.###")
                df.roundingMode = RoundingMode.CEILING
                val text2 = df.format((et_firstLengthConversion.text.toString().toFloat()* 1.6093)).toString()
                et_secondLengthConversion?.setText(text2)
            }
            else if(baseLength == convertedToLength) {
                Toast.makeText(
                    applicationContext,
                    "Cannot convert the same length",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun spinnerSetup() {
        val spinnerlength: Spinner = findViewById(R.id.spinner_firstLengthConversion)
        val spinnerlength2: Spinner = findViewById(R.id.spinner_secondLengthConversion)
        et_firstLengthConversion = findViewById<EditText>(R.id.et_firstLengthConversion)
        et_secondLengthConversion = findViewById<EditText>(R.id.et_secondLengthConversion)
        ArrayAdapter.createFromResource(
            this,
            R.array.length,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerlength.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.length2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerlength2.adapter = adapter
        }
        spinnerlength.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseLength = parent?.getItemAtPosition(position).toString()
            }
        })
        spinnerlength2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertedToLength = parent?.getItemAtPosition(position).toString()
            }
        })
    }
}