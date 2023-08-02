package com.rogergcc.certificatepinningdemo.data

import android.util.Log
import com.rogergcc.certificatepinningdemo.core.ObjetsProviders
import com.rogergcc.certificatepinningdemo.core.ResourceState
import com.rogergcc.certificatepinningdemo.data.cloud.api.GithubApiService
import com.rogergcc.certificatepinningdemo.data.cloud.response.GithubUserResponse
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import com.rogergcc.certificatepinningdemo.domain.IGithubRepository
import com.rogergcc.certificatepinningdemo.domain.Mappers.toGithubUser
import com.rogergcc.certificatepinningdemo.ui.customs.ErrorTYpe
import java.net.ConnectException
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException


/**
 * Created on julio.
 * year 2023 .
 */
class GithubRepositoryImpl(private val apiService: GithubApiService) : IGithubRepository {
    override suspend fun getUserDetails(user: String): ResourceState<GithubUserDomain> {

        try {
            val response = apiService.getUserDataResponseBody(user)

            if (!response.isSuccessful || response.code() != 200) {
//                return ResourceState.Failure(Exception("User Not Found"))
                return ResourceState.Error(ErrorTYpe.UNKNOWN)
            }

            val responseBody = response.body()
            if (responseBody == null) {
//                return ResourceState.Failure(Exception("User Not Found body is null"))
                return ResourceState.Error(ErrorTYpe.UNKNOWN)
            }

            val json = responseBody.string()
            if (!isSuccessResponse(json)) {
//                return ResourceState.Failure(Exception("Parsing error"))
                return ResourceState.Error(ErrorTYpe.UNKNOWN)
            }
            val successResponse =
                ObjetsProviders.gson.fromJson(json, GithubUserResponse::class.java)
            return ResourceState.Success(successResponse.toGithubUser())

        } catch (e: Exception) {
            Log.e("GithubRepoImp", "getUserDetails localizedMessage: ${e.message}")

            //e.printStackTrace()
            return when (e) {
                is ConnectException, is UnknownHostException -> {
                    Log.e("GithubRepoImp", "NetworkErrorException ")
                    ResourceState.Error(ErrorTYpe.NO_INTERNET) // error dado pin incorrecto
                }
                is SSLPeerUnverifiedException -> {
                    Log.e("GithubRepoImp", "SSLPeerUnverifiedException ")
                    ResourceState.Error(ErrorTYpe.NO_SSL_PINNING) // error dado pin incorrecto
                }
                else -> {
                    ResourceState.Error(ErrorTYpe.UNKNOWN)
                }
            }
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