package com.example.pehelper.data.network

import android.annotation.SuppressLint
import com.example.pehelper.data.model.RefreshTokenModel
import com.example.pehelper.data.repository.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val networkModule = module {
    single {
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(get()))
            .authenticator(TokenAuthenticator(get(), get(named("refreshApi"))))
            .build()
    }

    single(named("refreshClient")) {
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    single(named("refreshApi")) {
        Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7131")
            .client(get(named("refreshClient")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PEAPI::class.java)
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7131")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(PEAPI::class.java)
    }
}

class AuthInterceptor(private val tokenStorage: TokenStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        tokenStorage.accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(requestBuilder.build())
    }
}

class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val refreshApi: PEAPI
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val oldToken = tokenStorage.accessToken
            val refreshToken = tokenStorage.refreshToken

            if (refreshToken == null || response.request.header("Authorization") != "Bearer $oldToken") {
                return null
            }

            val refreshResponse = runBlocking {
                try {
                    refreshApi.refresh(RefreshTokenModel(refreshToken))
                } catch (e: Exception) {
                    null
                }
            }

            if (refreshResponse != null && refreshResponse.isSuccessful) {
                val newTokens = refreshResponse.body()
                tokenStorage.accessToken = newTokens?.accessToken
                tokenStorage.refreshToken = newTokens?.refreshToken

                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens?.accessToken}")
                    .build()
            } else {
                tokenStorage.clearTokens()
                return null
            }
        }
    }
} 