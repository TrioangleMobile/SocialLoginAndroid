package com.trioangle.sociallogin.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import com.trioangle.sociallogin.SignInWithAppleResult
import com.trioangle.sociallogin.SignInWithAppleService
import com.trioangle.sociallogin.view.SignInWithAppleButton.Companion.SIGN_IN_WITH_APPLE_LOG_TAG

internal class SignInWebViewClient(

    private val attempt: SignInWithAppleService.AuthenticationAttempt,
    private val callback: (SignInWithAppleResult) -> Unit
) : WebViewClient() {

    // for API levels < 24
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return isUrlOverridden(view, Uri.parse(url))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return isUrlOverridden(view, request?.url)
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        println("Get Reu ${request!!.url.host}")
        return super.shouldInterceptRequest(view, request)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view!!.addJavascriptInterface(MyJavaScriptInterface(),"android")
        if ( url!!.equals(BaseUrl.appleCallbackUrl,true)) {
            view.loadUrl("javascript:android.showHTML(document.body.getElementsByTagName('pre')[0].innerHTML);")
        }
    }

    private fun isUrlOverridden(view: WebView?, url: Uri?): Boolean {
        return when {
            url == null -> {
                false
            }
            url.toString().contains("appleid.apple.com") -> {
                view?.loadUrl(url.toString())
                true
            }
            url.toString().contains(attempt.redirectUri) -> {
                Log.d(SIGN_IN_WITH_APPLE_LOG_TAG, "Web view was forwarded to redirect URI")

                val codeParameter = url.getQueryParameter("code")
                val stateParameter = url.getQueryParameter("state")

                when {
                    codeParameter == null -> {
                        callback(SignInWithAppleResult.Failure(IllegalArgumentException("code not returned")))
                    }
                    stateParameter != attempt.state -> {
                        callback(SignInWithAppleResult.Failure(IllegalArgumentException("state does not match")))
                    }
                    else -> {
                        callback(SignInWithAppleResult.Success(codeParameter))
                    }
                }

                true
            }
            else -> {
                false
            }
        }
    }

    open inner class MyJavaScriptInterface() {

        @JavascriptInterface
        fun showHTML(html: String) {
            try {
                callback(SignInWithAppleResult.ResponseSuccess(html))
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}
