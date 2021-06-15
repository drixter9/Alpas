package com.example.alpas.utilsAlpas

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class AlpasRadioButton (context: Context, attrs: AttributeSet): AppCompatRadioButton(context, attrs){
    init {
        applyFont ()
    }

    private fun applyFont() {
        val typeface : Typeface =
                Typeface.createFromAsset(context.assets,"OpenSans-Bold.ttf")
        setTypeface(typeface)
    }
}