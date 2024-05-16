package com.app.simon

import java.io.Serializable

data class MonitorData (
    var nome: String = "",
    var local: String = "",
    var horario: String,
    var predio: String,
    var sala: String,
    var foto: String,
    var celular: String,
    var email: String,
    var status: String
): Serializable

