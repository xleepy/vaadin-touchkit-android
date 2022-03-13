package com.example.vaadin_test

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.os.Build
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.ValueCallback
import android.widget.Toast
import android.webkit.WebView
import android.content.ActivityNotFoundException


class MainActivity : AppCompatActivity() {

    // Localhost
    //private val webViewUrl = "https://25fab319.ngrok.io"

    private val webViewUrl = "https://vaadin-test-github.herokuapp.com/"
    private var mUploadMessage: ValueCallback<Uri>? = null
    private var uploadMessage: ValueCallback<Array<Uri>>? = null
    private val REQUEST_SELECT_FILE = 100
    private val FILECHOOSER_RESULTCODE = 1

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // https://stackoverflow.com/questions/5907369/file-upload-in-webview
    @SuppressLint("ObsoleteSdkInt")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return
                uploadMessage!!.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        intent
                    )
                )
                uploadMessage = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (uploadMessage == null)
                return
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            val result =
                if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        } else
            Toast.makeText(
                this,
                "Failed to Upload Image",
                Toast.LENGTH_LONG
            ).show()
    }

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
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        myWebView.webViewClient = webViewClient
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.allowFileAccess = true
        myWebView.settings.allowContentAccess = true

        myWebView.loadUrl(webViewUrl)
        myWebView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }
                println(filePathCallback)
                uploadMessage = filePathCallback

                val intent = fileChooserParams.createIntent()
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    Toast.makeText(
                        this@MainActivity,
                        "Cannot Open File Chooser",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }

                return true
            }
            // Need to accept permissions to use the camera
            override fun onPermissionRequest(request: PermissionRequest) {
                    print(request)
                    request.grant(request.resources)
                    this@MainActivity.runOnUiThread(Runnable {
                        if(request.origin.toString() == "file:///") {
                            request.grant(request.resources)
                        } else {
                            request.deny()
                        }
                    })
            }
        }
    }


}
