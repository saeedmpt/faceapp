package com.ai.chatapp.ui.main.settings

import androidx.databinding.DataBindingUtil
import com.ai.chatapp.R
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import com.ai.chatapp.databinding.ActivityWebviewBinding
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class WebviewActivity : AppCompatActivity() {
    private var contInst: Context? = null
    private lateinit var binding: ActivityWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)
        contInst = this
        binding.webview.webChromeClient = WebChromeClient()
        binding.webview.isVerticalScrollBarEnabled = false
        binding.webview.isHorizontalScrollBarEnabled = false
        binding.webview.settings.javaScriptEnabled = true
        val linkFinal = intent.getStringExtra("link")
        binding.webview.loadUrl(linkFinal!!)
    }

    //font
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}