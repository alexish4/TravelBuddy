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
import java.net.URL

class MainActivity : AppCompatActivity() {

    var baseCurrency = "EUR"
    var convertedToCurrency = "USD"
    var conversionRate = 0f

    private lateinit var et_firstCurrencyConversion: EditText
    private lateinit var et_secondCurrencyConversion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerCurrencySetup()
        textCurrencyChanged()
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

}