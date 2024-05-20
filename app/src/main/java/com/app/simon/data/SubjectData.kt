package com.app.simon.data

import java.io.Serializable

data class SubjectData(
    val user: UserData,
    val materia: String
) : Serializable
