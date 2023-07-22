package com.rogergcc.certificatepinningdemo.ui.presentation

import android.util.Log
import androidx.lifecycle.*
import com.rogergcc.certificatepinningdemo.data.cloud.GithubApi
import com.rogergcc.certificatepinningdemo.data.cloud.GsonProvider
import com.rogergcc.certificatepinningdemo.data.cloud.response.ErrorResponse
import com.rogergcc.certificatepinningdemo.data.cloud.response.GithubUserResponse
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import com.rogergcc.certificatepinningdemo.domain.IGithubRepository
import com.rogergcc.certificatepinningdemo.domain.Mappers.toGithubUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(
    private val repository: IGithubRepository,
) : ViewModel() {

    private var _userData = MutableLiveData<GithubUserDomain>()
    val userData: LiveData<GithubUserDomain> = _userData

    private var _errorNotFoundUser = MutableLiveData<ErrorResponse>()
    val userNotFound: LiveData<ErrorResponse> = _errorNotFoundUser

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun getUserData(profile: String) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val response = GithubApi.retrofitService.getUserDataResponseBody(profile)
//                if (response.isSuccessful){
//                    val responseBody = response.body()?.string()
//                    val gson = Gson()
//                    Log.d("MainViewModel", "getUserData responseBody: ${responseBody.toString()}")
//                    val responseType = object : TypeToken<GithubUserResponse>() {}.type
//                    val githubUserResponse = gson.fromJson<GithubUserResponse>(responseBody, responseType)
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
                            GithubUserResponse::class.java
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

    fun fetchUserData(profile: String) {
        viewModelScope.launch {
            val response = repository.getUserDetails(profile)
            response?.let {
                _userData.postValue(it)
            } ?: Log.d("MainViewModel", "getUserData: Response body is null")
        }
    }
}

class GithubViewModelFactory(private val repo: IGithubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IGithubRepository::class.java).newInstance(repo)
    }
}