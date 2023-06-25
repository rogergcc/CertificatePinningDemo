package com.rogergcc.certificatepinningdemo.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogergcc.certificatepinningdemo.network.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    private var _userData = MutableLiveData<GithubUser>()
    val userData: LiveData<GithubUser> = _userData

    private var _errorNotFoundUser = MutableLiveData<ErrorResponse>()
    val userNotFound: LiveData<ErrorResponse> = _errorNotFoundUser

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun getUserData(profile: String) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val response = GithubApi.retrofitService.getUserDataResponse(profile)
//                if (response.isSuccessful){
//                    val responseBody = response.body()?.string()
//                    val gson = Gson()
//                    Log.d("MainViewModel", "getUserData responseBody: ${responseBody.toString()}")
//                    val responseType = object : TypeToken<ResponseGithubUser>() {}.type
//                    val githubUserResponse = gson.fromJson<ResponseGithubUser>(responseBody, responseType)
//
//                    val githubUser = githubUserResponse.toGithubUser()
//
//                    githubUser.let { _userData.postValue(githubUser) }
//
//
//                }else{
//                    Log.d("MainViewModel", "getUserData errorBody: ${response.errorBody()?.string()}")
//                }
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val githubUserResponse = GsonProvider.gson.fromJson(
                            responseBody.string(),
                            ResponseGithubUser::class.java
                        )
                        val githubUser = githubUserResponse.toGithubUser()

                        _userData.postValue(githubUser)
                    } ?: Log.d("MainViewModel", "getUserData: Response body is null")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse =
                        GsonProvider.gson.fromJson(errorBody, ErrorResponse::class.java)
                    _errorNotFoundUser.postValue(errorResponse)
                    Log.d(
                        "MainViewModel",
                        "getUserData errorBody: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed: ${e.message}")
            }
        }
}