package com.alfayedoficial.writeandreadjsonfille

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

private const val PARENT_FILE_NAME = "dataApp"
private const val JSON_FILE_NAME = "writeandreadjsonfille.json"
private const val PATH_JSON_FILE = "PATH_JSON_FILE"

private fun createFolder() : File {
    val dir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath.toString() , PARENT_FILE_NAME)
    } else {
        File(Environment.getExternalStorageDirectory().absolutePath.toString() , PARENT_FILE_NAME)
    }
    return dir
}


fun Activity.saveDataToCashJsonFile(model: MutableList<String>){
    // create loading Cash Map
    val loadingMap: MutableList<String> = mutableListOf()
    loadingMap.addAll(model)
    if (!loadPathJsonFile().isNullOrEmpty()){
        val jsonListFromReadingJson = readDataFromCashJsonFile()
        if (!jsonListFromReadingJson.isNullOrEmpty()){
            val convertedList = convertStringToMap(jsonListFromReadingJson)
            if (convertedList.isNotEmpty()){
                loadingMap.addAll(convertedList)
            }
        }

    }

    // Create a path where we will place our private file on external storage.
    val folder = createFolder()

    try {
        if (!folder.exists()){
            folder.mkdirs()
        }
        val jsonFile = File(folder,JSON_FILE_NAME)
        jsonFile.createNewFile()
        FileOutputStream(jsonFile).use {
            it.write(Gson().toJson(loadingMap).toByteArray())
            it.flush()
            it.close()
        }

        savePathJsonFile(jsonFile.toString())
    }catch (e: Exception){
        e.printStackTrace()
    }
}

fun Activity.readDataFromCashJsonFile(): String?{
    val path = loadPathJsonFile()
    return if (path != null){
        // read file from internal storage
        val file = File(path)
        if (!file.exists()){
            return null
        }
        val fileInputStream = FileInputStream(file)
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = null
        while (run { text = bufferedReader.readLine()
                text } != null) {
            stringBuilder.append(text)
        }
        stringBuilder.toString()
    }else{
        null
    }
}

// save in shared preferences
fun Activity.savePathJsonFile(path : String){
    val sharedPref = getPreferences(MODE_PRIVATE ) ?: return
    with (sharedPref.edit()) {
        putString(PATH_JSON_FILE, path)
        commit()
    }
}

// read from shared preferences
fun Activity.loadPathJsonFile() : String?{
    val sharedPref = getPreferences(MODE_PRIVATE ) ?: return null
    return sharedPref.getString(PATH_JSON_FILE, null)
}

fun convertStringToMap(string: String): MutableList<String> {
    val gson = Gson()
    val type = object : TypeToken<MutableList<String>>() {}.type
    return gson.fromJson(string, type)
}