/*
 *     Digital Patrol Guard
 *     ServiceGenerator.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.networking

import ai.digital.patrol.AppProvider
import ai.digital.patrol.helper.PreferenceHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    private val builder = Retrofit.Builder()
        .baseUrl( APIUrl.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private var retrofit = builder.build()
    private val logging = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    fun createService(): RestInterface {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging)
        }
        val accessToken = PreferenceHelper.appsPrefs(AppProvider.applicationContext()).getString("accessToken", null)
        if (accessToken != null) {
            val authInterceptor = AuthenticationInterceptor(accessToken)
            if (!httpClient.interceptors().contains(authInterceptor)) {
                httpClient.addInterceptor(authInterceptor)
            }
        }
        builder.client(httpClient.build())
        retrofit = builder.build()
        return retrofit.create(RestInterface::class.java)
    }
}