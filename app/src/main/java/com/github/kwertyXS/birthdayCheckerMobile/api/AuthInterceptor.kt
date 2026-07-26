package com.github.kwertyXS.birthdayCheckerMobile.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {

    private val refreshApi = OkHttpClient()

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenManager.getAccessToken()
        val request = authorizedRequest(chain.request(), accessToken)
        val response = chain.proceed(request)

        if (response.code == 401) {
            tokenManager.clear()
        }

        if (response.code == 402) {
            response.close()
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken != null) {
                val newTokens = refreshTokens(refreshToken)
                if (newTokens != null) {
                    tokenManager.saveAccessToken(newTokens.accessToken)
                    tokenManager.saveRefreshToken(newTokens.refreshToken)
                    val retryRequest = authorizedRequest(chain.request(), newTokens.accessToken)
                    return chain.proceed(retryRequest)
                } else {
                    tokenManager.clear()
                }
            } else {
                tokenManager.clear()
            }
        }

        return response
    }

    private fun authorizedRequest(original: Request, token: String?): Request {
        return if (token != null) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
    }

    private fun refreshTokens(refreshToken: String): Tokens? {
        val json = JSONObject().apply {
            put("refreshToken", refreshToken)
        }
        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://birthdaychecker.onrender.com/auth/refresh")
            .post(body)
            .build()
        return try {
            val response = refreshApi.newCall(request).execute()
            if (response.code == 200) {
                val bodyString = response.body?.string()
                val json = JSONObject(bodyString!!)
                Tokens(
                    accessToken = json.getString("accessToken"),
                    refreshToken = json.getString("refreshToken"),
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }

    private data class Tokens(val accessToken: String, val refreshToken: String)
}
