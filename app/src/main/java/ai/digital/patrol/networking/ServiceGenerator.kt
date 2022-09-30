/*
 *     Digital Patrol Guard
 *     ServiceGenerator.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.networking

import ai.digital.patrol.GuardTourApplication
import ai.digital.patrol.helper.PreferenceHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceGenerator {
    var gson: Gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    private val builder = Retrofit.Builder()
        .baseUrl( APIUrl.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))

    private var retrofit = builder.build()
    private val logging = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    fun createService(): RestInterface {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging)
        }
        val accessToken = PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext()).getString("apiKey", null)
        if (accessToken != null) {
            val authInterceptor = AuthenticationInterceptor(accessToken)
            if (!httpClient.interceptors().contains(authInterceptor)) {
                httpClient.addInterceptor(authInterceptor)
            }
        }
        httpClient.retryOnConnectionFailure(true)
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)

        builder.client(httpClient.build())
        retrofit = builder.build()
        return retrofit.create(RestInterface::class.java)
    }

}