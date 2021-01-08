package com.telecom.android.tserver.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import java.io.*

class Logger(val context: Context) {

    fun logWrite(context: Context, appname: String, datetime: String, usuario: String, description: String, key:String?, value:String?) {
        val gson = Gson()
        val logItem = log(appname, datetime, usuario, description, key, value)

        val logItemJson :String = gson.toJson(logItem)
        try {

            val dir = File(context.filesDir, "logdir")
            if (!dir.exists()) {
                dir.mkdir()
                println("Creado")
            }
            var file = File(dir, "tserverlog.txt")
            val writer = FileWriter(file,true)
            writer.append(logItemJson);
            writer.append("\n")
            writer.flush();
            writer.close();

            println("FILE: " + file)

        } catch (e: IOException) {
            Log.e("Exception", "Error al escribir en el Archivo del Log: " + e.toString())
        }
    }


    fun logRead() : MutableList<String> {
        var logString : MutableList<String> = mutableListOf()
        try{
            val file = File(context.filesDir, "logdir/tserverlog.txt")
            if (file.exists()){
                val inputStream: InputStream = file.inputStream()
                val inputString = inputStream.bufferedReader().use { it.readText() }
                logString.add(inputString)
            }
        }catch (e: IOException) {
            Log.e("Exception", "Error al leer  el Archivo del Log: " + e.toString())
        }

        return logString

    }

    fun cleanLog(){
        try{
            val file = File(context.filesDir, "logdir/tserverlog.txt")
            val writer = FileWriter(file)
            writer.append("");
            writer.flush();
            writer.close();
            println("El Log fue limpiado!")
        }catch (e: IOException) {
            Log.e("Exception", "Error al limpiar en el Archivo del Log: " + e.toString())
        }


    }

}