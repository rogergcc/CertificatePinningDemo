package com.rogergcc.certificatepinningdemo.ui.presentation

import android.util.Log
import androidx.lifecycle.*
import com.rogergcc.certificatepinningdemo.core.ObjetsProviders
import com.rogergcc.certificatepinningdemo.core.Resource
import com.rogergcc.certificatepinningdemo.data.cloud.api.GithubApiInstance
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

    private val _resultLiveData = MutableLiveData<Resource<GithubUserDomain>>()
    val resultLiveData: LiveData<Resource<GithubUserDomain>> get() = _resultLiveData


    fun getUserData(profile: String) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val response = GithubApiInstance.retrofitService.getUserDataResponseBody(profile)
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
                        val githubUserResponse = ObjetsProviders.gson.fromJson(
                            responseBody.string(),
                            GithubUserResponse::class.java
                        )
                        val githubUser = githubUserResponse.toGithubUser()

                        _userData.postValue(githubUser)
                    } ?: Log.d("MainViewModel", "getUserData: Response body is null")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse =
                        ObjetsProviders.gson.fromJson(errorBody, ErrorResponse::class.java)
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
        viewModelScope.launch(Dispatchers.IO) {

            _resultLiveData.postValue(Resource.Loading)// Emitimos el estado de carga antes
            try {
                val response = repository.getUserDetails(profile)

                _resultLiveData.postValue(response)  // Emitimos el resultado obtenido del Repository
            } catch (e: Exception) {
                Log.e(
                    "MainViewModel",
                    "fetchUserData: ${Resource.Failure(Exception(e.message ?: "Unknown error"))}"
                )
                _resultLiveData.postValue(Resource.Failure(Exception(e.message ?: "Unknown error")))

            }

        }

    }
}

class GithubViewModelFactory(private val repo: IGithubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IGithubRepository::class.java).newInstance(repo)
    }
}