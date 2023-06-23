package com.rogergcc.certificatepinningdemo.network

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("documentation_url")
    val urldetails: String? = "",
)