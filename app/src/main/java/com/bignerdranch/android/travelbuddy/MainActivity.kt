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

    
    var baseCurrency = "Kilometers"
    var convertedToCurrency = "Miles"
    private lateinit var et_firstConversion: EditText
    private lateinit var et_secondConversion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinnerSetup()
        textChanged()
    }

    private fun textChanged() {
        et_firstConversion = findViewById<EditText>(R.id.et_firstConversion) //find by reference
        et_firstConversion.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "On Text Changed")
            }
            override fun afterTextChanged(s: Editable?) {
                try {
                    getResult()
                } catch (e: Exception) {
                    Log.e("Main", "$e")
                }
            }
        })
    }

    private fun getResult() {
        et_firstConversion = findViewById<EditText>(R.id.et_firstConversion) //find by reference
        et_secondConversion = findViewById<EditText>(R.id.et_secondConversion)
        if(et_firstConversion != null
            && et_firstConversion.text.isNotEmpty() && et_firstConversion.text.isNotBlank()) {
            if(baseCurrency == "Kilometers" && convertedToCurrency == "Miles"){
                val df = DecimalFormat("#.###")
                df.roundingMode = RoundingMode.CEILING
                val text = df.format((et_firstConversion.text.toString().toFloat() *  0.621371)).toString()
                et_secondConversion?.setText(text)
            }

            else if(baseCurrency == "Miles" && convertedToCurrency == "Kilometers"){
                val df = DecimalFormat("#.###")
                df.roundingMode = RoundingMode.CEILING
                val text2 = df.format((et_firstConversion.text.toString().toFloat()* 1.6093)).toString()
                et_secondConversion?.setText(text2)
            }
            else if(baseCurrency == convertedToCurrency) {
                Toast.makeText(
                    applicationContext,
                    "Cannot convert the same length",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)
        et_firstConversion = findViewById<EditText>(R.id.et_firstConversion)
        et_secondConversion = findViewById<EditText>(R.id.et_secondConversion)
        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
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
                baseCurrency = parent?.getItemAtPosition(position).toString()
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
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
            }
        })
    }
}