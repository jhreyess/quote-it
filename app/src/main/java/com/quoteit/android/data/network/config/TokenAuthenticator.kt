package com.quoteit.android.data.network.config

import com.quoteit.android.data.network.AuthenticationApi
import com.quoteit.android.data.network.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshResult = refreshToken()
        return if (refreshResult) {
            val newToken = SessionManager.getAccessToken()
            response.request().newBuilder()
                .header("x-access-token", newToken)
                .build()
        } else {
            null
        }

    }

    private fun refreshToken(): Boolean{
        val body = RefreshTokenRequest(SessionManager.getRefreshToken())
        val retrofitService = AuthenticationApi.retrofitService
        val result = runBlocking { retrofitService.refreshToken(body) }
        return if (result.success) {
            SessionManager.setAccessToken(result.token)
            true
        } else {
            false
        }
    }
}