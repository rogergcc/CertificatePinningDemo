package com.rogergcc.certificatepinningdemo.core

import com.rogergcc.certificatepinningdemo.ui.customs.ErrorTYpe


/**
 * Created on julio.
 * year 2023 .
 */
sealed class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    data class Success<out T>(val data: T) : ResourceState<T>()

    //    data class Failure(val exception: Exception) : ResourceState<Nothing>()
    data class Error<T>(
        val errorType: ErrorTYpe? = null,
    ) : ResourceState<T>() {
        override fun equals(other: Any?): Boolean {
            return false
        }

        override fun hashCode(): Int {
            return errorType?.hashCode() ?: 0
        }


    }
}