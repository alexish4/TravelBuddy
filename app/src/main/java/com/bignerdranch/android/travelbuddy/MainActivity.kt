package com.bignerdranch.android.travelbuddy

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var translateButton: Button
    //private lateinit var menuItem:

    //currency
    var baseCurrency = "EUR"
    var convertedToCurrency = "USD"
    var conversionRate = 0f

    //Miles and kilometers
    var baseLength = "Kilometers"
    var convertedToLength = "Miles"
    private lateinit var et_firstLengthConversion: EditText
    private lateinit var et_secondLengthConversion: EditText

    private lateinit var et_firstCurrencyConversion: EditText
    private lateinit var et_secondCurrencyConversion: EditText

    //temperature
    var baseTemp = "Celsius"
    var covertedtoTemp = "Fahrenheit"
    private lateinit var et_firstTempConversion: EditText
    private lateinit var et_secondTempConversion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        translateButton = findViewById(R.id.translate_button)

        spinnerCurrencySetup()
        textCurrencyChanged()

        //Miles and kilometers
        spinnerLengthSetup()
        textChangedLength()

        //temperature
        spinnerSetup()
        textChangedTemp()

        translateButton.setOnClickListener {
            val intent = Intent(this, TranslateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun textCurrencyChanged() {
        et_firstCurrencyConversion = findViewById<EditText>(R.id.et_firstCurrencyConversion) //find by reference
        et_firstCurrencyConversion.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "On Text Changed")
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    getApiResult()
                } catch (e: Exception) {
                    Log.e("Main", "$e")
                }
            }

        })
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

    private fun getLengthResult() {
        et_firstLengthConversion = findViewById<EditText>(R.id.et_firstLengthConversion) //find by reference
        et_secondLengthConversion = findViewById<EditText>(R.id.et_secondLengthConversion)
        if(et_firstLengthConversion != null
            && et_firstLengthConversion.text.isNotEmpty() && et_firstLengthConversion.text.isNotBlank()) {
            if(baseLength == "KM" && convertedToLength == "MI"){
                val df = DecimalFormat("#.###")
                df.roundingMode = RoundingMode.CEILING
                val text = df.format((et_firstLengthConversion.text.toString().toFloat() *  0.621371)).toString()
                et_secondLengthConversion?.setText(text)
            }

            else if(baseLength == "MI" && convertedToLength == "KM"){
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

    private fun getTempResult() {
        et_firstTempConversion = findViewById<EditText>(R.id.et_firstTempConversion) //find by reference
        et_secondTempConversion = findViewById<EditText>(R.id.et_secondTempConversion)
        if(et_firstTempConversion != null
            && et_firstTempConversion.text.isNotEmpty() && et_firstTempConversion.text.isNotBlank()) {
            if(baseTemp == "FA" && covertedtoTemp == "CE"){
                val df = DecimalFormat("##.##")
                df.roundingMode = RoundingMode.CEILING
                val text = df.format(((et_firstTempConversion.text.toString().toFloat() -32 )* 5/9)).toString()
                et_secondTempConversion?.setText(text)
            }


            else if(baseTemp == "CE" && covertedtoTemp == "FA"){
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

    private fun getApiResult() {
        et_firstCurrencyConversion = findViewById<EditText>(R.id.et_firstCurrencyConversion) //find by reference
        et_secondCurrencyConversion = findViewById<EditText>(R.id.et_secondCurrencyConversion)
        if(et_firstCurrencyConversion != null
            && et_firstCurrencyConversion.text.isNotEmpty() && et_firstCurrencyConversion.text.isNotBlank()) {
            val API = "https://api.freecurrencyapi.com/v1/latest?apikey=GuuQ5SDlbWeuIswftHTAzIZrEibHsi7vvDLcpMZL&currencies=$convertedToCurrency&base_currency=$baseCurrency" //might have problem
//            if(baseCurrency == "USD")
//                API = "https://api.freecurrencyapi.com/v1/latest?apikey=GuuQ5SDlbWeuIswftHTAzIZrEibHsi7vvDLcpMZL&currencies=$convertedToCurrency"
            if(baseCurrency == convertedToCurrency) {
                Toast.makeText(
                    applicationContext,
                    "Cannot convert the same currency",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val apiResult = URL(API).readText()
                        val jsonObject = JSONObject(apiResult)

                        conversionRate = jsonObject.getJSONObject("data").getString(convertedToCurrency).toFloat() //possible problem

                        Log.d("Main", "$conversionRate")
                        Log.d("Main", apiResult)

                        withContext(Dispatchers.Main) {
                            val text = ((et_firstCurrencyConversion.text.toString().toFloat()) * conversionRate).toString()
                            et_secondCurrencyConversion?.setText(text)
                        }

                    } catch (e: Exception) {
                        Log.e("Main", "$e")
                    }
                }
            }
        }
    } //end of getApiResult method

    private fun spinnerCurrencySetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstCurrencyConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondCurrencyConversion)

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
                getApiResult()
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
                getApiResult()
            }

        })
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

    private fun spinnerLengthSetup() {
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