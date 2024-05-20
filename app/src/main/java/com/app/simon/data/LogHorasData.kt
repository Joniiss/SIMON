package com.app.simon.data

import java.time.LocalDateTime

data class LogHorasData(
    val millis: Long = System.currentTimeMillis(),
    val monthValue: Int = LocalDateTime.now().monthValue,
    val uid: String = ""
)
