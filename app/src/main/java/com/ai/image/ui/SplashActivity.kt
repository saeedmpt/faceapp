package com.ai.image.ui

import android.content.Context
import com.ai.image.utility.PageStatusView.pageStatusView
import com.ai.image.ui.base.BaseActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.ai.image.R
import android.content.Intent
import android.os.Handler
import android.view.View
import com.ai.image.databinding.ActivitySplashBinding
import com.ai.image.utility.MyConstants.ACTION_TYPE
import com.ai.image.utility.MyConstants.VALUE_TYPE

class SplashActivity : BaseActivity() {
    private var binding: ActivitySplashBinding? = null
    private lateinit var contInst: Context
    private var valueType: String? = null
    private var actionType: String? = null
    private var isDoneVideo = false
    private var isDoneData = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        contInst = this
        val intent = intent
        if (intent != null && intent.extras != null) {
            val extras = intent.extras
            valueType = extras!!.getString(VALUE_TYPE)
            actionType = extras.getString(ACTION_TYPE)
        }
        isDoneData = true
        videoPlayer()
    }

    private fun videoPlayer() {
        isDoneVideo = true
        checkIntent()
    }


    private fun pageStatus(vararg visibleView: View?) {
        //if show main view pass null for params
        val viewsGone = arrayOf<View>(
            binding!!.layoutWatinig.rlLoading,
            binding!!.layoutWatinig.rlNoWifi,
            binding!!.layoutWatinig.rlNoData,
            binding!!.layoutWatinig.rlNoData2,
            binding!!.layoutWatinig.rlNoData3,
            binding!!.layoutWatinig.rlNoData4,
            binding!!.layoutWatinig.rlPvLoading,
            binding!!.layoutWatinig.rlRetry
        )
        pageStatusView(visibleView, viewsGone)
    }

    private fun checkIntent() {
        Handler().postDelayed({
            if (isDoneVideo && isDoneData) {
                isDoneVideo = false
                isDoneData = false
                startActivity(
                    Intent(contInst, MainActivity::class.java)
                        .putExtra(ACTION_TYPE, actionType)
                        .putExtra(VALUE_TYPE, valueType)
                )
                finishAffinity()
            }
        }, 700)

    }
}