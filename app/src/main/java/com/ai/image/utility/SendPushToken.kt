package com.ai.image.utility

import android.util.Log
import com.ai.image.component.Global
import com.ai.image.net.RepositoryData.initConnection
import okhttp3.ResponseBody
import com.ai.image.net.CounteryPresenterListener
import retrofit2.Call

class SendPushToken(token: String?) {
    init {
        Log.i("SendPushToken: ", token!!)
        initConnection(
            Global.getApiService().getApi().profileFcmUpdate(
                token
            ), object : CounteryPresenterListener<ResponseBody> {
                override fun onSuccess(data: ResponseBody) {
                    Log.i("SendPushToken: ", "onSuccess")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.i("SendPushToken: ", "onFailure")
                }

                override fun onErrorBody(errorBody: ResponseBody?) {
                    Log.i("SendPushToken: ", "onErrorBody")
                }
            }
        )
    }
}