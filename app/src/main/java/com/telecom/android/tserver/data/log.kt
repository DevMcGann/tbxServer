package com.telecom.android.tserver.data

data class log(
        val appname : String,
        val datetime : String,
        val usuario : String,
        val description : String,
        val key:String? = "",
        val value: String? =""
)