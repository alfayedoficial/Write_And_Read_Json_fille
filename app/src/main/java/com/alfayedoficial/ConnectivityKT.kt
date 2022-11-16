package com.alfayedoficial

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.floor
import kotlin.math.roundToInt


private var startTime: Long = 0
private var endTime: Long = 0
private var fileSize: Long = 0

// Bandwidth range in kbps copied from FBConnect Class
private const val POOR_BANDWIDTH = 20
private const val AVERAGE_BANDWIDTH = 550
private const val GOOD_BANDWIDTH = 2000

/*
* These constants aren't yet available in my API level (7), but I need to
* handle these cases if they come up, on newer versions
*/

const val NETWORK_TYPE_EHRPD = 14 // Level 11
const val NETWORK_TYPE_EVDO_B = 12 // Level 9
const val NETWORK_TYPE_HSPAP = 15 // Level 13
const val NETWORK_TYPE_IDEN = 11 // Level 8
const val NETWORK_TYPE_LTE = 13 // Level 11


/**
 * Check if there is any connectivity
 *
 * @return
 */
fun Context.isConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = cm.activeNetworkInfo
    return info != null && info.isConnected
}

fun Context.isConnectedFast(): String? {
    val cm =getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = cm.activeNetworkInfo
    return if (info != null && info.isConnected) {
        isConnectionFast(info.type, info.subtype)
    } else "No NetWork Access"
}


fun isConnectionFast(type: Int, subType: Int): String? {
    return when (type) {
        ConnectivityManager.TYPE_WIFI -> {
            "CONNECTED VIA WIFI"
        }
        ConnectivityManager.TYPE_MOBILE -> {
            when (subType) {
                TelephonyManager.NETWORK_TYPE_1xRTT -> "NETWORK TYPE 1xRTT" // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_CDMA -> "NETWORK TYPE CDMA (3G) Speed: 2 Mbps" // ~ 14-64 kbps
                TelephonyManager.NETWORK_TYPE_EDGE -> "NETWORK TYPE EDGE (2.75G) Speed: 100-120 Kbps" // ~
                TelephonyManager.NETWORK_TYPE_EVDO_0 -> "NETWORK TYPE EVDO_0" // ~ 400-1000 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_A -> "NETWORK TYPE EVDO_A" // ~ 600-1400 kbps
                TelephonyManager.NETWORK_TYPE_GPRS -> "NETWORK TYPE GPRS (2.5G) Speed: 40-50 Kbps" // ~ 100
                TelephonyManager.NETWORK_TYPE_HSDPA -> "NETWORK TYPE HSDPA (4G) Speed: 2-14 Mbps" // ~ 2-14
                TelephonyManager.NETWORK_TYPE_HSPA -> "NETWORK TYPE HSPA (4G) Speed: 0.7-1.7 Mbps" // ~
                TelephonyManager.NETWORK_TYPE_HSUPA -> "NETWORK TYPE HSUPA (3G) Speed: 1-23 Mbps" // ~ 1-23
                TelephonyManager.NETWORK_TYPE_UMTS -> "NETWORK TYPE UMTS (3G) Speed: 0.4-7 Mbps" // ~ 400-7000
                NETWORK_TYPE_EHRPD -> "NETWORK TYPE EHRPD" // ~ 1-2 Mbps
                NETWORK_TYPE_EVDO_B -> "NETWORK_TYPE_EVDO_B" // ~ 5 Mbps
                NETWORK_TYPE_HSPAP -> "NETWORK TYPE HSPA+ (4G) Speed: 10-20 Mbps" // ~ 10-20
                NETWORK_TYPE_IDEN -> "NETWORK TYPE IDEN" // ~25 kbps
                NETWORK_TYPE_LTE -> "NETWORK TYPE LTE (4G) Speed: 10+ Mbps" // ~ 10+ Mbps
                TelephonyManager.NETWORK_TYPE_UNKNOWN -> "NETWORK TYPE UNKNOWN"
                else -> ""
            }
        }
        else -> {
            ""
        }
    }
}


 fun downloadInfo(nclient: OkHttpClient , result:(Pair<Boolean , String>)->Unit) {
    var status = false
    var speed = "0"
    val request: Request = Request.Builder()
        .url("https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png") // replace image url
        .build()
    startTime = System.currentTimeMillis()
    nclient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            result(Pair(false , speed))
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                result(Pair(false , speed))
            } else {
                val input = response.body!!.byteStream()
                try {
                    val bos = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)
                    while (input.read(buffer) != -1) {
                        bos.write(buffer)
                    }
                    val docBuffer = bos.toByteArray()
                    fileSize = bos.size().toLong()
                } catch (e: IOException) {
                    input.close()
                    result(Pair(false , speed))
                }
                endTime = System.currentTimeMillis()
                // calculate how long it took by subtracting endTime from startTime
                val timeTakenMills = floor((endTime - startTime).toDouble()) // time taken in milliseconds
                val timeTakenInSecs = timeTakenMills / 1000 // divide by 1000 to get time in seconds
                val kilobytePerSec = (1024 / timeTakenInSecs).roundToInt()
                speed = (fileSize / timeTakenMills).roundToInt().toDouble().toString()

                status = if (kilobytePerSec <= POOR_BANDWIDTH) {
                    // slow connection
                    false
                } else if (kilobytePerSec <= AVERAGE_BANDWIDTH) {
                    // average connection
                    true
                } else if (kilobytePerSec <= GOOD_BANDWIDTH) {
                    // good connection
                    true
                } else {
                    // very good connection
                    true
                }

                result(Pair(status , speed))


            }
        }
    })

}




