package com.telecom.android.tserver.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.gson.Gson
import com.telecom.android.tserver.data.Logger
import com.telecom.android.tserver.data.SharedPreference
import com.telecom.android.tserver.data.item
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*

class WebServerService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val sharedPreference = SharedPreference(this)
        val log = Logger(this) //objeto logger para escribir en el log(tserver.txt)
        val gson = Gson() //Esto es para leer un JSON y pasarlo a Objeto Java u otra clase de objeto

        embeddedServer(Jetty, 3100) {
            install(CORS) {
                allowCredentials = true
                allowNonSimpleContentTypes = true
                anyHost()
                method(HttpMethod.Head)
                header("appname")
                header("version")
                header("fecha")
                header("legajo")
                method(HttpMethod.Options)
                header(HttpHeaders.XForwardedProto)
            }
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }

            routing {

                get("/deleteStorage"){
                    println("paso la 51")
                    val appName: String? = call.request.header("appname")
                    val appVersion: String? = call.request.header("version")
                    val appFecha: String? = call.request.header("fecha")
                    val usuario: String? =  call.request.header("legajo")

                    if (appName != null || appVersion != null || appFecha != null || usuario != null ){
                        sharedPreference.screenInfoToPreference(appName!!, appVersion!!,appFecha!!)
                    }else{
                        call.respond("Error con los headers")
                        call.response.status(HttpStatusCode.BadRequest)
                    }

                    try {
                        sharedPreference.clearSharedPreference()
                        call.response.status(HttpStatusCode.OK)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: Delete  -  /storage  - OK " , null, null )

                    }catch (e:Exception){
                        call.response.status(HttpStatusCode.InternalServerError)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: Delete  -  /storage  - Failed Internal Server Error", null, null  )

                    }
                }

                get("/deleteStorage/{key}"){
                    val key = call.parameters["key"]
                    val appName: String? = call.request.header("appname")
                    val appVersion: String? = call.request.header("version")
                    val appFecha: String? = call.request.header("fecha")
                    val usuario: String? =  call.request.header("legajo")

                    try {
                        if (appName != null || appVersion != null || appFecha != null ){
                            sharedPreference.screenInfoToPreference(appName!!, appVersion!!,appFecha!!)
                        }else{
                            call.response.status(HttpStatusCode.BadRequest)
                        }

                        sharedPreference.removeValue(key!!)
                        call.response.status(HttpStatusCode.OK)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: Delete -  /storage/{key}  - OK", key, null  )

                    }catch (e:Exception){
                        call.response.status(HttpStatusCode.InternalServerError)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: Delete -  /storage/{key}  - Failed Internal Server Error", key, null  )
                    }

                }

                get("/storage"){

                    val appName: String? = call.request.header("appname")
                    val appVersion: String? = call.request.header("version")
                    val appFecha: String? = call.request.header("fecha")
                    val usuario: String? =  call.request.header("legajo")

                    if (appName != null || appVersion != null || appFecha != null ){
                        sharedPreference.screenInfoToPreference(appName!!, appVersion!!,appFecha!!)
                    }else{
                        call.response.status(HttpStatusCode.BadRequest)
                    }

                    try {
                        val listaMap : MutableMap<String,Any> = mutableMapOf()
                        val data = sharedPreference.getAll()

                        for (item in data!!){
                            listaMap[item.key] = item.value!!
                            //getAll: No elimina nada
                        }
                        if (listaMap != null) {
                            call.respond(listaMap)
                            log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage  - OK", null,null)

                        }else{
                            call.respond(false)
                            log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage  - OK (Empty)", null,null)
                        }
                    }catch(e:Exception){
                        call.response.status(HttpStatusCode.InternalServerError)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage  - Failed Internal Server Error", null,null)

                    }

                }

                get("/storage/app"){
                    val appName: String? = call.request.header("appname")
                    val listaMap : MutableMap<String,Any> = mutableMapOf()
                    val data = sharedPreference.getAll()
                    val appVersion: String? = call.request.header("version")
                    val appFecha: String? = call.request.header("fecha")
                    val usuario: String? =  call.request.header("legajo")

                    if (appName != null || appVersion != null || appFecha != null ){
                        sharedPreference.screenInfoToPreference(appName!!, appVersion!!,appFecha!!)
                    }else{
                        call.response.status(HttpStatusCode.BadRequest)
                    }

                    try{
                        for (item in data!!){
                            if (item.key.startsWith(appName.toString())){
                                listaMap[item.key] = item.value!!
                                sharedPreference.removeValue(item.key!!)
                            }
                        }
                        if (listaMap != null) {
                            call.respond(listaMap)
                            log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage/app  - OK", null, listaMap.toString())

                        }else{
                            call.respond(false)
                            log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage/app  - OK (empty)", null, null)
                        }
                    }catch(e:Exception){
                        call.response.status(HttpStatusCode.InternalServerError)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage/app  - Failed Internal Server Error", null, null)

                    }
                }

                get("/storage/{key}") {
                    val key = call.parameters["key"]
                    var data = sharedPreference.getFromPrefs(key!!)
                    val appName: String? = call.request.header("appname")
                    val appVersion: String? = call.request.header("version")
                    val appFecha: String? = call.request.header("fecha")
                    val usuario: String? =  call.request.header("legajo")

                    if (appName != null || appVersion != null || appFecha != null ){
                        sharedPreference.screenInfoToPreference(appName!!, appVersion!!,appFecha!!)
                    }else{
                        call.response.status(HttpStatusCode.BadRequest)
                    }

                    try {
                        if (data != null ){
                            if (data.startsWith(appName.toString())) {
                                call.respond(data!!)
                                sharedPreference.removeValue(key!!)
                            }else{
                                call.respond(data!!)
                                log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage/{key}  - OK", key!!, data!!)
                            }
                        }else{
                            call.respond(false)
                            log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage/{key}  - OK (empty)", key!!, data!!)
                        }
                    }catch(e:Exception){
                        call.response.status(HttpStatusCode.InternalServerError)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: get -  /storage/{key}  - Failed Internal Server Error", key!!, data!!)
                    }

                }

                post("/storage"){

                    val appName: String? = call.request.header("appname")
                    val appVersion: String? = call.request.header("version")
                    val appFecha: String? = call.request.header("fecha")
                    val usuario: String? =  call.request.header("legajo")

                    if (appName != null || appVersion != null || appFecha != null ){
                        sharedPreference.screenInfoToPreference(appName!!, appVersion!!,appFecha!!)
                    }else{
                        call.response.status(HttpStatusCode.BadRequest)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: Post -  /storage  - Failed Bad Request",null,null)
                    }

                    try {
                        var value = call.receive<String>();

                        //deserializar body
                        val objeto = gson.fromJson(value, item::class.java)

                        //crear el item a guardar en el Storage
                        var newKey : String
                        if (objeto.target != null){
                            newKey  = objeto.target +  "_" + objeto.key
                            sharedPreference.saveToPrefs(newKey!!, objeto.value!!)
                            call.response.status(HttpStatusCode.OK)
                            call.respond("Added key " + newKey!! + "value: " + objeto.value!! )
                            log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: Post -  /storage  - Ok",newKey!!, objeto.value!!)

                        }else{
                            sharedPreference.saveToPrefs(objeto.key!!,objeto.value!!)
                            call.response.status(HttpStatusCode.OK)
                            call.respond("Added key " + objeto.key!! + "value: " + objeto.value!! )
                            log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: Post -  /storage  - Ok",objeto.key!!,objeto.value!!)

                        }

                    }catch(e:Exception){
                        call.response.status(HttpStatusCode.InternalServerError)
                        log.logWrite(context = this@WebServerService,  appName!!, appFecha!!, usuario!!, "method: Post -  /storage  - Internal Server Error", null, null)
                    }

                }

                get("/logs"){
                    try {
                        val logList : MutableList<String> = mutableListOf()
                        val data = log.logRead()

                        for (item in data!!){
                            logList.add(item)
                        }

                        if (logList != null){
                            call.respond(logList!!)
                        }

                        call.response.status(HttpStatusCode.OK)
                    }catch (e:Exception){
                        call.response.status(HttpStatusCode.InternalServerError)
                    }
                }


/*                get("/logs/{key}"){
                    //TODO not yet
                }*/

                get("/clearLogs"){
                    try {
                        log.cleanLog()
                        call.response.status(HttpStatusCode.OK)
                    }catch (e:Exception){
                        call.respond("Error con los headers")
                        call.response.status(HttpStatusCode.InternalServerError)
                    }

                }

            }

        }.start(wait = false)
        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}