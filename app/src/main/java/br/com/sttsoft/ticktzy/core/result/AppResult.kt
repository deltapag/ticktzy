package br.com.sttsoft.ticktzy.core.result

sealed class AppResult<out T> {
    data class Success<T>(val data: T): AppResult<T>()
    data class Failure(val error: ErrorModel): AppResult<Nothing>()
}