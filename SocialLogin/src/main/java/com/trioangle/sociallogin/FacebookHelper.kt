package com.example.sociallogin

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
import com.trioangle.sociallogin.datamodels.AccountDetails
import org.json.JSONObject
import java.util.*

object FacebookHelper {
    private val permissions: Collection<String> =
        Arrays.asList("public_profile ", "email", "user_birthday", "user_location")
    private var callbackManager: CallbackManager? = null
    private var loginManager: LoginManager? = null
    private var shareDialog: ShareDialog? = null
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var fbSignInListener: OnFbSignInListener? = null
    private var facebookKey: String? = null

    /**
     * Interface to listen the Facebook login
     */
    interface OnFbSignInListener {
        fun OnFbSignInComplete(accountDetails: AccountDetails?, error: String?)
    }

    fun initFacebook(){
        FacebookSdk.sdkInitialize(activity) // Facebook SDK Initialization
    }

    fun FacebookHelper(
        activity: Activity?,
        fbSignInListener: OnFbSignInListener?,
        facebookKey: String
    ) {
        this.activity = activity
        this.fbSignInListener = fbSignInListener
        this.facebookKey = facebookKey
        FacebookSdk.setApplicationId(facebookKey)
    }

    fun FacebookHelper(
        fragment: Fragment?,
        fbSignInListener: OnFbSignInListener?,
        facebookKey: String
    ) {
        this.fragment = fragment
        this.fbSignInListener = fbSignInListener
        this.facebookKey = facebookKey
    }

    fun FacebookHelper(activity: Activity?, facebookKey: String) {
        shareDialog = ShareDialog(activity)
        this.facebookKey = facebookKey
    }

    fun FacebookHelper(fragment: Fragment?, facebookKey: String) {
        shareDialog = ShareDialog(fragment)
        this.facebookKey = facebookKey
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
                    fbSignInListener!!.OnFbSignInComplete(accountDetails(`object`!!), null)
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

    private fun accountDetails(jsonObject: JSONObject): AccountDetails {
        var accountDetails = AccountDetails()

        accountDetails.apply {
            accountId = jsonObject.optString("id")
            accountFullName = jsonObject.optString("name")
            accountPhotoUrl = getImageUrl(accountId!!)!!
            accountEmail = jsonObject.optString("email")
            if (accountFullName!!.isNotEmpty()) {
                var firstName = ""
                var lastName = ""
                val idx = accountFullName!!.lastIndexOf(' ')
                try {
                    firstName = accountFullName!!.substring(0, idx)
                    lastName = accountFullName!!.substring(idx + 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                accountFirstName = firstName
                accountLastName = lastName
            }
        }

        return accountDetails
    }

    private fun getImageUrl(fbID: String): String? {
        return "https://graph.facebook.com/" + fbID + "/picture"
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (callbackManager != null) callbackManager!!.onActivityResult(
            requestCode,
            resultCode,
            data
        )
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