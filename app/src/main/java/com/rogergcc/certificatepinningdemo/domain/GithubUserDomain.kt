package com.rogergcc.certificatepinningdemo.domain


data class GithubUserDomain(

    var login: String,
    var avatarUrl: String,
    var name: String,
    var bio: String,
    var publicRepos: Int,
    var followers: Int,
    var following: Int,
) {
}