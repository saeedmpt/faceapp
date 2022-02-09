package com.saeedmpt.chatapp.utility

import com.esotericsoftware.minlog.Log
import com.google.gson.Gson
import io.paperdb.Paper

object PaperBook {

    private const val KEY_TOKEN = "token"
    private const val KEY_IS_RATE = "isRate"

    private const val KEY_FIREBASE_TOKEN = "firebaseToken"
    private const val KEY_LAST_LINK_VOICE = "lastLinkVoice"
    private const val KEY_LAST_FRAGMENT_MAIN = "lastFragmentMain"

    @JvmStatic
    fun setToken(token: String) {
        Paper.book().write(KEY_TOKEN, token)
    }

    @JvmStatic
    val token: String
        get() {
            val data = Paper.book().read(KEY_TOKEN, "")
            Log.warn("PaperBook", Gson().toJson(data))
            return data
        }


    @JvmStatic
    fun setRate(value: Boolean) {
        Paper.book().write(KEY_IS_RATE, value)
    }

    @JvmStatic
    fun isRate(): Boolean {
        return Paper.book().read(KEY_IS_RATE, false)
    }


    @JvmStatic
    fun logout() {
        Paper.book().delete(KEY_TOKEN)
    }

    //settings


    @JvmStatic
    fun setLastLinkVoice(data: String) {
        Paper.book().write(KEY_LAST_LINK_VOICE, data)
        Log.warn("PaperBookSet", Gson().toJson(data))
    }

    @JvmStatic
    val lastLinkVoice: String
        get() {
            val data = Paper.book().read(KEY_LAST_LINK_VOICE, "")
            Log.warn("PaperBook", Gson().toJson(data))
            return data
        }

    @JvmStatic
    fun setFirebaseToken(data: String) {
        Paper.book().write(KEY_FIREBASE_TOKEN, data)
    }

    @JvmStatic
    val firebaseToken: String
        get() {
            return Paper.book().read(KEY_FIREBASE_TOKEN, "")
        }

    @JvmStatic
    fun setLastFragmentMain(data: Int) {
        Paper.book().write(KEY_LAST_FRAGMENT_MAIN, data)
        Log.warn("PaperBookSet", Gson().toJson(data))
    }

    @JvmStatic
    val lastFragmentMain: Int
        get() {
            val data = Paper.book().read(KEY_LAST_FRAGMENT_MAIN, 0)
            Log.warn("PaperBook", Gson().toJson(data))
            return data
        }


}
