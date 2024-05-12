package com.app.simon

import java.time.LocalDateTime

data class logHorasData(
    val millis: Long = System.currentTimeMillis(),
    val monthValue: Int = LocalDateTime.now().monthValue,
    val uid: String = ""
)
