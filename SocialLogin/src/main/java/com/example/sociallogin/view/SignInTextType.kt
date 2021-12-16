package com.example.sociallogin

import androidx.annotation.StringRes

internal enum class SignInTextType(@StringRes val text: Int) {
    SIGN_IN(R.string.sign_in_with_apple_button_signInWithApple),
    CONTINUE(R.string.sign_in_with_apple_button_continueWithApple)
}