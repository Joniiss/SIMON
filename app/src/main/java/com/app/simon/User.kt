package com.app.simon

import java.io.File
import java.io.Serializable

data class User(
    val periodo: String,
    val curso: String,
    var pNome: String = "",
    val nome: String,
    val materias: ArrayList<String>,
    var uid: String = "",
    var celular: String = "",
    var email: String = "",
    var horario: String = "",
    var predio: String = "",
    var sala: String = "",
    var status: String = "false",
    var foto: String = "",
    var monitor: String = "false",
    val materiasMonitor: ArrayList<String>
) : Serializable