package com.diegulog.intellifit.utils

import kotlin.math.min
import kotlin.math.roundToInt


fun <T> List<T>.reduceList(targetSize: Int): List<T> {
    if (targetSize < 2) {
        throw IllegalArgumentException("El tamaño objetivo debe estar entre 2 y el tamaño de la lista original")
    }

    if (targetSize == this.size) {
        return this // No se aplica reducción
    }
    //Queremos que la lista tenga siempre el primer y ultimo elemento
    val step = (this.size - 1).toDouble() / (targetSize - 1)
    return List(targetSize) { index ->
        val position = min((index * step).roundToInt(), this.lastIndex)
        this[position]
    }

}