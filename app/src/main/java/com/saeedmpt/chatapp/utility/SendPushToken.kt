package com.saeedmpt.chatapp.utility

import android.util.Log
import com.saeedmpt.chatapp.component.Global
import com.saeedmpt.chatapp.net.RepositoryData.initConnection
import com.saeedmpt.chatapp.net.RepositoryData
import okhttp3.ResponseBody
import com.saeedmpt.chatapp.net.CounteryPresenterListener
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