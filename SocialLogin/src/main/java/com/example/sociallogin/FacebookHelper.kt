package com.example.sociallogin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.facebook.*

import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog

import org.json.JSONObject
import java.util.Arrays

object FacebookHelper {
    private val permissions: Collection<String> =
        Arrays.asList("public_profile ", "email", "user_birthday", "user_location")
    private var callbackManager: CallbackManager? = null
    private var loginManager: LoginManager? = null
    private var shareDialog: ShareDialog? = null
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var fbSignInListener: OnFbSignInListener? = null

    /**
     * Interface to listen the Facebook login
     */
    interface OnFbSignInListener {
        fun OnFbSignInComplete(graphResponse: GraphResponse?, error: String?)
    }

    fun initFacebook(){
        FacebookSdk.sdkInitialize(activity) // Facebook SDK Initialization
    }

    fun FacebookHelper(activity: Activity?, fbSignInListener: OnFbSignInListener?) {
        this.activity = activity
        this.fbSignInListener = fbSignInListener
    }

    fun FacebookHelper(fragment: Fragment?, fbSignInListener: OnFbSignInListener?) {
        this.fragment = fragment
        this.fbSignInListener = fbSignInListener
    }

    fun FacebookHelper(activity: Activity?) {
        shareDialog = ShareDialog(activity)
    }

    fun FacebookHelper(fragment: Fragment?) {
        shareDialog = ShareDialog(fragment)
    }


    fun connect() {
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()

        if (activity != null) loginManager!!.logInWithReadPermissions(
            activity,
            permissions
        ) else loginManager!!.logInWithReadPermissions(fragment, permissions)

        loginManager!!.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    if (loginResult != null) {
                        callGraphAPI(loginResult.getAccessToken())
                    }
                }

                override fun onCancel() {
                    fbSignInListener!!.OnFbSignInComplete(null, "User cancelled.")
                }

                override fun onError(exception: FacebookException) {
                    if (exception is FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut()
                        }
                    }
                    fbSignInListener!!.OnFbSignInComplete(null, exception.message)
                }
            })
    }

    private fun callGraphAPI(accessToken: AccessToken) {
        val request: GraphRequest = GraphRequest.newMeRequest(
            accessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                    fbSignInListener!!.OnFbSignInComplete(response, null)
                }
            })
        val parameters = Bundle()
        //Explicitly we need to specify the fields to get values else some values will be null.
        parameters.putString(
            "fields",
            "id,birthday,email,first_name,gender,last_name,link,location,name"
        )
        request.setParameters(parameters)
        request.executeAsync()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (callbackManager != null) callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * To share the details in facebook wall.
     *
     * @param title       of the content
     * @param description of the content
     * @param url         link to share.
     */
    fun shareOnFBWall(title: String?, description: String?, url: String?) {
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val linkContent: ShareLinkContent = ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(description)
                .setContentUrl(Uri.parse(url))
                .build()
            shareDialog!!.show(linkContent)
        }
    }

}