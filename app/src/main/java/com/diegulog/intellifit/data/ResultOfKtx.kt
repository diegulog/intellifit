package com.diegulog.intellifit.data



/**
 * `true` if [ResultOf] is of type [Success] & holds non-null [Success.data].
 */
val ResultOf<*>.succeeded
    get() = this is ResultOf.Success && value != null


/**
 * Comprobar si Result es exitoso o fallido
 * @return true if Result.Success
 */
inline val <R> ResultOf<R>.isSuccessful: Boolean
    get() = this is ResultOf.Success && value != null

/**
 * Check whether Result is successful or failed
 * @return true if Result.Failure
 */
inline val <R> ResultOf<R>.isFailure: Boolean
    get() = this is ResultOf.Failure

inline val <R> ResultOf<R>.isLoading: Boolean
    get() = this is ResultOf.Loading
/**
 * @return the value or null if it is a Result.Failure
 */
inline val <R> ResultOf<R>.valueOrNull: R?
    get() = if (this is ResultOf.Success) value else null

/**
 * @return the value or throw an exception if failure
 */
inline val <R> ResultOf<R?>.valueOrThrow: R
    get() = if (this is ResultOf.Success) value!!
    else throw throwableOrNull ?: NullPointerException("value == null")

/**
 * @return throwable value or null of the Result<R>
 */
inline val <R> ResultOf<R>.throwableOrNull: Throwable?
    get() = if (this is ResultOf.Failure) throwable else null

/**
 * The block() will be called when it is a success
 */
inline fun <R> ResultOf<R>.onSuccess(block: (R) -> Unit) {
    if (this is ResultOf.Success) {
        block(value)
    }
}

/**
 * The block() will be called when it is a failure
 */
inline fun <R> ResultOf<R>.onFailure(block: (Throwable?) -> Unit) {
    if (this is ResultOf.Failure) {
        block(throwable)
    }
}

/**
 * The block() will be called when it is a loading
 */
inline fun <R> ResultOf<R>.onLoading(block: () -> Unit) {
    if (this is ResultOf.Loading) {
        block()
    }
}
/**
 * Map successful value into something else.
 * @return a new transformed Result.
 */
inline fun <T, R> ResultOf<T>.map(transform: (T) -> R): ResultOf<R> {
    return when (this) {
        is ResultOf.Failure -> this
        is ResultOf.Success -> ResultOf.Success(transform(value))
        is ResultOf.Loading -> this

    }
}

/**
 * Map Result successful value into something else.
 * @return a new transformed Result.
 */
inline fun <T, R> ResultOf<T>.flatMap(transform: (T) -> ResultOf<R>): ResultOf<R> {
    return when (this) {
        is ResultOf.Failure -> this
        is ResultOf.Success -> transform(value)
        is ResultOf.Loading -> this
    }
}

