package com.alfayedoficial.writeandreadjsonfille

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data1 : MutableList<String> = mutableListOf("name" , "Ali","age" , "28","job", "Android Developer","country" ,"Egypt")
        val data2 : MutableList<String> = mutableListOf("name" , "Ali","age" , "28","job", "Android Developer","country" ,"Egypt")
        val jsonData = mutableListOf<String>()
        jsonData.addAll(data1)
        jsonData.addAll(data2)
        // You must take permission before save data to cash
        saveDataToCashJsonFile(jsonData)

    }


}