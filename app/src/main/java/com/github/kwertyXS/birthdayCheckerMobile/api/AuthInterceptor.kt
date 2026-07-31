package com.github.kwertyXS.birthdayCheckerMobile.api

import com.github.kwertyXS.birthdayCheckerMobile.managers.TokenManager
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
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

    private val refreshApi = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
        .build()

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenManager.getAccessToken()
        val request = authorizedRequest(chain.request(), accessToken)
        val response = chain.proceed(request)

        if (response.code == 401 || response.code == 402) {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken != null) {
                response.close()
                val newAccessToken = refreshTokens(refreshToken)
                if (newAccessToken != null) {
                    tokenManager.saveAccessToken(newAccessToken)
                    val retryRequest = authorizedRequest(chain.request(), newAccessToken)
                    return chain.proceed(retryRequest)
                }
            }
            tokenManager.clear()
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

    private fun refreshTokens(refreshToken: String): String? {
        val url = "${NetworkModule.BASE_URL}/api/v1/refresh?token=$refreshToken"
        val body = ByteArray(0).toRequestBody(null)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        return try {
            refreshApi.newCall(request).execute().use { response ->
                if (response.code == 200) {
                    val json = JSONObject(response.body?.string()!!)
                    json.getString("access_token")
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }
}
