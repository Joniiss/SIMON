package com.app.simon.data

data class CommentData(
    var autor: String,
    var data: String,
    var monitor: String,
    var post: String,
    var qtdVotos: Int,
    var texto: String,
    var aprovado: String,
    var id: String = ""
)
