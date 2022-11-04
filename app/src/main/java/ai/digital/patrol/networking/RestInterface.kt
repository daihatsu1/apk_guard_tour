/*
 *     Digital Patrol Guard
 *     RestInterface.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.networking

import ai.digital.patrol.data.entity.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RestInterface {
    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("npk") npk: String?,
        @Field("password") password: String?
    ): Call<LoggedInUser?>?

    @GET("patroli/jadwalPatroli")
    fun getJadwal(): Call<List<Schedule?>>?

    @GET("patroli/shift")
    fun getShift(): Call<List<Shift?>>?

    @GET("patroli/dataPatroli")
    fun getPatrolData(): Call<List<Zone>>?

    @GET("patroli/getDataTemuan")
    fun getDataTemuan(): Call<List<Temuan>>?

    @Multipart
    @POST("patroli/dataTemuan")
    @JvmSuppressWildcards
    fun postReport(
        @PartMap params: Map<String, RequestBody>
    ): Call<Report>

    @GET("patroli/getPatrolActivity")
    fun getPatrolActivity(@Query("id_jadwal_patroli") idJadwal: String): Call<PatrolActivity>?

    @Multipart
    @JvmSuppressWildcards
    @POST("patroli/setPatrolActivity")
    fun setPatrolActivity(
        @PartMap params: Map<String, RequestBody>
    ): Call<PatrolActivity>
}