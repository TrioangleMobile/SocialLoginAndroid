package com.example.socialloginintegration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sociallogin.FacebookHelper
import com.example.sociallogin.GoogleSignInHelper
import com.trioangle.sociallogin.datamodels.AccountDetails

class MainActivity : AppCompatActivity(), FacebookHelper.OnFbSignInListener,
    GoogleSignInHelper.OnGoogleSignInListener {

    lateinit var fbButton: Button
    lateinit var googleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Facebook login
        fbButton = findViewById(R.id.btn_fb)
        fbButton.setOnClickListener {
            fbButtonClicked()
        }

        //Google login
        googleButton = findViewById(R.id.btn_google)
        googleButton.setOnClickListener {
            googleButtonClicked()
        }

    }

    fun fbButtonClicked() {
        FacebookHelper.FacebookHelper(this, this, "4223568274395476")
        FacebookHelper.initFacebook()
        FacebookHelper.connect()
    }

    fun googleButtonClicked() {
        GoogleSignInHelper.GoogleSignInHelper(this, this)
        GoogleSignInHelper.connect()
        //GoogleSignInHelper.onStart() //already logged in
        GoogleSignInHelper.signIn()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Facebook login
        FacebookHelper.onActivityResult(requestCode, resultCode, data)

        //Google login
        GoogleSignInHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun OnGSignInSuccess(accountDetails: AccountDetails?) {
        accountDetails
    }

    override fun OnGSignInError(error: String?) {
        error
    }

    override fun OnFbSignInComplete(accountDetails: AccountDetails?, error: String?) {
        accountDetails
    }
}