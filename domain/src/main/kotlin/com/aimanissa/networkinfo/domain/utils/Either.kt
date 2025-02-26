package com.aimanissa.networkinfo.domain.utils

import arrow.core.Either
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Suppress("TooGenericExceptionCaught")
inline fun <reified T> attempt(func: () -> T): Either<Throwable, T> = try {
    Either.Right(func.invoke())
} catch (error: Throwable) {
    if (error is CancellationException) {
        throw error
    }
    Either.Left(error)
}

@Suppress("TooGenericExceptionCaught")
inline fun <LEFT, RIGHT, TYPE> Either<LEFT, RIGHT>.fold(
    onError: (LEFT) -> TYPE,
    onSuccess: (RIGHT) -> TYPE
): TYPE = when
(this) {
    is Either.Right -> onSuccess(value)
    is Either.Left -> onError(value)
}

fun <T> flowAttemptEmptyList(): Flow<Either<Throwable, List<T>>> = flow {
    emit(Either.Right(emptyList()))
}

fun <LEFT, RIGHT> RIGHT.toRightEither(): Either<LEFT, RIGHT> =
    Either.Right(this)

fun <LEFT, RIGHT> LEFT.toLeftEither(): Either<LEFT, RIGHT> =
    Either.Left(this)
