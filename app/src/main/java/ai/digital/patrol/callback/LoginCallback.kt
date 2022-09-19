package ai.digital.patrol.callback

import ai.digital.patrol.data.entity.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface LoginCallback : Callback<LoggedInUser?> {
    override fun onResponse(call: Call<LoggedInUser?>, response: Response<LoggedInUser?>)
    override fun onFailure(call: Call<LoggedInUser?>, t: Throwable)
}