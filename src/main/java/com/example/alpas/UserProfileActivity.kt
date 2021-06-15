package com.example.alpas

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alpas.firestore.FirestoreClass
import com.example.alpas.models.UserAlpas
import com.example.alpas.utilsAlpas.GlideLoader
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.iv_user_photo
import java.io.IOException
import java.util.*

class UserProfileActivity :  BaseActivity() , View.OnClickListener {

    private lateinit var mUserDetails: UserAlpas
    private var mSelectedImageFileUri : Uri? = null
    private var mUserProfileFileURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        et_first_name_profile.setText(mUserDetails.firstName)
        et_last_name_profile.setText(mUserDetails.lastName)
        et_email_profile.isEnabled = false
        et_email_profile.setText(mUserDetails.email)
        et_birthday_profile.isEnabled = false
        et_birthday_profile.setText(mUserDetails.birthday)

        if(mUserDetails.profileCompleted == 0) {
            tv_title_user.text = resources.getString(R.string.title_complete_profile)
            et_first_name_profile.isEnabled = false
            et_last_name_profile.isEnabled = false
        }
        else{
            setupActionbar()
            tv_title_user.text = resources.getString(R.string.title_edit_profile)

            GlideLoader(this@UserProfileActivity).loadUserImage(mUserDetails.image,iv_user_photo)

            if (mUserDetails.mobile!= 0L){
                et_mobile_number_profile.setText(mUserDetails.mobile.toString())
            }
            when (mUserDetails.gender) {
                Constants.MALE -> rb_male.isChecked = true
                Constants.FEMALE -> rb_female.isChecked =true
                else -> rb_prefer_not_to_say.isChecked =true
            }
        }

        iv_user_photo.setOnClickListener (this@UserProfileActivity)

        btn_save_profile.setOnClickListener{

            if(validateProfileDetails()) {

                showProgressDialog(resources.getString(R.string.please_wait))

                if (mSelectedImageFileUri != null) {
                    FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE)
                }
                else{
                    updateUserProfileDetails()
                }
            }
        }
    }
    private fun setupActionbar() {
        setSupportActionBar(toolbar_userEdit_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolbar_userEdit_activity.setNavigationOnClickListener{ onBackPressed()}
    }
    override fun onClick(v: View?) {
        if (v !=null){

            when (v.id){
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )== PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this@UserProfileActivity)
                    }
                    else{
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this@UserProfileActivity)
        }
        else{
            Toast.makeText(
                this,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE)    {
                if (data!=null){
                    try {
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserImage(mSelectedImageFileUri!!,iv_user_photo)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selector_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun validateProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_mobile_number_profile.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_mobile), true)
                false
            }
            else -> {
                true
            }
        }
    }
    fun  userProfileUpdateSuccessful(){
        Toast.makeText(
                this@UserProfileActivity,
                resources.getString(R.string.msg_profile_update_success),
                Toast.LENGTH_SHORT
        ).show()
        FirestoreClass().getUserDetails(this@UserProfileActivity)
    }

    fun userProfileUpdateSharePrefSuccessful(){
        hideProgressDialog()
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    fun imageUploadSuccess (imageURL:String){
        mUserProfileFileURL = imageURL
        updateUserProfileDetails()
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()

        val firstName = et_first_name_profile.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME]= firstName
        }
        val lastName = et_last_name_profile.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME]= lastName
        }

        val mobileNumber = et_mobile_number_profile.text.toString().trim { it <= ' ' }
        val gender = when {
            rb_male.isChecked -> Constants.MALE
            rb_female.isChecked -> Constants.FEMALE
            else -> Constants.PREFER_NOT_TO_SAY
        }
        if (mUserProfileFileURL.isNotEmpty()){
            userHashMap[Constants.IMAGE]= mUserProfileFileURL
        }
        if (mobileNumber.isNotEmpty() && mobileNumber!= mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]= mobileNumber.toLong()
        }
        if (gender.isNotEmpty() && gender!= mUserDetails.gender){
        userHashMap[Constants.GENDER]= gender
        }
        userHashMap[Constants.GENDER]= gender
        userHashMap[Constants.COMPLETE_PROFILE]=1
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }


}