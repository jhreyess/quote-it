package com.quoteit.android.data.network.config

object SessionManager {
    private var ACCESS_TOKEN = "access"
    private var REFRESH_TOKEN = "refresh"

    fun setAccessToken(token: String) { ACCESS_TOKEN = token }
    fun setToken(token: String){ REFRESH_TOKEN = token }

    fun getAccessToken() = ACCESS_TOKEN
    fun getRefreshToken() = REFRESH_TOKEN
}