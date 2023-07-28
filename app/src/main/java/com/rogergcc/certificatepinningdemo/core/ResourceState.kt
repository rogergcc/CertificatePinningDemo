package com.rogergcc.certificatepinningdemo.core


/**
 * Created on julio.
 * year 2023 .
 */
sealed class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    data class Success<out T>(val data: T) : ResourceState<T>()
    data class Failure(val exception: Exception) : ResourceState<Nothing>()
}