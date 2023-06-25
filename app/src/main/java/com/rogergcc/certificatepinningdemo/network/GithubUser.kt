package com.rogergcc.certificatepinningdemo.network


data class GithubUser(

    var login: String,
    var avatarUrl: String,
    var name: String,
    var bio: String,
    var publicRepos: Int,
    var followers: Int,
    var following: Int,
) {
}