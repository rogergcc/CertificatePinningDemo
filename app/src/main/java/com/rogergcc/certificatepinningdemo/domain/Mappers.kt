package com.rogergcc.certificatepinningdemo.domain

import com.rogergcc.certificatepinningdemo.data.cloud.response.GithubUserResponse


/**
 * Created on julio.
 * year 2023 .
 */
object Mappers {
    fun GithubUserResponse.toGithubUser(): GithubUserDomain = GithubUserDomain(
        this.login ?: "",
        this.avatarUrl ?: "",
        this.name ?: "",
        this.bio ?: "",
        this.publicRepos ?: 0,
        this.followers ?: 0,
        this.following ?: 0,
    )

}