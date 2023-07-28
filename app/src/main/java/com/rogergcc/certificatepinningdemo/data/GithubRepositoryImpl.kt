package com.rogergcc.certificatepinningdemo.data

import android.util.Log
import com.rogergcc.certificatepinningdemo.core.ObjetsProviders
import com.rogergcc.certificatepinningdemo.core.ResourceState
import com.rogergcc.certificatepinningdemo.data.cloud.api.GithubApiService
import com.rogergcc.certificatepinningdemo.data.cloud.response.GithubUserResponse
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import com.rogergcc.certificatepinningdemo.domain.IGithubRepository
import com.rogergcc.certificatepinningdemo.domain.Mappers.toGithubUser
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import javax.net.ssl.SSLHandshakeException
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
                return ResourceState.Failure(Exception("User Not Found"))
            }

            val responseBody = response.body()
            if (responseBody == null) {
                return ResourceState.Failure(Exception("User Not Found body is null"))
            }

            val json = responseBody.string()
            if (!isSuccessResponse(json)) {
                return ResourceState.Failure(Exception("Parsing error"))
            }
            val successResponse =
                ObjetsProviders.gson.fromJson(json, GithubUserResponse::class.java)
            return ResourceState.Success(successResponse.toGithubUser())
        } catch (ex: CertificateExpiredException) {
            Log.e("GithubRepoImp", "CertificateExpiredException ")
//            return ResourceState.Failure(Exception("CertificateExpiredException"))
        } catch (ex: CertificateNotYetValidException) {
            Log.e("GithubRepoImp", "CertificateNotYetValidException ")
//            return ResourceState.Failure(Exception("CertificateNotYetValidException"))
        } catch (ex: SSLPeerUnverifiedException) {
            Log.e("GithubRepoImp", "SSLPeerUnverifiedException ")
//            return ResourceState.Failure(Exception("SSLPeerUnverifiedException"))
        } catch (ex: SSLHandshakeException) {
            Log.e("GithubRepoImp", "SSLHandshakeException ")
//            return ResourceState.Failure(Exception("SSLHandshakeException"))

        } catch (e: Exception) {
            Log.e("GithubRepoImp", "getUserDetails Generic Exception")
//            Log.e("GithubRepoImp", "getUserDetails l: ${e.localizedMessage}")
            e.printStackTrace()
            return ResourceState.Failure(Exception(e.message ?: "Unknown error"))
        }
        return ResourceState.Failure(Exception("Unknown error"))
    }

    private fun isSuccessResponse(json: String): Boolean {
        // Aquí debes implementar la lógica para determinar si el JSON es una SuccessResponse
        // por ejemplo, verificando si contiene ciertos campos específicos.
        // Puedes utilizar bibliotecas de parsing JSON como Gson o Moshi para facilitar esta verificación.
        // En este ejemplo, asumimos que es una SuccessResponse si contiene el campo "login".
        return json.contains("\"login\"")
    }


}