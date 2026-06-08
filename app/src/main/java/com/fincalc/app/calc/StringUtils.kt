package com.fincalc.app.calc

/** Converte texto (com possíveis separadores .,) em Double. */
fun parseBRL(value: String): Double {
    if (value.isBlank()) return 0.0
    val sanitized = value.trim()
        .replace(".", "")
        .replace(',', '.')
    return sanitized.toDoubleOrNull() ?: 0.0
}
