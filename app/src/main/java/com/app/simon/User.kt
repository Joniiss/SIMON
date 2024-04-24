package com.app.simon

import java.io.Serializable

data class User(
    val periodo: String,
    val curso: String,
    val nome: String,
    val materias: ArrayList<String>,
    var uid: String = "",
    var celular: String = "",
    var email: String = "",
    var horario: String = "",
    var predio: String = "",
    var sala: String = "",
    var status: Boolean = false
    //val materiasMonitor: List<String>?
) : Serializable