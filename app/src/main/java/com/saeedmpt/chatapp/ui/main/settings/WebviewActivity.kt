package com.saeedmpt.chatapp.ui.main.settings

import com.saeedmpt.chatapp.utility.PaperBook.setRate
import com.saeedmpt.chatapp.utility.PaperBook
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import com.saeedmpt.chatapp.R
import android.view.ViewGroup
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.RatingBar
import android.content.Intent
import android.content.ActivityNotFoundException
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import com.saeedmpt.chatapp.databinding.ActivityWebviewBinding
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