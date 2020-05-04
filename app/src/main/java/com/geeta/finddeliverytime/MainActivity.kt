package com.geeta.finddeliverytime

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private val myurl: String = "https://sameday.costco.com/store/costco/info?tab=delivery"
    //private var mediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView: TextView = findViewById(R.id.textView)
        val myWebView: WebView = findViewById(R.id.webView)
        var mediaPlayer = MediaPlayer.create(this, R.raw.sound1)
        //val timer = MyCounter(10000, 1000)


        // Enable java script in web view
        myWebView.settings.javaScriptEnabled = true
        // Enable zooming in web view
        myWebView.settings.setSupportZoom(true)
        // Enable disable images in web view
        myWebView.settings.blockNetworkImage = false
        myWebView.settings.loadsImagesAutomatically = true
        myWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        myWebView.settings.allowContentAccess = true
        myWebView.settings.setAppCacheEnabled(true)

        // load webview
        myWebView.loadUrl(myurl)

        // Set web view client
        myWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                // Page loading started
                toast("Page loading.")
                progressBar.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Page loading finished
                // Display the loaded page title in a toast message
                toast("Page loaded: ${view.title}")
                progressBar.visibility = View.GONE
                super.onPageFinished(view, url)


                // get html from myWebView and display time slot availability in textView
                Thread.sleep(9000)
                myWebView.evaluateJavascript(
                    "(function() { return ('<html>'+document.getElementsByClassName('ReactModal__Content ReactModal__Content--after-open icModal')[0].innerText+'</html>'); })();",
                    ValueCallback<String?> { html ->
                        Log.d("HTML", html)

                        if (html?.contains("Fast & Flexible") == true) {
                            textView.setText ("Time slot available see below!")
                            textView.setTextColor(Color.parseColor("#63982c"))
                            textView.blink(50)


                            // Play mediaPlayer for 20 secs
                            val timer = object: CountDownTimer(20000, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    mediaPlayer.start()
                                }

                                override fun onFinish() {
                                    mediaPlayer.stop()
                                }
                            }
                           timer.start()

                        }
                        else {
                            textView.setText ("Delivery times not available. ")
                            textView.setTextColor(Color.RED)
                            textView.blink(50)
                        }
                    })
            }
        }
    }

    // function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            // If web view have back history, then go to the web view back history
            webView.goBack()
            toast("Going to back history")
        } else {
            super.onBackPressed()
        }
    }

    fun onProgressChanged(view: WebView, newProgress: Int) {
        val progress = "$newProgress%"
        if (newProgress == 100) {
            val progress = "Page Loaded."
        }
    }

    //function to blink text in textView
    fun View.blink(
        times: Int = Animation.INFINITE,
        duration: Long = 50L,
        offset: Long = 20L,
        minAlpha: Float = 0.0f,
        maxAlpha: Float = 1.0f,
        repeatMode: Int = Animation.REVERSE
    ) {
        startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
            it.duration = duration
            it.startOffset = offset
            it.repeatMode = repeatMode
            it.repeatCount = times
        })
    }

}