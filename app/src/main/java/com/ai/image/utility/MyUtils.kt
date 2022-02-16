package com.ai.image.utility

import kotlin.io.readText
import android.util.DisplayMetrics
import android.app.Activity
import android.widget.Toast
import okhttp3.ResponseBody
import org.json.JSONObject
import com.ai.image.R
import org.json.JSONException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import java.util.ArrayList

object MyUtils {
    fun getSizeScreen(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        return displayMetrics.widthPixels
    }

    @JvmStatic
    fun setToastMessageResourse(context: Context, toastMessageResourse: Int) {
        Toast.makeText(
            context,
            context.resources.getString(toastMessageResourse),
            Toast.LENGTH_SHORT
        ).show()
    }

    @JvmStatic
    fun getListImages(vararg data: String?): ArrayList<String> {
        val list = ArrayList<String>()
        for (value in data) {
            if (value != null && value.length > 0) {
                list.add(value)
            }
        }
        if (list.size == 0) {
            list.add("") //add for tumb string
        }
        return list
    }

    @JvmStatic
    fun setToastMessage(context: Context?, toastMessage: String) {
        Toast.makeText(context, toastMessage + "", Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun handelErrorBody(contInst: Context, errorBody: ResponseBody?): String {
        try {
            if (errorBody != null) {
                var jsonObj: JSONObject? = null
                jsonObj = JSONObject(errorBody.charStream().readText())
                val errorText = jsonObj.getString("error")
                setToastMessage(contInst, errorText)
                return errorText
            } else {
                setToastMessageResourse(contInst, R.string.error_body)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""
    }

    fun copy(contInst: Context, text: String?) {
        val clipboard = contInst.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(text, text)
        clipboard.setPrimaryClip(clip)
    }
}