package com.example.alpas.utilsAlpas

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class AlpasEditTextRegular  (context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs){
    init {
        applyFont ()
    }

    private fun applyFont() {
        val typeface : Typeface =
            Typeface.createFromAsset(context.assets,"OpenSans-Regular.ttf")
        setTypeface(typeface)
    }

}