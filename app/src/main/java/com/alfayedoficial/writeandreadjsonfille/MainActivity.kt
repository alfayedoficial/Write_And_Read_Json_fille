package com.alfayedoficial.writeandreadjsonfille

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alfayedoficial.downloadInfo
import com.alfayedoficial.isConnected
import com.alfayedoficial.isConnectedFast
import okhttp3.OkHttpClient


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val data1 : MutableList<String> = mutableListOf("name" , "Ali","age" , "28","job", "Android Developer","country" ,"Egypt")
//        val data2 : MutableList<String> = mutableListOf("name" , "Ali","age" , "28","job", "Android Developer","country" ,"Egypt")
//        val jsonData = mutableListOf<String>()
//        jsonData.addAll(data1)
//        jsonData.addAll(data2)
//        // You must take permission before save data to cash
//        saveDataToCashJsonFile(jsonData)

    }


    override fun onResume() {
        super.onResume()

        findViewById<TextView>(R.id.textViews).setOnClickListener {
            findViewById<TextView>(R.id.textViews2).text = isConnectedFast()
            if (isConnected()){
                 downloadInfo(OkHttpClient()){
                     // update the UI with the speed test results
                     runOnUiThread {
                         val result = "Is Connected status :${it.first} \n Result Speed : ${it.second}"
                         findViewById<TextView>(R.id.textViews).text = result

                     }

                 }
            }else{
                findViewById<TextView>(R.id.textViews).text = "No Internet Connection"
            }
        }
    }


}