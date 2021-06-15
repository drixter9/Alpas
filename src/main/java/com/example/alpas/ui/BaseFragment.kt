package com.example.alpas.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.alpas.ChatActivity
import com.example.alpas.Constants
import com.example.alpas.R
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.UserAlpas
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.progress_diaglog.*
import kotlinx.android.synthetic.main.user_info_dialog.*

open class BaseFragment : Fragment() {

    private lateinit var mProgressDialog: Dialog
    private lateinit var mUserDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun showProgressDialog(text: String) {

        mProgressDialog = Dialog(requireActivity())
        mProgressDialog.setContentView(R.layout.progress_diaglog)
        mProgressDialog.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        mProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mProgressDialog.tv_progress_text.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        if (::mProgressDialog.isInitialized) {
            mProgressDialog.dismiss()
        }
    }

    fun userInfo(userID: String) {
        FirestoreClass().getUserInfo(this@BaseFragment, userID)
    }

    fun userInfoSuccessFragment(userInfo: UserAlpas) {
        mUserDialog = Dialog(requireActivity())
        mUserDialog.setContentView(R.layout.user_info_dialog)
        mUserDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mUserDialog.tv_user_name.text = " ${userInfo.firstName} ${userInfo.lastName}"
        mUserDialog.tv_user_info_email.text = userInfo.email
        mUserDialog.tv_user_info_gender.text = userInfo.gender
        GlideLoader(requireActivity()).loadUserImage(
            userInfo.image,
            mUserDialog.iv_user_image
        )
        mUserDialog.show()
        mUserDialog.iv_user_info_close.setOnClickListener {
            mUserDialog.dismiss()
        }
        mUserDialog.btn_user_message.setOnClickListener {
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS_CHAT, userInfo.uid)
            startActivity(intent)
        }
    }
}