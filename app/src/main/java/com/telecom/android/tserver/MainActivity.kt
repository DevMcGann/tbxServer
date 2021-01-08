package com.telecom.android.tserver


import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.telecom.android.tserver.service.WebServerService
import android.text.format.Formatter
import android.content.Context
import android.widget.Button
import android.widget.Toast
import com.telecom.android.tserver.data.Logger
import com.telecom.android.tserver.data.SharedPreference

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref : SharedPreference
    private val log = Logger(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, WebServerService::class.java))
        sharedPref = SharedPreference(this)

        val tInfo : TextView = findViewById(R.id.tAppName)
        val textView: TextView = findViewById(R.id.tIp)
        val logButton : Button = findViewById(R.id.btn_leerLog)
        val wipeLogButton : Button = findViewById(R.id.b_wipe)
        val wipeStorageButton : Button = findViewById(R.id.btn_wipeStorage)
        val logText : TextView = findViewById(R.id.t_log)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        textView.text = "$ipAddress"

        tInfo.text = infoEnPantalla()

        logButton.setOnClickListener(){
            logText.text = log.logRead().toString()
        }

        wipeLogButton.setOnClickListener(){
            log.cleanLog()
        }

        wipeStorageButton.setOnClickListener(){
            sharedPref.clearSharedPreference()
        }

    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(this@MainActivity, "Resumed", Toast.LENGTH_SHORT).show()
        val tInfo : TextView = findViewById(R.id.tAppName)
        tInfo.text = infoEnPantalla()
    }

    private fun infoEnPantalla() : String {
        sharedPref = SharedPreference(this)
        val informacion : String? = sharedPref.getScreenInfoPreference()
        if (informacion != null){
            return informacion!!
        }else{
            return "No hay ninguna info de App almacenada"
        }
    }
}