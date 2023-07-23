package com.rogergcc.certificatepinningdemo.data.cloud

import com.rogergcc.certificatepinningdemo.data.cloud.response.GitHubReposResponse
import com.rogergcc.certificatepinningdemo.data.cloud.response.GithubUserResponse
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


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


