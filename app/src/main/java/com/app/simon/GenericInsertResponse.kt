package com.app.simon

import com.google.gson.annotations.SerializedName

/***
 * Supondo que cada vez que vc
 * use uma Function que retorne um documentId (docId) no payload
 * como um Json, basta tratar como um objeto de resposta.
 * Isso organiza o código e pode ser reaproveitado para
 * qualquer function que retorne no payload um docId
 *
 */
class GenericInsertResponse {

    @SerializedName("nome")
    var nome: String? = null;

    @SerializedName("curso")
    var curso: String? = null;

    @SerializedName("periodo")
    var periodo: String? = null;

    @SerializedName("materias")
    var materias: String? = null;

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenericInsertResponse
        if (nome != other.nome) return false
        if (curso != other.curso) return false
        return true
    }

    override fun hashCode(): Int {
        var result = nome?.hashCode() ?: 0
        result = 31 * result + (nome?.hashCode() ?: 0)
        return result
    }
}