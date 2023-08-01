package com.rogergcc.certificatepinningdemo.ui.presentation

import androidx.lifecycle.*
import com.rogergcc.certificatepinningdemo.core.ResourceState
import com.rogergcc.certificatepinningdemo.domain.GithubUserDomain
import com.rogergcc.certificatepinningdemo.domain.IGithubRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(
    private val repository: IGithubRepository,
) : ViewModel() {


    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->

//        ResourceState.Failure(
////                        Exception(
////                            e.message ?: "Unknown error"
////                        )
////                    )
//            Exception(throwable.message ?: "Unknown error")
//        )
        throwable.printStackTrace()
    }

    private val _resultLiveData = MutableLiveData<ResourceState<GithubUserDomain>>()
    val resultLiveData: LiveData<ResourceState<GithubUserDomain>> get() = _resultLiveData



    fun fetchUserData(profile: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            _resultLiveData.postValue(ResourceState.Loading)// Emitimos el estado de carga antes
            val response = repository.getUserDetails(profile)

            _resultLiveData.postValue(response)  // Emitimos el resultado obtenido del Repository

        }

    }

//    fun fetchUserData(profile: String) {
//        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//
//            _resultLiveData.postValue(ResourceState.Loading)// Emitimos el estado de carga antes
//            try {
//                val response = repository.getUserDetails(profile)
//
//                _resultLiveData.postValue(response)  // Emitimos el resultado obtenido del Repository
//            } catch (e: Exception) {
//                Log.e(
//                    "MainViewModel",
//                    "fetchUserData: ${ResourceState.Failure(Exception(e.message ?: "Unknown error"))}"
//                )
//                _resultLiveData.postValue(
//                    ResourceState.Failure(
//                        Exception(
//                            e.message ?: "Unknown error"
//                        )
//                    )
//                )
//
//            }
//
//        }
//
//    }
}

class GithubViewModelFactory(private val repo: IGithubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IGithubRepository::class.java).newInstance(repo)
    }
}