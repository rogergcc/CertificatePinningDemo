package com.rogergcc.certificatepinningdemo.core


/**
 * Created on julio.
 * year 2023 .
 */
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val exception: Exception) : Resource<Nothing>()
}