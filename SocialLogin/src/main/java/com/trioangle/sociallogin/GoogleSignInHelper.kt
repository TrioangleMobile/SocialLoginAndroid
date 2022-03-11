package com.example.sociallogin

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.trioangle.sociallogin.datamodels.AccountDetails


object GoogleSignInHelper {
    const val RC_SIGN_IN = 1008

    // GoogleSignInClient
    private var googleSignInClient: GoogleSignInClient? = null

    // Activity instance
    private var activity: Activity? = null

    /**
     * Google sign in Listener
     */
    private var onGoogleSignInListener: OnGoogleSignInListener? = null

    fun GoogleSignInHelper(activity: Activity?, onGoogleSignInListener: OnGoogleSignInListener?) {
        this.activity = activity
        this.onGoogleSignInListener = onGoogleSignInListener
    }

    /**
     * Connect to google
     */
    fun connect() {
        //Mention the GoogleSignInOptions to get the user profile and email.
        // Instantiate Google SignIn Client.
        googleSignInClient = GoogleSignIn.getClient(
            activity,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )
    }

    /**
     * Call this method in your onStart().If user have already signed in it will provide result directly.
     */
    fun onStart() {
        val account: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null && onGoogleSignInListener != null) {
            onGoogleSignInListener!!.OnGSignInSuccess(accountDetails(account))
        }
    }

    /**
     * To Init the sign in process.
     */
    fun signIn() {
        val signInIntent: Intent = googleSignInClient!!.getSignInIntent()
        activity!!.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * To signOut from the application.
     */
    fun signOut() {
        if (googleSignInClient != null) {
            googleSignInClient!!.signOut()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            // Signed in successfully
            if (onGoogleSignInListener != null) {
                onGoogleSignInListener!!.OnGSignInSuccess(accountDetails(account))
            }
        } catch (e: ApiException) {
            if (onGoogleSignInListener != null) {
                onGoogleSignInListener!!.OnGSignInError(
                    GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode())
                )
            }
        }
    }

    private fun accountDetails(account: GoogleSignInAccount): AccountDetails {
        var accountDetails = AccountDetails()

        accountDetails.apply {
            accountId = account.id!!
            accountFullName = account.displayName!!
            accountPhotoUrl = account.photoUrl.let { account.photoUrl.toString() }
            accountPhotoUrl = accountPhotoUrl.replace("s96-c", "s400-c")
            accountEmail = account.email!!
            val splitStr = accountFullName!!.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            accountFirstName = splitStr[0]
            accountLastName = ""
            for (i in 1 until splitStr.size) {
                accountLastName = accountLastName + " " + splitStr[i]
            }
            if (accountLastName == "") accountLastName = ""
        }

        return accountDetails
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    /**
     * Interface to listen the Google sign in
     */
    interface OnGoogleSignInListener {
        fun OnGSignInSuccess(accountDetails: AccountDetails?)
        fun OnGSignInError(error: String?)
    }
}