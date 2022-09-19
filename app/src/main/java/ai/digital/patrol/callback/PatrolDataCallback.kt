package ai.digital.patrol.callback

import ai.digital.patrol.data.entity.LoggedInUser
import ai.digital.patrol.data.entity.PatrolData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface PatrolDataCallback : Callback<PatrolData?> {
    override fun onResponse(call: Call<PatrolData?>, response: Response<PatrolData?>)
    override fun onFailure(call: Call<PatrolData?>, t: Throwable)
}