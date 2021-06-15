package com.example.alpas.utilsAlpas

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class AlpasTextViewSemiBold (context: Context, attrs: AttributeSet):
    AppCompatTextView(context, attrs){
    init {
        applyFont ()
    }

    private fun applyFont() {
        val typeface : Typeface =
            Typeface.createFromAsset(context.assets,"OpenSans-SemiBold.ttf")
        setTypeface(typeface)
    }
}