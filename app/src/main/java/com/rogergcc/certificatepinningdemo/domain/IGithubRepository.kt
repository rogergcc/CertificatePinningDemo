package com.rogergcc.certificatepinningdemo.domain


interface IGithubRepository {

    suspend fun getUserDetails(user: String): GithubUserDomain?
}
