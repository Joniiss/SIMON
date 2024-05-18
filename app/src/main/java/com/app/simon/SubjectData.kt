package com.app.simon

import java.io.Serializable

data class SubjectData(
    val user: User,
    val materia: String
) : Serializable
