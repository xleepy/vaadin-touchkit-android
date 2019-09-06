package com.example.vaadin_test

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlin.NullPointerException as NullPointerException1

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)
        val myWebView: WebView = findViewById(R.id.webview)
        val webViewClient = WebViewClient()
        myWebView.webViewClient = webViewClient
        myWebView.settings.javaScriptEnabled = true

        myWebView.loadUrl("http://10.0.2.2:8080")
    }
}
