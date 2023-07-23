package com.rogergcc.certificatepinningdemo.data

import com.rogergcc.certificatepinningdemo.core.GsonProvider
import com.rogergcc.certificatepinningdemo.core.Resource
import com.rogergcc.certificatepinningdemo.data.cloud.GithubApiService
import com.rogergcc.certificatepinningdemo.data.cloud.response.GithubUserResponse
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import com.rogergcc.certificatepinningdemo.domain.IGithubRepository
import com.rogergcc.certificatepinningdemo.domain.Mappers.toGithubUser


/**
 * Created on julio.
 * year 2023 .
 */
class GithubRepositoryImpl(private val apiService: GithubApiService) : IGithubRepository {
    override suspend fun getUserDetails(user: String): Resource<GithubUserDomain> {

        try {
            val response = apiService.getUserDataResponseBody(user)

            if (!response.isSuccessful || response.code() != 200) {
                return Resource.Failure(Exception("User Not Found"))
            }

            val responseBody = response.body()
            if (responseBody == null) {
                return Resource.Failure(Exception("User Not Found body is null"))
            }

            val json = responseBody.string()
            if (!isSuccessResponse(json)) {
                return Resource.Failure(Exception("Parsing error"))
            }
            val successResponse =
                GsonProvider.gson.fromJson(json, GithubUserResponse::class.java)
            return Resource.Success(successResponse.toGithubUser())

        } catch (e: Exception) {
            return Resource.Failure(e)
        }

    }

    private fun isSuccessResponse(json: String): Boolean {
        // Aquí debes implementar la lógica para determinar si el JSON es una SuccessResponse
        // por ejemplo, verificando si contiene ciertos campos específicos.
        // Puedes utilizar bibliotecas de parsing JSON como Gson o Moshi para facilitar esta verificación.
        // En este ejemplo, asumimos que es una SuccessResponse si contiene el campo "login".
        return json.contains("\"login\"")
    }


}