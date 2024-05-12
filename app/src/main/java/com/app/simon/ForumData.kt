package com.app.simon

import java.io.Serializable
data class ForumData(
    var autor: String,
    var titulo: String,
    var texto: String,
    var materiaP: String,
    var qtdComent: Integer,
    var aprovado: String,
    var data: String,
    var id: String = ""
): Serializable
