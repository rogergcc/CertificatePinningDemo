package com.rogergcc.certificatepinningdemo.data.cloud.api

import com.rogergcc.certificatepinningdemo.BuildConfig
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GithubApiInstance {
    private const val BASE_URL = "https://api.github.com/"
    private const val USER_NOT_FOUND =
        "https://octodex.github.com/images/octocat-de-los-muertos.jpg"

    //error if not use
//    private val certPinner = CertificatePinner.Builder()
//        .add("*.github.com", "sha256/Jg78dOE+fydIGk19swWwiypUSR6HWZybfnJG/8G7pyM=rr")
//        .build()


    //sslabs A+
    //https://www.ssllabs.com/ssltest/analyze.html?d=api.github.com
    // Server Key and Certificate #1 not working 1UPHAdcUbUoOcd5rDTD/0oMSnngCU6YzXzpByO4CCp4=

    //2nd certificate works Jg78dOE+fydIGk19swWwiypUSR6HWZybfnJG/8G7pyM=
    private val certificatePinner: CertificatePinner by lazy {
        CertificatePinner.Builder()
            .add("*.github.com", "sha256/Jg78dOE+fydIGk19swWwiypUSR6HWZybfnJG/8G7pyM=")
            .build()
    }
//    private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
//        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
//    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }


    private val okHttpClient = OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .addInterceptor(loggingInterceptor)
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .writeTimeout(30, TimeUnit.SECONDS)
        .build()


    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    val retrofitService: GithubApiService by lazy {
        retrofit.create(GithubApiService::class.java)
    }


}