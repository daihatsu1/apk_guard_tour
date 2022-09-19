/*
 *     Digital Patrol Guard
 *     RestInterface.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.networking

import ai.digital.patrol.data.entity.LoggedInUser
import ai.digital.patrol.data.entity.Report
import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.data.entity.Zone
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

    @GET("patroli/dataPatroli")
    fun getPatrolData(): Call<List<Zone>>?

    @Multipart
    @POST("patroli/dataTemuan")
    @JvmSuppressWildcards
    fun postReport(
        @PartMap params: Map< String, RequestBody>
    ): Call<Report>

}