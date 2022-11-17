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


    var baseTemp = "Fahrenheit"
    var covertedtoTemp = "Celsius"
    private lateinit var et_firstTempConversion: EditText
    private lateinit var et_secondTempConversion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinnerSetup()
        textChangedTemp()
    }

    private fun textChangedTemp() {
        et_firstTempConversion = findViewById<EditText>(R.id.et_firstTempConversion) //find by reference
        et_firstTempConversion.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "On Text Changed")
            }
            override fun afterTextChanged(s: Editable?) {
                try {
                    getTempResult()
                } catch (e: Exception) {
                    Log.e("Main", "$e")
                }
            }
        })
    }

    private fun getTempResult() {
        et_firstTempConversion = findViewById<EditText>(R.id.et_firstTempConversion) //find by reference
        et_secondTempConversion = findViewById<EditText>(R.id.et_secondTempConversion)
        if(et_firstTempConversion != null
            && et_firstTempConversion.text.isNotEmpty() && et_firstTempConversion.text.isNotBlank()) {
            if(baseTemp == "Fahrenheit" && covertedtoTemp == "Celsius"){
                val df = DecimalFormat("##.##")
                df.roundingMode = RoundingMode.CEILING
                val text = df.format(((et_firstTempConversion.text.toString().toFloat() -32 )* 5/9)).toString()
                et_secondTempConversion?.setText(text)
            }


            else if(baseTemp == "Celsius" && covertedtoTemp == "Fahrenheit"){
                val df = DecimalFormat("##.##")
                df.roundingMode = RoundingMode.CEILING
                val text2 = df.format((et_firstTempConversion.text.toString().toFloat() * 1.8 +32)).toString()
                et_secondTempConversion?.setText(text2)
            }
            else if(baseTemp == covertedtoTemp) {
                Toast.makeText(
                    applicationContext,
                    "Cannot convert the same unit",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstTempConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondTempConversion)
        et_firstTempConversion = findViewById<EditText>(R.id.et_firstTempConversion)
        et_secondTempConversion = findViewById<EditText>(R.id.et_secondTempConversion)
        ArrayAdapter.createFromResource(
            this,
            R.array.temp,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.temp2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }
        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseTemp = parent?.getItemAtPosition(position).toString()
            }
        })
        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                covertedtoTemp = parent?.getItemAtPosition(position).toString()
            }
        })
    }
}