package com.example.alpas

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.UserAlpas
import com.example.alpas.utilsAlpas.GlideLoader
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.progress_diaglog.*
import kotlinx.android.synthetic.main.user_info_dialog.*

open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog
    private lateinit var mUserDialog: Dialog

    fun showErrorSnackBar( message: String, errorMessage: Boolean){
        val snackBar =
                Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_SHORT)
        val snackBarView = snackBar.view

        if(errorMessage){

            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@BaseActivity,
                            R.color.snack_bar_error_text
                    )
            )
        }
        else{
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@BaseActivity,
                            R.color.snack_bar_success_text
                    )
            )
        }
        snackBar.show()

    }
    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.progress_diaglog)
        mProgressDialog.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        mProgressDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        mProgressDialog.tv_progress_text.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
    fun doubelBackToExit(){
        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({doubleBackToExitPressedOnce=false},2000)
    }

    fun userInfo(userID: String){
        FirestoreClass().getUserDetailsChat(this@BaseActivity ,userID)
    }
    fun userInfoSuccess(userInfo: UserAlpas){
        mUserDialog = Dialog(this)
        mUserDialog.setContentView(R.layout.user_info_dialog)
        mUserDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mUserDialog.tv_user_name.text =" ${userInfo.firstName} ${userInfo.lastName}"
        mUserDialog.tv_user_info_email.text = userInfo.email
        mUserDialog.tv_user_info_gender.text = userInfo.gender
        GlideLoader(this).loadUserImage(
            userInfo.image,
            mUserDialog.iv_user_image
        )
        mUserDialog.show()
        mUserDialog.iv_user_info_close.setOnClickListener {
            mUserDialog.dismiss()
        }
        mUserDialog.btn_user_message.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS_CHAT,userInfo.uid)
            startActivity(intent)
        }
    }
}