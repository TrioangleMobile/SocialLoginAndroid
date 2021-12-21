package com.example.sociallogin

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager

class SignInWithAppleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    internal companion object {
        const val SIGN_IN_WITH_APPLE_LOG_TAG = "SIGN_IN_WITH_APPLE"
    }

    init {
        var applyLayout = resources.getIdentifier("sign_in_with_apple_button", "layout", "com.example.sociallogin")
        LayoutInflater.from(context).inflate(applyLayout, this, true)
    }

    private val textView: TextView = findViewById(resources.getIdentifier("textView", "id", "com.example.sociallogin"))

    init {
        var applyStyleable = resources.getIdentifier("SignInWithAppleButton", "styleable", "com.example.sociallogin")
        var applyStyle = resources.getIdentifier("SignInWithAppleButton", "style", "com.example.sociallogin")
        val attributes =
            context.theme.obtainStyledAttributes(attrs, applyStyleable as IntArray, 0, applyStyle)

        var applyStyleableBg = resources.getIdentifier("SignInWithAppleButton_android_background", "styleable", "com.example.sociallogin")
        var applyStyleableLeft = resources.getIdentifier("SignInWithAppleButton_android_drawableLeft", "styleable", "com.example.sociallogin")
        var applyStyleableText = resources.getIdentifier("SignInWithAppleButton_android_textColor", "styleable", "com.example.sociallogin")
        var applyStyleableTextType = resources.getIdentifier("SignInWithAppleButton_sign_in_with_apple_button_textType", "styleable", "com.example.sociallogin")
        var applyStyleableCorner = resources.getIdentifier("SignInWithAppleButton_sign_in_with_apple_button_cornerRadius", "styleable", "com.example.sociallogin")
        var applyDimenCorner = resources.getIdentifier("sign_in_with_apple_button_cornerRadius_default", "dimen", "com.example.sociallogin")

        // Style
        val background = attributes.getDrawable(applyStyleableBg)
        val icon = attributes.getDrawable(applyStyleableLeft)
        val textColor = attributes.getColorStateList(applyStyleableText)

        // Text type
        val text = attributes.getInt(
            applyStyleableTextType,
            SignInTextType.SIGN_IN.ordinal
        )

        // Corner radius
        val cornerRadius = attributes.getDimension(
            applyStyleableCorner,
            resources.getDimension(applyDimenCorner)
        )

        attributes.recycle()

        this.background = background?.mutate()
        (background as? GradientDrawable)?.cornerRadius = cornerRadius

        if (icon != null) {
            var applyDimen = resources.getIdentifier("sign_in_with_apple_button_textView_icon_verticalOffset", "dimen", "com.example.sociallogin")

            val iconVerticalOffset =
                resources.getDimensionPixelOffset(applyDimen)

            icon.setBounds(
                0,
                iconVerticalOffset,
                icon.intrinsicWidth,
                icon.intrinsicHeight + iconVerticalOffset
            )

            textView.setCompoundDrawablesRelative(icon, null, null, null)
        }

        textView.setTextColor(textColor)
        textView.text = resources.getString(SignInTextType.values()[text].text)
    }

    fun setUpSignInWithAppleOnClick(
        fragmentManager: FragmentManager,
        configuration: SignInWithAppleConfiguration,
        text: String,
        callback: (SignInWithAppleResult) -> Unit
    ) {
        val fragmentTag = "SignInWithAppleButton-$id-SignInWebViewDialogFragment"
        textView.text=text
        val service = SignInWithAppleService(fragmentManager, fragmentTag, configuration, callback)
        setOnClickListener { service.show() }
    }

    fun setUpSignInWithAppleOnClick(
        fragmentManager: FragmentManager,
        configuration: SignInWithAppleConfiguration,
        text:String,
        callback: SignInWithAppleCallback
    ) {
        setUpSignInWithAppleOnClick(fragmentManager,configuration,text, callback.toFunction())
    }
}
