package com.rogergcc.certificatepinningdemo.domain

import com.rogergcc.certificatepinningdemo.core.ResourceState


interface IGithubRepository {

    suspend fun getUserDetails(user: String): ResourceState<GithubUserDomain>
}
