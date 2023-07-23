package com.rogergcc.certificatepinningdemo.domain

import com.rogergcc.certificatepinningdemo.core.Resource


interface IGithubRepository {

    suspend fun getUserDetails(user: String): Resource<GithubUserDomain>
}
