package com.rogergcc.certificatepinningdemo.data.cloud.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("documentation_url")
    val detailers: String? = "",
)