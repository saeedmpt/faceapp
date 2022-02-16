package com.ai.chatapp.utility

import android.util.Log
import com.ai.chatapp.component.Global
import com.ai.chatapp.net.RepositoryData.initConnection
import okhttp3.ResponseBody
import com.ai.chatapp.net.CounteryPresenterListener
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