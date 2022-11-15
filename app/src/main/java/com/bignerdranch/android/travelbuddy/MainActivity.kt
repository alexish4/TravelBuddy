package com.bignerdranch.android.travelbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    var baseCurrency = "EUR"
    var convertedToCurrency = "USD"
    var conversionRate = 0f

    private lateinit var et_firstConversion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        spinnerSetup()
//        textChanged()
    }

    private fun getApiResult() {
        et_firstConversion = findViewById<EditText>(R.id.et_firstConversion)
        if(et_firstConversion != null
            && et_firstConversion.text.isNotEmpty() && et_firstConversion.text.isNotBlank()) {
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

                        conversionRate = jsonObject.getJSONObject("data").getString(convertedToCurrency).toFloat()

                        Log.d("Main", "$conversionRate")
                        Log.d("Main", apiResult)
                    } catch (e: Exception) {
                        Log.e("Main", "$e")
                    }
                }
            }
        }
    }

}