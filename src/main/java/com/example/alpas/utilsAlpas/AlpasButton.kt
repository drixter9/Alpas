package com.example.alpas.utilsAlpas

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class AlpasButton(context: Context, attrs: AttributeSet): AppCompatButton(context, attrs){
    init {
        applyFont ()
    }

    private fun applyFont() {
        val typeface : Typeface =
            Typeface.createFromAsset(context.assets,"OpenSans-Bold.ttf")
        setTypeface(typeface)
    }
}