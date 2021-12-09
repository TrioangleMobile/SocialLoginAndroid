package com.example.sociallogin

import android.app.Activity
import android.content.Intent

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


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
            onGoogleSignInListener!!.OnGSignInSuccess(account)
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
                onGoogleSignInListener!!.OnGSignInSuccess(account)
            }
        } catch (e: ApiException) {
            if (onGoogleSignInListener != null) {
                onGoogleSignInListener!!.OnGSignInError(
                    GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode())
                )
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    /**
     * Interface to listen the Google sign in
     */
    interface OnGoogleSignInListener {
        fun OnGSignInSuccess(googleSignInAccount: GoogleSignInAccount?)
        fun OnGSignInError(error: String?)
    }
}