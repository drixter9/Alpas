package com.example.alpas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_forum.*
import kotlinx.android.synthetic.main.activity_search_user_chat.*
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*

class TermsAndConditionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        setupActionbar()

    }

    private fun setupActionbar() {
        setSupportActionBar(toolbar_terms_and_conditions_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_terms_and_conditions_activity.setNavigationOnClickListener { onBackPressed() }
    }
}