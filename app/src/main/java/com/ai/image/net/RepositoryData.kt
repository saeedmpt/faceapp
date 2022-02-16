package com.ai.chatapp.net

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.ai.chatapp.component.Global
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RepositoryData {
    @JvmStatic
    fun <T> initConnection(
        callMe: Call<T>,
        mListener: CounteryPresenterListener<T>
    ) {
        mListener.onLoading()
        if (networkConnection()) {
            Log.e("RepositoryData:call", callMe.request().url().toString())
            callMe.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    mListener.doneLoading();
                    if (response.body() != null) {
                        if (response.code() == 200 || response.code() == 201) {
                            Log.e("RepositoryData:Success", response.body().toString())
                            mListener.onSuccess(response.body()!!)
                        } else {
                            Log.e("RepositoryData:Retry:", response.body().toString())
                            mListener.onErrorBody(response.errorBody())
                            mListener.onRetry(response.body()!!)
                        }
                    } else {
                        mListener.onErrorBody(response.errorBody())
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    try {
                        Log.e("RepositoryData:Failure", t.message!!)
                        mListener.doneLoading();
                        mListener.onFailure(call, t)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        } else {
            mListener.onNoWifi()
        }
    }

    @JvmStatic
    fun <T> initConnectionList(
        position: Int,
        callMe: Call<T>,
        mListener: CounteryPresenterListener<T>
    ) {
        mListener.onLoading(position)
        if (networkConnection()) {
            Log.e("RepositoryData:call", callMe.request().url().toString())
            callMe.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    mListener.doneLoading(position)
                    if (response.body() != null) {
                        if (response.code() == 200 || response.code() == 201) {
                            Log.e("RepositoryData:Success", response.body().toString())
                            mListener.onSuccess(response.body()!!, position)
                        } else {
                            Log.e("RepositoryData:Retry:", response.body().toString())
                            mListener.onErrorBody(response.errorBody(), position)
                            mListener.onRetry(response.body()!!, position)
                        }
                    } else {
                        mListener.onErrorBody(response.errorBody(), position)
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e("RepositoryData:Failure", t.message!!)
                    mListener.doneLoading(position)
                    mListener.onFailure(call, t, position)
                }
            })
        } else {
            mListener.onNoWifi(position)
        }
    }

    private fun networkConnection(): Boolean {
        if (Global.context == null) {
            return true
        }
        val cm =
            Global.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiNetwork != null && wifiNetwork.isConnected) {
            return true
        }
        val mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (mobileNetwork != null && mobileNetwork.isConnected) {
            return true
        }
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

}