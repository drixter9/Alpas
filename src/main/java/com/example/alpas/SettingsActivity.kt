package com.example.alpas

import android.content.Intent
import android.os.Bundle
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.UserAlpas
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    private lateinit var mUserDetails: UserAlpas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupActionbar()

        tv_edit.setOnClickListener {
            val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
            startActivity(intent)
        }
    }
    private fun setupActionbar() {
        setSupportActionBar(toolbar_settings)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_settings.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: UserAlpas){
        mUserDetails = user
        GlideLoader(this@SettingsActivity).loadUserImage(user.image, iv_user_photo)
        tv_name_settings.text = "${user.firstName} ${user.lastName}"
        tv_gender_settings.text = user.gender
        tv_email_settings.text = user.email
        tv_mobile_settings.text = "${user.mobile}"
        hideProgressDialog()
    }
    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
}