package com.rogergcc.certificatepinningdemo.data

import android.util.Log
import com.rogergcc.certificatepinningdemo.data.cloud.GithubApi
import com.rogergcc.certificatepinningdemo.data.cloud.GsonProvider
import com.rogergcc.certificatepinningdemo.data.cloud.response.GithubUserResponse
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import com.rogergcc.certificatepinningdemo.domain.IGithubRepository
import com.rogergcc.certificatepinningdemo.domain.Mappers.toGithubUser


/**
 * Created on julio.
 * year 2023 .
 */
class GithubRepositoryImpl : IGithubRepository {
    override suspend fun getUserDetails(user: String): GithubUserDomain? {
        try {
            val response = GithubApi.retrofitService.getUserDataResponseBody(user)

            if (!response.isSuccessful && response.body() == null) {
                // Si hay un error o la respuesta es nula, devolvemos una lista vacía
                return null
            }
            val responseBody = response.body()!!
            val githubUserResponse = GsonProvider.gson.fromJson(
                responseBody.string(),
                GithubUserResponse::class.java
            )
            return githubUserResponse.toGithubUser()
        } catch (e: Exception) {
            Log.e("MainViewModel", "Failed: ${e.message}")
            return null
        }
    }
}