package com.rogergcc.certificatepinningdemo.data.cloud

import com.google.gson.Gson
import com.rogergcc.certificatepinningdemo.BuildConfig
import com.rogergcc.certificatepinningdemo.data.cloud.response.GitHubReposResponse
import com.rogergcc.certificatepinningdemo.data.cloud.response.GithubUserResponse
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


object GithubApi {
    private const val BASE_URL = "https://api.github.com/"
    private const val USER_NOT_FOUND =
        "https://octodex.github.com/images/octocat-de-los-muertos.jpg"
    val retrofitService: GithubApiService by lazy {
        retrofit.create(GithubApiService::class.java)
    }

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

    private fun setupInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG)
            logging.level = (HttpLoggingInterceptor.Level.BODY)
        else
            logging.level = (HttpLoggingInterceptor.Level.NONE)
        return logging
    }


    private val okHttpClient = OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .addInterceptor(setupInterceptor())
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .writeTimeout(30, TimeUnit.SECONDS)
        .build()


    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
}

interface GithubApiService {
    @GET("/users/{profile}")
    fun getUserData(@Path("profile") profile: String):
            Call<GithubUserDomain>

    @GET("/users/{profile}")
    suspend fun getUsersData(@Path("profile") profile: String):
            GithubUserResponse

    //las respuestas varian segun el tipo de respuesta
    // Si no encuentra el usuario => {message: "Not Found", documentation_url: "https://docs.github.com/rest/reference/users#get-a-user"}
    //Caso ok {login: "rogergcc", id: 1068192, node_id: "MDQ6VXNlcjEwNjgxOTI=", avatar_url: "https://avatars.githubusercontent.com/u/1068192?v=4", gravatar_id: "", …}
    @GET("users/{profile}")
    suspend fun getUserDataResponseBody(@Path("profile") profile: String): Response<ResponseBody>

    @GET("users/{profile}")
    suspend fun getUserResponse(@Path("profile") profile: String): Response<GithubUserResponse>

    @GET("/repositories")
    suspend fun getRepositories(): Response<List<GitHubReposResponse>>
}

object GsonProvider {
    val gson: Gson = Gson()
}

