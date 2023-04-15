package com.diegulog.intellifit.data

/**
 * Una clase genérica que contiene un valor con su carga..
 * @param <T>
 */
sealed class ResultOf<out T> {
    data class Success<out T>(val value: T) : ResultOf<T>()
    data class Failure(val throwable: Throwable?) : ResultOf<Nothing>()
    object Loading : ResultOf<Nothing>()
}
