package com.francisco.weather.core.network

/**
 * Sealed hierarchy of errors that can arise from WeatherAPI calls.
 * Repositories wrap low-level exceptions into these types before returning Result<T>.
 */
sealed class WeatherError : Exception() {

    /** Network-level failure (no connectivity, timeout, etc.) */
    data class Network(override val cause: Throwable) : WeatherError() {
        override val message: String = "No se pudo conectar. Revisa tu conexión."
    }

    /** HTTP error returned by the server (4xx / 5xx). */
    data class Http(val code: Int, override val message: String) : WeatherError()

    /** Successful HTTP response but the result list / body was empty. */
    data object Empty : WeatherError() {
        override val message: String = "No se encontraron resultados."
    }

    /** Any other unexpected error. */
    data class Unknown(override val cause: Throwable) : WeatherError() {
        override val message: String = "Ocurrió un error inesperado."
    }
}
